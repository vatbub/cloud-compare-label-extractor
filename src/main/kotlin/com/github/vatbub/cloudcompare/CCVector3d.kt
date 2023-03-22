package com.github.vatbub.cloudcompare

import java.io.RandomAccessFile

data class CCVector3d(val x: Double, val y: Double, val z: Double) {
    operator fun plus(other: CCVector3d): CCVector3d = CCVector3d(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z,
    )

    operator fun minus(other: CCVector3d): CCVector3d = CCVector3d(
        this.x - other.x,
        this.y - other.y,
        this.z - other.z,
    )
}

fun RandomAccessFile.read3DVector() =
    CCVector3d(readLEDouble(), readLEDouble(), readLEDouble())
