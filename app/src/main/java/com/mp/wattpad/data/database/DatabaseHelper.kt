package com.mp.wattpad.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context):
    SQLiteOpenHelper(context, dbName, null, 1){

    private val sqlCreateTableStories = "CREATE TABLE IF NOT EXISTS $storyTable (" +
            "$Story_ID INTEGER PRIMARY KEY," +
            "$Story_Title_COL TEXT," +
            "$Story_Author_COL TEXT," +
            "$Story_Image_COL TEXT);"

    companion object{
        const val dbName = "StoriesDatabase"
        const val storyTable = "Stories"
        const val Story_ID = "StoryID"
        const val Story_Title_COL = "StoryTitle"
        const val Story_Author_COL = "StoryAuthor"
        const val Story_Image_COL = "StoryImage"

        var database: SQLiteDatabase? = null
        fun initDatabase(ctx: Context){
            database = DatabaseHelper(ctx).writableDatabase
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(sqlCreateTableStories)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $dbName")
        onCreate(db)
    }
}