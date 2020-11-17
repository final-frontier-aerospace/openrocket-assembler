package com.ffaero.openrocketassembler.model

import java.nio.ByteBuffer
import com.ffaero.openrocketassembler.model.proto.SupportingFileOuterClass

class SupportingFile internal constructor(internal val data: SupportingFileOuterClass.SupportingFile) {
	public val path: String
		get() = data.path
	
	public val contents: ByteBuffer
		get() = data.content.asReadOnlyByteBuffer()
	
	override fun equals(other: Any?): Boolean {
		if (!(other is SupportingFile)) {
			return false
		}
		return data == other.data
	}

	override fun hashCode(): Int = data.hashCode()
}
