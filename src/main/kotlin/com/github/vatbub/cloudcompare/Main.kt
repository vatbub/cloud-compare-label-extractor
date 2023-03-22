package com.github.vatbub.cloudcompare

import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    export(
        inputFile = File("C:\\Users\\path\\to\\CloudCompare-file.bin"),
        outputFolder = File("C:\\Users\\path\\to\\cloudCompareOutput"),
    )
}

@OptIn(ExperimentalTime::class)
private fun export(inputFile: File, outputFolder: File) {
    val timeTaken = measureTime {
        println("Loading binary file...")
        val binFile =
            CloudCompareBinFile(inputFile)

        println("Creating output folder ${outputFolder.absolutePath} ...")
        outputFolder.mkdirs()

        println("Exporting CSVs...")
        binFile.root.children.forEach { child ->
            child as ccHObject
            val pointCloud = child.children.first() as ccPointCloud
            val labels = pointCloud.children.filterIsInstance<cc2DLabel>()

            val points = labels.map { label ->
                val location = pointCloud.points[label.pointReferences.first().second.toInt()] - pointCloud.globalShift
                CsvPoint(
                    name = label.name!!,
                    easting = location.x,
                    northing = location.y,
                    elevation = location.z,
                )
            }

            val outputFile = outputFolder.resolve(pointCloud.name!! + ".csv")
            println("Exporting to ${outputFile.absolutePath} ...")

            outputFile.writeText(
                buildString {
                    appendLine("Name,Easting,Northing,Elevation")
                    points.forEach { point -> appendLine("${point.name},${point.easting},${point.northing},${point.elevation}") }
                },
            )
        }
    }

    println("Done, execution took ${timeTaken.toDouble(DurationUnit.MILLISECONDS)}ms")
}

private data class CsvPoint(val name: String, val easting: Double, val northing: Double, val elevation: Double)
