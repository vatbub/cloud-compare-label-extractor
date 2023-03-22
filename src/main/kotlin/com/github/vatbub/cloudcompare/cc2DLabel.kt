package com.github.vatbub.cloudcompare

import java.io.RandomAccessFile

@Suppress("ClassName", "MemberVisibilityCanBePrivate")
class cc2DLabel(name: String?) : ccHObject(name) {
    var entityCenterPoint: Boolean = false
        private set

    var screenPosX: Float = 0f
        private set

    var screenPosY: Float = 0f
        private set

    var showFullBody: Boolean = false
        private set

    var dispIn2D: Boolean = false
        private set

    var dispPointsLegend: Boolean = false
        private set

    val pointReferences = mutableListOf<Pair<UInt, UInt>>()

    override fun fromFileNoChildren(
        randomAccessFile: RandomAccessFile,
        dataVersion: Int,
        flags: Int,
    ) {
        super.fromFileNoChildren(randomAccessFile, dataVersion, flags)

        val pointsCount = randomAccessFile.readLEUInt()

        repeat(pointsCount.toInt()) {
            val pointIndex = randomAccessFile.readLEUInt()
            val cloudId = randomAccessFile.readLEUInt()
            if (cloudId != 0.toUInt()) {
                pointReferences.add(cloudId to pointIndex)
            }

            // Everything is correct at least until here

            if (dataVersion >= 49) {
                // mesh ID (dataVersion >= 49 - will be retrieved later)
                val meshID = randomAccessFile.readLEUInt()

                // uv coordinates in the triangle (dataVersion >= 49)
                @Suppress("UNUSED_VARIABLE")
                val uv = randomAccessFile.read2DVector()

                if (meshID != 0.toUInt()) {
                    println("The point for the label $name has a mesh id, I have no idea what that means, but it seems we can ignore it")
                }
            }

            // entity center point (dataVersion >= 50)
            if (dataVersion >= 50) {
                entityCenterPoint = randomAccessFile.readBoolean()
            }
        }

        // Relative screen position (dataVersion >= 20)
        screenPosX = randomAccessFile.readLEFloat()
        screenPosY = randomAccessFile.readLEFloat()

        // Collapsed state (dataVersion >= 20)
        showFullBody = randomAccessFile.readBoolean()

        if (dataVersion > 20) {
            // Show in 2D boolean (dataVersion >= 21)
            dispIn2D = randomAccessFile.readBoolean()

            // Show point(s) legend boolean (dataVersion >= 21)
            dispPointsLegend = randomAccessFile.readBoolean()
        }
    }
}
