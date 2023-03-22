package com.github.vatbub.cloudcompare

import java.io.RandomAccessFile

data class CCVector2d(val x: Double, val y: Double)

fun RandomAccessFile.read2DVector() =
    CCVector2d(readLEDouble(), readLEDouble())
