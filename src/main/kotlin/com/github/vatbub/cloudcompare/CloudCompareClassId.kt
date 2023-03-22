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

import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_ARRAY_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_BOX_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CAMERA_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CLIP_BOX_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CLIP_BOX_PART_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CLOUD_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_COMPRESSED_NORMAL_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CONE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_COORDINATESYSTEM_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CUSTOM_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_CYLINDER_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_DISH_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_EXTRU_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_FACET_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_GROUND_BASED_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_GROUP_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_HIERARCH_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_IMAGE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_KDTREE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_LABEL_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_LEAF_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_MATERIAL_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_MESH_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_NORMAL_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_OCTREE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_PLANE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_POLYLINE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_PRIMITIVE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_QUADRIC_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_RGBA_COLOR_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_RGB_COLOR_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_SENSOR_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_SPHERE_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_TEX_COORDS_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_TORUS_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_TRANS_BUFFER_BIT
import com.github.vatbub.cloudcompare.ObjectTypeFlags.CC_VIEWPORT_BIT

enum class CloudCompareClassId(val longValue: Long) {
    OBJECT(0),
    HIERARCHY_OBJECT(CC_HIERARCH_BIT),
    POINT_CLOUD(HIERARCHY_OBJECT.longValue or CC_CLOUD_BIT),
    MESH(HIERARCHY_OBJECT.longValue or CC_MESH_BIT),
    SUB_MESH(HIERARCHY_OBJECT.longValue or CC_MESH_BIT or CC_LEAF_BIT),
    MESH_GROUP(MESH.longValue or CC_GROUP_BIT), // DEPRECATED; DEFINITION REMAINS FOR BACKWARD COMPATIBILITY ONLY
    FACET(HIERARCHY_OBJECT.longValue or CC_FACET_BIT),
    POINT_OCTREE(HIERARCHY_OBJECT.longValue or CC_OCTREE_BIT or CC_LEAF_BIT),
    POINT_KDTREE(HIERARCHY_OBJECT.longValue or CC_KDTREE_BIT or CC_LEAF_BIT),
    POLY_LINE(HIERARCHY_OBJECT.longValue or CC_POLYLINE_BIT),
    IMAGE(CC_HIERARCH_BIT or CC_IMAGE_BIT),
    CALIBRATED_IMAGE(IMAGE.longValue or CC_LEAF_BIT),
    SENSOR(CC_HIERARCH_BIT or CC_SENSOR_BIT),
    GBL_SENSOR(SENSOR.longValue or CC_GROUND_BASED_BIT),
    CAMERA_SENSOR(SENSOR.longValue or CC_CAMERA_BIT),
    PRIMITIVE(MESH.longValue or CC_PRIMITIVE_BIT), // primitives are meshes
    PLANE(PRIMITIVE.longValue or CC_PLANE_BIT),
    SPHERE(PRIMITIVE.longValue or CC_SPHERE_BIT),
    TORUS(PRIMITIVE.longValue or CC_TORUS_BIT),
    CONE(PRIMITIVE.longValue or CC_CONE_BIT),
    OLD_CYLINDER_ID(PRIMITIVE.longValue or CC_CYLINDER_BIT), // for backward compatibility
    CYLINDER(PRIMITIVE.longValue or CC_CYLINDER_BIT or CC_CONE_BIT), // cylinders are cones
    BOX(PRIMITIVE.longValue or CC_BOX_BIT),
    DISH(PRIMITIVE.longValue or CC_DISH_BIT),
    EXTRU(PRIMITIVE.longValue or CC_EXTRU_BIT),
    QUADRIC(PRIMITIVE.longValue or CC_QUADRIC_BIT),
    MATERIAL_SET(CC_MATERIAL_BIT or CC_GROUP_BIT or CC_LEAF_BIT),
    ARRAY(CC_ARRAY_BIT),
    NORMALS_ARRAY(CC_ARRAY_BIT or CC_NORMAL_BIT or CC_LEAF_BIT),
    NORMAL_INDEXES_ARRAY(CC_ARRAY_BIT or CC_COMPRESSED_NORMAL_BIT or CC_LEAF_BIT),
    RGB_COLOR_ARRAY(CC_ARRAY_BIT or CC_RGB_COLOR_BIT or CC_LEAF_BIT),
    RGBA_COLOR_ARRAY(CC_ARRAY_BIT or CC_RGBA_COLOR_BIT or CC_LEAF_BIT),
    TEX_COORDS_ARRAY(CC_ARRAY_BIT or CC_TEX_COORDS_BIT or CC_LEAF_BIT),
    LABEL_2D(HIERARCHY_OBJECT.longValue or CC_LABEL_BIT or CC_LEAF_BIT),
    VIEWPORT_2D_OBJECT(HIERARCHY_OBJECT.longValue or CC_VIEWPORT_BIT or CC_LEAF_BIT),
    VIEWPORT_2D_LABEL(VIEWPORT_2D_OBJECT.longValue or CC_LABEL_BIT),
    CLIPPING_BOX(CC_CLIP_BOX_BIT or CC_LEAF_BIT),
    CLIPPING_BOX_PART(CC_CLIP_BOX_PART_BIT or CC_LEAF_BIT),
    TRANS_BUFFER(HIERARCHY_OBJECT.longValue or CC_TRANS_BUFFER_BIT or CC_LEAF_BIT),
    COORDINATESYSTEM(PRIMITIVE.longValue or CC_COORDINATESYSTEM_BIT),
    //  Custom types
    /** Custom objects are typically defined by plugins. They can be inserted in an object
     hierarchy or displayed in an OpenGL context like any other ccHObject.
     To differentiate custom objects, use the meta-data mechanism (see ccObject::getMetaData
     and ccObject::setMetaData). You can also define a custom icon (see ccHObject::getIcon).

     It is highly advised to use the ccCustomHObject and ccCustomLeafObject interfaces to
     define a custom types. Carefully read the ccCustomHObject::isDeserialized method's
     description and the warning below!

     Warning: custom objects can't be 'fully' serialized. Don't overload the
     'ccSerializableObject::toFile' method for them as this would break the deserialization mechanism!
     They can only be serialized as plain ccHObject instances (CC_TYPES::HIERARCHY_OBJECT).
     Hierarchical custom objects (CC_TYPES::CUSTOM_H_OBJECT) will be deserialized as ccCustomHObject
     instances. Leaf custom objects (CC_TYPES::CUSTOM_LEAF_OBJECT) will be deserialized as
     ccCustomLeafObject instances.
     **/
    CUSTOM_H_OBJECT(HIERARCHY_OBJECT.longValue or CC_CUSTOM_BIT),
    CUSTOM_LEAF_OBJECT(CUSTOM_H_OBJECT.longValue or CC_LEAF_BIT),
}

