/*
 *  cloud-compare-label-extractor
 *
 *  Copyright (C) 2022 - 2023 Frederik Kammel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.vatbub.cloudcompare

import java.nio.ByteBuffer
import java.nio.ByteOrder

internal fun ByteArray.buffer(order: ByteOrder = ByteOrder.BIG_ENDIAN) = ByteBuffer.wrap(this).order(order)

internal val ByteBuffer.uint: UInt
    get() {
        // Note: UInt is always 32 bits (4 bytes) regardless of platform architecture
        // See https://kotlinlang.org/docs/basic-types.html#unsigned-integers
        val bytes = 4
        val bigEndianBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        bigEndianBuffer.putInt(this.int)
        bigEndianBuffer.rewind()

        // PaddedArray must be in big endian order
        val paddedArray = ByteArray(bytes)
        bigEndianBuffer.get(paddedArray, 0, bigEndianBuffer.remaining().coerceAtMost(4))

        return ((paddedArray[0].toUInt() and 0xFFu) shl 24) or
            ((paddedArray[1].toUInt() and 0xFFu) shl 16) or
            ((paddedArray[2].toUInt() and 0xFFu) shl 8) or
            (paddedArray[3].toUInt() and 0xFFu)
    }

internal val ByteBuffer.ulong: ULong
    get() {
        // Note: UInt is always 32 bits (4 bytes) regardless of platform architecture
        // See https://kotlinlang.org/docs/basic-types.html#unsigned-integers
        val bytes = 8
        val bigEndianBuffer = ByteBuffer.allocate(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val thisLong = this.long
        bigEndianBuffer.putLong(thisLong)
        bigEndianBuffer.rewind()

        // PaddedArray must be in little endian order
        val paddedArray = ByteArray(bytes)
        bigEndianBuffer.get(paddedArray, 0, bigEndianBuffer.remaining().coerceAtMost(bytes))

        return ((paddedArray[7].toULong() and 0xFFu) shl 56) or
            ((paddedArray[6].toULong() and 0xFFu) shl 48) or
            ((paddedArray[5].toULong() and 0xFFu) shl 40) or
            ((paddedArray[4].toULong() and 0xFFu) shl 32) or
            ((paddedArray[3].toULong() and 0xFFu) shl 24) or
            ((paddedArray[2].toULong() and 0xFFu) shl 16) or
            ((paddedArray[1].toULong() and 0xFFu) shl 8) or
            (paddedArray[0].toULong() and 0xFFu)
    }
