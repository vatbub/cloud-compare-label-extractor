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

import java.io.RandomAccessFile

@Suppress("MemberVisibilityCanBePrivate")
abstract class CloudCompareObject(name: String?) {
    abstract fun fromFileNoChildren(
        randomAccessFile: RandomAccessFile,
        dataVersion: Int,
        flags: Int,
    )

    var name = name
        private set
    var uniqueID: UInt = 0.toUInt()
        private set

    var objectFlags: UInt = 0.toUInt()
        private set

    val metadata = mutableMapOf<String, Any>()

    @Suppress("UNREACHABLE_CODE")
    fun readHeader(randomAccessFile: RandomAccessFile, dataVersion: Int, @Suppress("UNUSED_PARAMETER") flags: Int) {
        require(dataVersion >= 20)
        uniqueID = randomAccessFile.readLEUInt()
        name = if (dataVersion < 22) {
            // old style
            val nameArray = ByteArray(256)
            randomAccessFile.read(nameArray)
            String(nameArray)
        } else {
            randomAccessFile.readQString()
        }

        objectFlags = randomAccessFile.readLEUInt()

        // meta data (dataVersion>=30)
        if (dataVersion >= 30) {
            // count
            val metaDataCount = randomAccessFile.readLEUInt()

            repeat(metaDataCount.toInt()) {
                // "key + value" pairs
                @Suppress("UNUSED_VARIABLE")
                val key = randomAccessFile.readQString()!!

                @Suppress("UNUSED_VARIABLE")
                val value: Any = TODO() // randomAccessFile.readNullTerminatedString()
                metadata[key] = value
            }
        }
    }
}
