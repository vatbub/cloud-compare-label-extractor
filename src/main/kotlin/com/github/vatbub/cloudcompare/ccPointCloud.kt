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

import com.github.vatbub.cloudcompare.CloudCompareBinFile.DeserializationFlags.DF_POINT_COORDS_64_BITS
import java.io.RandomAccessFile

@Suppress("ClassName", "MemberVisibilityCanBePrivate")
class ccPointCloud(name: String?) : ccShiftedObject(name) {
    var pointsVisibility: Array<UByte> = arrayOf()
        private set

    var pointSize: Byte = 0.toByte()
        private set

    var points: Array<CCVector3d> = arrayOf()
        private set

    var sfColorScaleDisplayed: Boolean = false
        private set

    var displayedScalarFieldIndex: Int = 0
        private set

    override fun fromFileNoChildren(
        randomAccessFile: RandomAccessFile,
        dataVersion: Int,
        flags: Int,
    ) {
        super.fromFileNoChildren(randomAccessFile, dataVersion, flags)
        require(dataVersion >= 20)

        if (dataVersion < 33) {
            // 'coordinates shift' (dataVersion>=20)
            globalShift = randomAccessFile.read3DVector()
            globalScale = 1.0
        } else {
            // 'global shift & scale' (dataVersion>=33)
            loadShiftInfoFromFile(randomAccessFile)
        }

        // 'visibility' array (dataVersion>=20)
        val hasVisibilityArray = randomAccessFile.readBoolean()
        if (hasVisibilityArray) {
            pointsVisibility = randomAccessFile.uByteArrayFromFile(dataVersion)
        }

        // 'point size' (dataVersion>=24)
        if (dataVersion >= 24) {
            pointSize = randomAccessFile.readByte()
        }

        // points array (dataVersion>=20)
        val fileCoordIsDouble = (flags and DF_POINT_COORDS_64_BITS.intValue) > 0
        points = if (!fileCoordIsDouble) {
            // file is 'float' and current type is 'double'
            randomAccessFile.genericArrayFromFile(
                dataVersion,
                { it.readLEFloat() },
                { CCVector3d(it[0].toDouble(), it[1].toDouble(), it[2].toDouble()) },
            )
        } else {
            randomAccessFile.genericArrayFromFile(
                dataVersion,
                { it.readLEDouble() },
                { CCVector3d(it[0], it[1], it[2]) },
            )
        }

        // colors array (dataVersion>=20)
        val hasColorsArray = randomAccessFile.readBoolean()
        if (hasColorsArray) {
            TODO("RGBA colors not yet supported")
        }

        // normals array (dataVersion>=20)
        val hasNormalsArray = randomAccessFile.readBoolean()
        if (hasNormalsArray) {
            TODO("Normals not yet supported")
        }

        // scalar field(s)
        // number of scalar fields (dataVersion>=20)
        val sfCount: UInt = randomAccessFile.readLEUInt()

        // scalar fields (dataVersion>=20)
        repeat(sfCount.toInt()) {
            TODO("Scalar fields not yet supported")
        }

        if (dataVersion < 27) {
            TODO("Scalar fields must be fixed for backwards compatibility, this parser can't do that.")
        }

        sfColorScaleDisplayed = randomAccessFile.readBoolean()
        displayedScalarFieldIndex = randomAccessFile.readLEInt()

        // grid structures (dataVersion>=41)
        if (dataVersion >= 41) {
            // number of grids
            val gridCount: UInt = randomAccessFile.readLEUInt()

            // load each grid
            repeat(gridCount.toInt()) {
                TODO("Grids not yet supported")
            }
        }

        // Waveforms (dataVersion >= 44)
        if (dataVersion >= 44) {
            val withFWF = randomAccessFile.readBoolean()
            if (withFWF) {
                TODO("Wave forms not yet supported: Object name: $name")
            }
        }
    }
}
