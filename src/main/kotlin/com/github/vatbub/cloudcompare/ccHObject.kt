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

import com.github.vatbub.cloudcompare.CloudCompareClassId.*
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CUSTOM_BIT
import java.io.EOFException
import java.io.RandomAccessFile

@Suppress("ClassName", "MemberVisibilityCanBePrivate")
open class ccHObject(name: String?) : CloudCompareObject(name) {
    var visible: Boolean = true
        private set
    var lockedVisibility: Boolean = true
        private set
    var colorsDisplayed: Boolean = true
        private set
    var normalsDisplayed: Boolean = true
        private set
    var sfDisplayed: Boolean = true
        private set
    var colorIsOverridden: Boolean = false
        private set
    var glTransEnabled: Boolean = false
        private set
    var showNameIn3D: Boolean = false
        private set

    var selectionBehaviour: SelectionBehaviour = SelectionBehaviour.SELECTION_AA_BBOX
        private set

    val children = mutableListOf<CloudCompareObject>()

    fun fromFile(randomAccessFile: RandomAccessFile, dataVersion: Int, flags: Int) {
        fromFileNoChildren(randomAccessFile, dataVersion, flags)
        val serializableChildCount = try {
            randomAccessFile.readLEUInt()
        } catch (e: EOFException) {
            println("We reached the end of the file")
            0.toUInt()
        }

        // read serializable children (if any)
        repeat(serializableChildCount.toInt()) {
            var classId = randomAccessFile.readClassId(dataVersion)
            require(classId != OBJECT)

            if (dataVersion in 35..47 && classId.longValue and CC_CUSTOM_BIT != 0L) {
                // bug fix: for a long time the CC_CAMERA_BIT and CC_QUADRIC_BIT were wrongly defined
                // with two bits instead of one! The additional and wrongly defined bit was the CC_CUSTOM_BIT :(
                if (((classId.longValue and CAMERA_SENSOR.longValue) == CAMERA_SENSOR.longValue) ||
                    ((classId.longValue and QUADRIC.longValue) == QUADRIC.longValue)
                ) {
                    classId = CloudCompareClassId.values().first {
                        it.longValue == classId.longValue and CC_CUSTOM_BIT.inv()
                    }
                }
            }

            // create corresponding child object
            val child = create(classId)

            // specific case of custom objects (defined by plugins)
            if ((classId.longValue and CUSTOM_H_OBJECT.longValue) == CUSTOM_H_OBJECT.longValue) {
                TODO()
            }

            child.fromFile(randomAccessFile, dataVersion, flags)

            children.add(child)
        }

        // read the selection behavior (dataVersion>=23)
        if (dataVersion >= 23) {
            selectionBehaviour = SelectionBehaviour.values()[randomAccessFile.readLEInt()]
        }

        // read transformation history (dataVersion >= 45)
        if (dataVersion >= 45) {
            repeat(16) {
                randomAccessFile.readLEFloat()
            }
        }
    }

    override fun fromFileNoChildren(
        randomAccessFile: RandomAccessFile,
        dataVersion: Int,
        flags: Int,
    ) {
        // read 'ccObject' header
        super.readHeader(randomAccessFile, dataVersion, flags)

        visible = randomAccessFile.readBoolean()
        lockedVisibility = randomAccessFile.readBoolean()
        colorsDisplayed = randomAccessFile.readBoolean()
        normalsDisplayed = randomAccessFile.readBoolean()
        sfDisplayed = randomAccessFile.readBoolean()
        colorIsOverridden = randomAccessFile.readBoolean()
        if (colorIsOverridden) TODO()

        glTransEnabled = randomAccessFile.readBoolean()
        if (glTransEnabled) TODO()

        if (dataVersion >= 24) showNameIn3D = randomAccessFile.readBoolean()
    }

    companion object {
        fun create(objectType: CloudCompareClassId, name: String? = null): ccHObject {
            return when (objectType) {
                HIERARCHY_OBJECT -> ccHObject(name)
                POINT_CLOUD -> ccPointCloud(name)
                MESH -> ccMesh(name, null) // warning: no associated vertices --> retrieved later
                SUB_MESH -> ccSubMesh(name, null) // warning: no associated mesh --> retrieved later
                MESH_GROUP -> {
                    println("[ccHObject::New] Mesh groups are deprecated!")
                    ccMeshGroup(name) // warning: no associated vertices --> retrieved later
                }

                POLY_LINE -> ccPolyline(
                    name,
                    null,
                ) // warning: no associated vertices --> retrieved later
                FACET -> ccFacet(name)
                MATERIAL_SET -> ccMaterialSet(name)
                NORMALS_ARRAY -> NormsTableType(name)
                NORMAL_INDEXES_ARRAY -> NormsIndexesTableType(name)
                RGB_COLOR_ARRAY -> ColorsTableType(name)
                RGBA_COLOR_ARRAY -> RGBAColorsTableType(name)
                TEX_COORDS_ARRAY -> TextureCoordsContainer(name)
                IMAGE -> ccImage(name)
                CALIBRATED_IMAGE -> throw IllegalArgumentException("CALIBRATED_IMAGE is deprecated")
                GBL_SENSOR ->
                    ccGBLSensor(name) // warning: default sensor type set in constructor (see CCCoreLib::GroundBasedLidarSensor::setRotationOrder)
                CAMERA_SENSOR -> ccCameraSensor(name)
                LABEL_2D -> cc2DLabel(name)
                VIEWPORT_2D_OBJECT -> cc2DViewportObject(name)
                VIEWPORT_2D_LABEL -> cc2DViewportLabel(name)
                PLANE -> ccPlane(name)
                SPHERE -> ccSphere(name)
                TORUS -> ccTorus(name)
                in listOf(CYLINDER, OLD_CYLINDER_ID) -> ccCylinder(name)
                BOX -> ccBox(name)
                CONE -> ccCone(name)
                DISH -> ccDish(name)
                EXTRU -> ccExtru(name)
                QUADRIC -> ccQuadric(name)
                TRANS_BUFFER -> ccIndexedTransformationBuffer(name)
                CUSTOM_H_OBJECT -> ccCustomHObject(name)
                CUSTOM_LEAF_OBJECT -> ccCustomLeafObject(name)
                COORDINATESYSTEM -> ccCoordinateSystem(name)
                in listOf(POINT_OCTREE, POINT_KDTREE) ->
                    // construction this way is not supported (yet)
                    throw IllegalArgumentException(
                        "[ccHObject::New] This object (type $objectType) can't be constructed this way (yet)!",
                    )

                else ->
                    throw IllegalArgumentException("[ccHObject::New] Invalid object type ($objectType)!")
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun create(pluginId: String, classId: String, name: String? = null): ccHObject? {
            return null
        }
    }
}
