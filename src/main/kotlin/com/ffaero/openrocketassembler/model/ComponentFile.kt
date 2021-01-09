package com.ffaero.openrocketassembler.model

import java.io.File

class ComponentFile(val id: Int, file: File) : File(file.absolutePath) {
}
