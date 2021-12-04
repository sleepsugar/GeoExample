package com.example.geohack.utils

import android.os.Environment
import android.text.format.Time
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import kotlin.Throws

class Collector(private var folderName: String) {

    private val folder: File
        get() {
            val folder = File(Environment.getExternalStorageDirectory(), folderName)

            //create the folder is it doesn't exist
            createFolder(folder)
            return folder
        }

    private fun createFolder(folder: File) {
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    fun collectToFile(fileName: String, values: ArrayList<Float>) {
    }

    fun collectToFile(fileName: String, vararg args: Float) {
        val values = ArrayList<Float>()
        for (arg in args) {
            values.add(arg)
        }

        collectToFile(fileName, values)
    }
}
