package com.ffaero.openrocketassembler.model

import java.io.File

class ComponentFile(public val id: Int, file: File) : File(file.getAbsolutePath()) {
}
