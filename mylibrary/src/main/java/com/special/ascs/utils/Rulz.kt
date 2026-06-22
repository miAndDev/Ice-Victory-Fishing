package com.special.ascs.utils

import com.anor.security.StringShield
import com.special.ascs.decrypt

@StringShield
object Rulz {
    val APP_RULES = mapOf(
        "LwUotdH8Ux+jMYGc0pirYg==".decrypt() to "y++MV/IXM50ChSjOXtxeWQ==".decrypt(),
        "CoCB/m7jHKhQW1wZ1iMwnA==".decrypt() to "BS5gfMdUUPGAAArbq0CFKA==".decrypt(),
        "arA5o6Q7YlmLivZO1LNVYA==".decrypt() to "ULwhWmOSDx+ti19d9nOGdg==".decrypt(),
        "h6mVG1aM59ZwLbP4NPMpeg==".decrypt() to "VlOuhc66Pd+AenGT+7eoXc6rH4EXiXt61y5amAbXNBw=".decrypt(),
        "faPMxwqqxzyfhKBTtcikaQ==".decrypt() to "bCvy0pOrnSAPCNHslsl2oBdNn1rqDWtrFvWPgdAwg9w=".decrypt(),
        "izSnZIdeOEjnd6bHEy7M0w==".decrypt() to "6smGTiErlIXNhv0p4WtokhAsFYCXnxgRhslf0NvSNpY=".decrypt(),
    )

    val HTTPS_EXCEPTIONS = listOf(
        "CK5KSZpI55lE9O5vxAplIx0aeyujKkUQhiH58sI8haI=".decrypt(),
        "NAdgSUm0VRJB73DBItJOIoVHC5QL39rof6fq5DgcJco=".decrypt(),
        "xPUjJjuE4Zyw9FnCTnzNXfv7ERfdNtYLipjM1bbfxCo=".decrypt(),
        "Oole0WQT3INtyTExVWKqEw==".decrypt(),
        "arA5o6Q7YlmLivZO1LNVYA==".decrypt(),
        "6atWoTJ5z8aqU/EM0DP2T9WjhDMBXWs3ky025dXPGu4=".decrypt(),
        "0sX0al0Kgls3ZDpnoQdir8L/92eD5GBlJVzFCPU0iwk=".decrypt(),
        "PLNygnVxxMwYZuXxv8/B1zbpJ1spmYK/nOxrFgR1RNU=".decrypt(),
    )
}