package com.anor.security

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.*
import java.io.File

abstract class GenerateShieldSourcesTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val folder = File(outputDir.get().asFile, "com/anor/security")
        folder.deleteRecursively()
        folder.mkdirs()

        File(folder, "StringShield.kt").writeText("""
            package com.anor.security
            @Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
            @Retention(AnnotationRetention.BINARY)
            annotation class StringShield
        """.trimIndent())

        File(folder, "AesDecryptor.kt").writeText("""
            package com.anor.security
            import android.util.Base64
            import javax.crypto.Cipher
            import javax.crypto.spec.SecretKeySpec
            
            object AesDecryptor {
                private const val SECRET_KEY = "1234567890123456" 
                private const val ALGORITHM = "AES"
                private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
            
                @JvmStatic
                fun decrypt(encrypted: String): String {
                    return try {
                        val key = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
                        val cipher = Cipher.getInstance(TRANSFORMATION)
                        cipher.init(Cipher.DECRYPT_MODE, key)
                        val decoded = Base64.decode(encrypted, Base64.DEFAULT)
                        String(cipher.doFinal(decoded))
                    } catch (e: Exception) { "" }
                }
            }
        """.trimIndent())
    }
}

class StringShieldPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        val generatedDir = project.layout.buildDirectory.dir("generated/source/stringshield")

        val generateTask = project.tasks.register("generateShieldSources", GenerateShieldSourcesTask::class.java) {
            outputDir.set(generatedDir)
        }

        androidComponents.onVariants { variant ->
            if (project.plugins.hasPlugin("com.android.library")) {
                variant.sources.java?.addGeneratedSourceDirectory(
                    generateTask,
                    GenerateShieldSourcesTask::outputDir,
                )
                variant.sources.kotlin?.addGeneratedSourceDirectory(
                    generateTask,
                    GenerateShieldSourcesTask::outputDir,
                )
            }

            variant.instrumentation.transformClassesWith(
                StringShieldFactory::class.java,
                InstrumentationScope.PROJECT,
            ) {}
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }

        project.tasks.configureEach(object : Action<Task> {
            override fun execute(task: Task) {
                if (!project.plugins.hasPlugin("com.android.library")) return

                if (task.name.contains("ksp") && task.name.contains("Kotlin")) {
                    task.dependsOn(generateTask)
                }
            }
        })
    }
}

abstract class StringShieldFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(ctx: ClassContext, next: ClassVisitor): ClassVisitor {
        return StringShieldClassVisitor(Opcodes.ASM9, next, ctx.currentClassData.className)
    }
    override fun isInstrumentable(data: ClassData): Boolean {
        return !data.className.startsWith("com.anor.security.AesDecryptor")
    }
}

class StringShieldClassVisitor(api: Int, next: ClassVisitor, private val className: String) : ClassVisitor(api, next) {
    private var isShielded = false
    private val annotationDescriptor = "Lcom/anor/security/StringShield;"
    private val decryptorClass = "com/anor/security/AesDecryptor"

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        if (descriptor == annotationDescriptor && className != decryptorClass) isShielded = true
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (isShielded) EncryptionMethodVisitor(api, mv) else mv
    }
}

class EncryptionMethodVisitor(api: Int, mv: MethodVisitor) : MethodVisitor(api, mv) {
    override fun visitLdcInsn(value: Any?) {
        if (value is String && value.isNotEmpty()) {
            try {
                val encrypted = PluginCryptoUtil.encrypt(value)
                super.visitLdcInsn(encrypted)
                super.visitMethodInsn(
                    Opcodes.INVOKESTATIC, "com/anor/security/AesDecryptor",
                    "decrypt", "(Ljava/lang/String;)Ljava/lang/String;", false
                )
            } catch (e: Exception) { super.visitLdcInsn(value) }
        } else { super.visitLdcInsn(value) }
    }
}
