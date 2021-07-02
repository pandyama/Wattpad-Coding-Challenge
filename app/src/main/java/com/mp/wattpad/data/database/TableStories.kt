package com.mp.wattpad.data.database

import android.content.ContentValues
import com.mp.wattpad.data.model.SQLModel

class TableStories {

    private val getStories = "SELECT * FROM ${DatabaseHelper.storyTable}"
    private val deleteStories = "DELETE FROM ${DatabaseHelper.storyTable}"
    private val db = DatabaseHelper.database

    fun getStories(): List<SQLModel>{
        val res = db!!.rawQuery(getStories, null)
        val listOfStories: MutableList<SQLModel> = mutableListOf()
        if(res.count != 0){
            while(res.moveToNext()){
                val storyModel = SQLModel(res.getString(1), res.getString(2), res.getString(3))
                listOfStories.add(storyModel)
            }
        }
        return listOfStories
    }

    fun insertStories(story: SQLModel): Long{
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.Story_Title_COL, story.storyTitle)
        contentValues.put(DatabaseHelper.Story_Author_COL, story.storyAuthor)
        contentValues.put(DatabaseHelper.Story_Image_COL, story.storyImage)
        return db!!.insert(DatabaseHelper.storyTable,null,contentValues)
    }

    fun clearStories(){
        db!!.execSQL(deleteStories)
    }
}