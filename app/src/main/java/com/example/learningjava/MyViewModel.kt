package com.example.learningjava
//central repository
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

// App screens
object Screens {
    const val Splash = "Splash"
    const val Home = "Home"
}

// Models ...
// store all the info of 1 chapter
data class Chapter(val id: Int, val title: String, val subtitle: String, val body: String)

class MyViewModel(context: Context) : ViewModel() {
    private val dbUtils = DbUtils(context)   // initialize

    init {
        val isDb = dbUtils.checkDb()
        if (isDb) dbUtils.openDb()
        else dbUtils.copyDb()
    }

    fun getIndex(): SnapshotStateList<Chapter> {
        // Open the Database if not already opened
        val isDb = dbUtils.checkDb()
        if (!isDb) dbUtils.copyDb() else dbUtils.openDb()

        // Get the data from database and return it to the calling function
        val indexes = mutableStateListOf<Chapter>()
        // to iterate row by row on the result set.
        val cur = dbUtils.db!!.rawQuery("select id, title, subtitle from Chapter", null)
        if (cur.moveToFirst()) {
            do {
                val idID = cur.getColumnIndex("id")
                val id: Int = cur.getInt(idID)

                val titleID = cur.getColumnIndex("title")
                val title: String = cur.getString(titleID).toString()

                val subtitleID = cur.getColumnIndex("subtitle")
                val subtitle: String = cur.getString(subtitleID).toString()

                indexes.add(Chapter(id, title, subtitle, ""))

            } while (cur.moveToNext())
        }

        cur.close()
        return indexes
    }

    fun getChapter(id: Int): Chapter? {
        // get the full chapter including body
        val cur = dbUtils.db!!.rawQuery("select * from Chapter where id=${id}", null)
        var chapter: Chapter? = null
        if (cur.moveToFirst()) {
            val idID = cur.getColumnIndex("id")
            val id = cur.getInt(idID)

            val titleID = cur.getColumnIndex("title")
            val title: String = cur.getString(titleID).toString()

            val subtitleID = cur.getColumnIndex("subtitle")
            val subtitle: String = cur.getString(subtitleID).toString()

            val bodyID = cur.getColumnIndex("body")
            val body: String = cur.getString(bodyID).toString()

            chapter = Chapter(id, title, subtitle, body)
        }

        cur.close()
        return chapter
    }
}
