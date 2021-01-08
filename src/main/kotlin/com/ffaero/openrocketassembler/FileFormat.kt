package com.ffaero.openrocketassembler

import com.google.protobuf.ByteString

object FileFormat {
	public const val extension = "orka"
	public const val version = 1
	public val emptyORK = ByteString.copyFrom(byteArrayOf(
		 0x1F, -0x75,  0x08,  0x00,  0x77,  0x19, -0x0A,  0x5F,  0x00,  0x03, -0x4D, -0x4F, -0x51, -0x38, -0x33,
		 0x51,  0x28,  0x4B,  0x2D,  0x2A, -0x32, -0x34, -0x31, -0x4D,  0x55,  0x32, -0x2C,  0x33,  0x50,  0x52,
		 0x48, -0x33,  0x4B, -0x32,  0x4F, -0x37, -0x34,  0x4B, -0x49,  0x55,  0x0A,  0x0D,  0x71, -0x2D, -0x4B,
		 0x50, -0x4E, -0x49, -0x1D, -0x1B, -0x4E, -0x37,  0x2F,  0x48, -0x33,  0x2B, -0x36,  0x4F, -0x32,  0x4E,
		 0x2D,  0x41,  0x55,  0x6D,  0x67,  0x03,  0x11, -0x4B, -0x4D,  0x29,  0x2E,  0x4D,  0x4A, -0x32, -0x31,
		 0x2D, -0x38, -0x31,  0x4B, -0x33,  0x2B,  0x29,  0x06,  0x72,  0x4B,  0x12, -0x2D,  0x53, -0x0B, -0x13,
		 0x6C, -0x0C, -0x2F, -0x3C, -0x0B,  0x61, -0x16, -0x0B,  0x11,  0x46,  0x02,  0x2D,  0x00,  0x00, -0x43,
		 0x0C,  0x20, -0x5C, -0x77,  0x00,  0x00,  0x00
	))
}
