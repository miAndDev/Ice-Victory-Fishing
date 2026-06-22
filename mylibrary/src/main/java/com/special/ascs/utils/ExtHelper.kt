package com.special.ascs.utils

import com.anor.security.StringShield
import com.special.ascs.decrypt
import com.special.ascs.utils.Rulz.HTTPS_EXCEPTIONS

@StringShield
class ExtHelper() {
    fun isHttpsAccordingToRules(string: String): Boolean =
        (string.startsWith("BejmjEFmm2ieU9ohQHWFUw==".decrypt()) || string.startsWith("KkBqjVS1A/BY5zqQ6tj54A==".decrypt())) && HTTPS_EXCEPTIONS.none { string.startsWith(it) }


}