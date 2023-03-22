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

import java.io.EOFException
import java.io.RandomAccessFile
import java.nio.ByteOrder.BIG_ENDIAN
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.charset.Charset

fun RandomAccessFile.readLEInt(): Int {
    val byteArray = ByteArray(4)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(LITTLE_ENDIAN).int
}

fun RandomAccessFile.readLEUInt(): UInt {
    val byteArray = ByteArray(4)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(LITTLE_ENDIAN).uint
}

fun RandomAccessFile.readBEUInt(): UInt {
    val byteArray = ByteArray(4)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(BIG_ENDIAN).uint
}

fun RandomAccessFile.readLEULong(): ULong {
    val byteArray = ByteArray(8)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(LITTLE_ENDIAN).ulong
}

fun RandomAccessFile.readLEDouble(): Double {
    val byteArray = ByteArray(8)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(LITTLE_ENDIAN).double
}

fun RandomAccessFile.readLEFloat(): Float {
    val byteArray = ByteArray(4)
    if (read(byteArray) < 0) throw EOFException()

    return byteArray.buffer(LITTLE_ENDIAN).float
}

fun RandomAccessFile.readQString(): String? {
    val length = readBEUInt()
    if (length.toLong() == 0xFFFFFFFF) return null
    val byteArray = ByteArray(length.toInt())
    read(byteArray)
    return String(byteArray, Charset.forName("UTF-16"))
}

internal fun RandomAccessFile.readClassId(binaryVersion: Int): CloudCompareClassId {
    // class ID (on 32 bits between version 2.0 and 3.3, then 64 bits from version 3.4)
    val classId = if (binaryVersion < 34) {
        readLEUInt().toULong().toLong()
    } else {
        readLEULong().toLong()
    }

    return CloudCompareClassId.values().first { it.longValue == classId }
}

fun RandomAccessFile.uByteArrayFromFile(dataVersion: Int) = genericArrayFromFile(
    dataVersion,
    { randomAccessFile ->
        randomAccessFile.read().toUByte()
    },
    {
        require(it.size == 1)
        it.first()
    },
)

inline fun <reified ElementType, reified ComponentType> RandomAccessFile.genericArrayFromFile(
    dataVersion: Int,
    componentReader: (RandomAccessFile) -> ComponentType,
    elementConverter: (Array<ComponentType>) -> ElementType,
): Array<ElementType> {
    val header = readArrayHeader(dataVersion)

    return Array(header.elementCount.toInt()) {
        val components = Array(header.componentCount.toInt()) {
            componentReader(this)
        }
        elementConverter(components)
    }
}

fun RandomAccessFile.readArrayHeader(dataVersion: Int): ArrayHeader {
    require(dataVersion >= 20)
    return ArrayHeader(read().toUByte(), readLEUInt())
}

data class ArrayHeader(val componentCount: UByte, val elementCount: UInt)
