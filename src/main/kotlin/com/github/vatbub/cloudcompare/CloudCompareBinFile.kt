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

import com.github.vatbub.cloudcompare.CloudCompareBinFile.DeserializationFlags.*
import com.github.vatbub.cloudcompare.CloudCompareBinFile.SerializationPrecision.*
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset

class CloudCompareBinFile(private val location: File) {
    lateinit var root: ccHObject
        private set

    init {
        parse()
    }

    private fun parse() {
        RandomAccessFile(location, "r").use { randomAccessFile ->
            val magicBytes = ByteArray(3) // Read first 3 bytes, must be CCB
            randomAccessFile.read(magicBytes)
            val magicString = String(magicBytes)
            require(magicString == "CCB") { "Magic string must be 'CCB' but was '$magicString'" }

            val loadFlagsString = String(byteArrayOf(randomAccessFile.readByte()), Charset.forName("UTF-8"))
            val loadFlags = loadFlagsString.toInt()
            require(loadFlags <= 8) { "Invalid file header (4th byte is $loadFlagsString)" }

            val binVersion = randomAccessFile.readLEInt()
            require(binVersion >= 20) { "Bin version must be at least 20 but was $binVersion" }

            val coordsFormat =
                if (loadFlags and DF_POINT_COORDS_64_BITS.intValue > 0) double else float
            val scalarFormat =
                if (loadFlags and DF_SCALAR_VAL_32_BITS.intValue > 0) float else double
            println("[BIN] Version ${binVersion / 10}.${binVersion % 10} (coords: $coordsFormat / scalar: $scalarFormat)")

            // we read first entity type
            val classId = randomAccessFile.readClassId(binVersion)
            require(classId != CloudCompareClassId.OBJECT) { "First entity must not be an object" }

            root = ccHObject.create(classId)

            if (classId == CloudCompareClassId.CUSTOM_H_OBJECT) {
                println("Custom H object is read")
                // store seeking position
                val originalPointer = randomAccessFile.filePointer
                // we need to load it as plain ccCustomHobject
                root.fromFileNoChildren(randomAccessFile, binVersion, loadFlags) // this will load it
                randomAccessFile.seek(originalPointer) // reseek back the file

                val metadataClassId = root.metadata["class_name"].toString()
                val pluginId = root.metadata["plugin_name"].toString()

                // try to get a new object from external factories
                val newChild = ccHObject.create(pluginId, metadataClassId)
                requireNotNull(newChild) { "CC_FERR_FILE_WAS_WRITTEN_BY_UNKNOWN_PLUGIN" }
                root = newChild
            }

            root.fromFile(randomAccessFile, binVersion, loadFlags)
        }
    }

    enum class DeserializationFlags(val intValue: Int) {
        DF_POINT_COORDS_64_BITS(1),
        DF_SCALAR_VAL_32_BITS(2),
    }

    @Suppress("EnumEntryName")
    enum class SerializationPrecision {
        double, float // ktlint-disable enum-entry-name-case
    }
}
