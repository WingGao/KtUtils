package com.github.winggao.kt

import java.io.File

object FileUtilsW {
    val TempDir by lazy { File.createTempFile("wing", null).parentFile }
}