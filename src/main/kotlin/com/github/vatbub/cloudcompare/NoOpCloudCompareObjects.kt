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

@file:Suppress("unused", "ClassName")

package com.github.vatbub.cloudcompare

import java.io.RandomAccessFile

abstract class NoOpCloudCompareObject(name: String?) : ccHObject(name) {
    init {
        throw NotImplementedError("This object type is not yet supported by the parser.")
    }

    override fun fromFileNoChildren(
        randomAccessFile: RandomAccessFile,
        dataVersion: Int,
        flags: Int,
    ) {
        TODO("Not yet implemented")
    }
}

class ccMesh(name: String?, val vertices: Any?) : NoOpCloudCompareObject(name)
class ccSubMesh(name: String?, val vertices: Any?) : NoOpCloudCompareObject(name)
class ccMeshGroup(name: String?) : NoOpCloudCompareObject(name)
class ccPolyline(name: String?, val vertices: Any?) : NoOpCloudCompareObject(name)
class ccFacet(name: String?) : NoOpCloudCompareObject(name)
class ccMaterialSet(name: String?) : NoOpCloudCompareObject(name)
class NormsTableType(name: String?) : NoOpCloudCompareObject(name)
class NormsIndexesTableType(name: String?) : NoOpCloudCompareObject(name)
class ColorsTableType(name: String?) : NoOpCloudCompareObject(name)
class RGBAColorsTableType(name: String?) : NoOpCloudCompareObject(name)
class TextureCoordsContainer(name: String?) : NoOpCloudCompareObject(name)
class ccImage(name: String?) : NoOpCloudCompareObject(name)
class ccGBLSensor(name: String?) : NoOpCloudCompareObject(name)
class ccCameraSensor(name: String?) : NoOpCloudCompareObject(name)
class cc2DViewportObject(name: String?) : NoOpCloudCompareObject(name)
class cc2DViewportLabel(name: String?) : NoOpCloudCompareObject(name)
class ccPlane(name: String?) : NoOpCloudCompareObject(name)
class ccSphere(name: String?) : NoOpCloudCompareObject(name)
class ccTorus(name: String?) : NoOpCloudCompareObject(name)
class ccCylinder(name: String?) : NoOpCloudCompareObject(name)
class ccBox(name: String?) : NoOpCloudCompareObject(name)
class ccCone(name: String?) : NoOpCloudCompareObject(name)
class ccDish(name: String?) : NoOpCloudCompareObject(name)
class ccExtru(name: String?) : NoOpCloudCompareObject(name)
class ccQuadric(name: String?) : NoOpCloudCompareObject(name)
class ccIndexedTransformationBuffer(name: String?) : NoOpCloudCompareObject(name)
class ccCustomHObject(name: String?) : NoOpCloudCompareObject(name)
class ccCustomLeafObject(name: String?) : NoOpCloudCompareObject(name)
class ccCoordinateSystem(name: String?) : NoOpCloudCompareObject(name)
