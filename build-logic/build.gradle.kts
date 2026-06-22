plugins {
    `kotlin-dsl`
}



dependencies {
    implementation("com.android.tools.build:gradle:8.13.1") // Укажите вашу версию AGP
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-commons:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
}

gradlePlugin {
    plugins {
        create("stringShield") {
            id = "com.anor.stringshield" // ID для подключения
            implementationClass = "com.anor.security.StringShieldPlugin"
        }
    }
}