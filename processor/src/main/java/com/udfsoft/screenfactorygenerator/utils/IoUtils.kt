package com.udfsoft.screenfactorygenerator.utils

import java.io.OutputStream

object IoUtils {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}