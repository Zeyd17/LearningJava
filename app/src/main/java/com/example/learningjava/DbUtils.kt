package com.example.learningjava

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val DATABASE_NAME = "Book.db"
const val DATABASE_PATH = "/data/data/com.example.learningjava/databases/"  //in android device
const val DATABASE_VERSION = 1

class DbUtils(private val context: Context) :
    // helps to load the db and execute the query on the db
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    var db: SQLiteDatabase? = null // initialize

    override fun onCreate(p0: SQLiteDatabase?) {}   //modify

    fun checkDb(): Boolean {
        return File(DATABASE_PATH + DATABASE_NAME).exists()
    }

    fun copyDb() {
        try {
            val mInput = context.assets.open(DATABASE_NAME)
            val dir = File(DATABASE_PATH)
            if (!dir.exists()) dir.mkdirs()
            val file = File(DATABASE_PATH + DATABASE_NAME)
            if (!file.exists()) file.createNewFile()
            val mOutput = FileOutputStream(DATABASE_PATH + DATABASE_NAME)

            // transfer bytes from the inputFile to the outputFile
            val buffer = ByteArray(1024)
            var length: Int = mInput.read(buffer)

            while (length > 0) {
                mOutput.write(buffer, 0, length)
                length = mInput.read(buffer)
            }

            //Close the streams
            mOutput.flush()
            mOutput.close()
            mInput.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun dbDelete() {
        val file = File(DATABASE_PATH + DATABASE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            this.dbDelete()
        }
    }

    fun openDb() {
        this.db = SQLiteDatabase.openDatabase(   //se value of instance
            DATABASE_PATH + DATABASE_NAME,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

}