// Bits for object type flags (64 bits)
object ObjectTypeFlags {
    const val CC_HIERARCH_BIT: Long = 0x00000000000001 // Hierarchical object
    const val CC_LEAF_BIT: Long = 0x00000000000002 // Tree leaf (no children)
    const val CC_GROUP_BIT: Long = 0x00000000000004 // Group (no data, aggregation only)
    const val CC_PRIMITIVE_BIT: Long = 0x00000000000008 // Primitive (sphere, plane, torus, cylinder, etc.)
    const val CC_ARRAY_BIT: Long = 0x00000000000010 // Array
    const val CC_LABEL_BIT: Long = 0x00000000000020 // 2D label
    const val CC_VIEWPORT_BIT: Long = 0x00000000000040 // 2D viewport
    const val CC_CUSTOM_BIT: Long = 0x00000000000080 // For custom (plugin defined) objects
    const val CC_CLOUD_BIT: Long = 0x00000000000100 // Point Cloud
    const val CC_MESH_BIT: Long = 0x00000000000200 // Mesh
    const val CC_OCTREE_BIT: Long = 0x00000000000400 // Octree
    const val CC_POLYLINE_BIT: Long = 0x00000000000800 // Polyline
    const val CC_IMAGE_BIT: Long = 0x00000000001000 // Picture
    const val CC_SENSOR_BIT: Long = 0x00000000002000 // Sensor def.
    const val CC_PLANE_BIT: Long = 0x00000000004000 // Plane (primitive)
    const val CC_SPHERE_BIT: Long = 0x00000000008000 // Sphere (primitive)
    const val CC_TORUS_BIT: Long = 0x00000000010000 // Torus (primitive)
    const val CC_CYLINDER_BIT: Long = 0x00000000020000 // Cylinder (primitive)
    const val CC_CONE_BIT: Long = 0x00000000040000 // Cone (primitive)
    const val CC_BOX_BIT: Long = 0x00000000080000 // Box (primitive)
    const val CC_DISH_BIT: Long = 0x00000000100000 // Dish (primitive)
    const val CC_EXTRU_BIT: Long = 0x00000000200000 // Extrusion (primitive)
    const val CC_KDTREE_BIT: Long = 0x00000000400000 // Kd-tree
    const val CC_FACET_BIT: Long = 0x00000000800000 // Facet (composite object: cloud + 2D1/2 MESH.intValue + 2D1/2 polyline)
    const val CC_MATERIAL_BIT: Long = 0x00000001000000 // Material
    const val CC_CLIP_BOX_BIT: Long = 0x00000002000000 // Clipping box
    const val CC_TRANS_BUFFER_BIT: Long = 0x00000004000000 // Indexed transformation buffer
    const val CC_GROUND_BASED_BIT: Long = 0x00000008000000 // For Ground Based Lidar Sensors
    const val CC_RGB_COLOR_BIT: Long = 0x00000010000000 // Color (R,G,B)
    const val CC_NORMAL_BIT: Long = 0x00000020000000 // Normal (Nx,Ny,Nz)
    const val CC_COMPRESSED_NORMAL_BIT: Long = 0x00000040000000 // Compressed normal (index)
    const val CC_TEX_COORDS_BIT: Long = 0x00000080000000 // Texture coordinates (u,v)
    const val CC_CAMERA_BIT: Long = 0x00000100000000 // For camera sensors (projective sensors)
    const val CC_QUADRIC_BIT: Long = 0x00000200000000 // Quadric (primitive)
    const val CC_RGBA_COLOR_BIT: Long = 0x00000400000000 // Color (R,G,B,A)
    const val CC_COORDINATESYSTEM_BIT: Long = 0x00000800000000 // CoordinateSystem (primitive)
    const val CC_CLIP_BOX_PART_BIT: Long = 0x00001000000000 // Clipping-box component
}
