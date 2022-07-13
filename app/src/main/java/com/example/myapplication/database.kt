package com.example.myapplication

import android.content.ContentValues
import android.content.SharedPreferences
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class DBHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // functions for comparing and setting database versions
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.database_versions",
        Context.MODE_PRIVATE
    )
    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION
    }
    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DATABASE_NAME, DATABASE_VERSION)
            apply()
        }
    }

    // installing database from resources by coping
    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$ASSETS_PATH/$DATABASE_NAME.db")

        try {
            val outputFile = File(context.getDatabasePath(DATABASE_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }

    // starting the function of installing the database, if it has not been done before
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    // there is no need to implement these functions, since the database is not created, but copied from the file
    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    // starting the function of checking installing the database and getting access to the database
    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary() //without install every time
        //installDatabaseFromAssets() //can use for clean
        return super.getWritableDatabase()
    }

    // designation for corresponding files, tables, fields
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "tasks"
        const val TA = "tasks"
        const val TA_ID = "_id"
        const val TA_DES = "description"
        const val TA_COST = "cost"
        const val TA_AV = "available"
        const val TA_DL = "dailylist"
        const val TA_CHECK = "done"
        const val TA_CAT = "fk_cat"

        const val TC = "cats"
        const val TC_NAME = "name"
        const val ASSETS_PATH = "databases"
    }
}

// getting data (tasks) from the database: moving the cursor over the result of the sql-query to tasks table,
// creating task-objects
// and adding it to the tasks list

// if an additional parameter id was received, then tasks will be consist of one object,
// obtained from a row with the given id
fun SQLiteDatabase.getdata(tasks: MutableList<Task>, id: Int = -1){
    val cursor: Cursor = if (id==-1){
        this.query(DBHelper.TA, null, null, null, null, null, null)
    }
    else{
        this.rawQuery("select * from "+DBHelper.TA+" where _id="+id.toString(), null)
    }
    if (cursor.moveToFirst()){
        val idIndex: Int = cursor.getColumnIndex(DBHelper.TA_ID)
        val desIndex: Int = cursor.getColumnIndex(DBHelper.TA_DES)
        val costIndex: Int = cursor.getColumnIndex(DBHelper.TA_COST)
        val avIndex: Int = cursor.getColumnIndex(DBHelper.TA_AV)
        val dlIndex: Int = cursor.getColumnIndex(DBHelper.TA_DL)
        val checkIndex: Int = cursor.getColumnIndex(DBHelper.TA_CHECK)
        val catIndex: Int = cursor.getColumnIndex(DBHelper.TA_CAT)
        do {
            if (cursor.getInt(idIndex) in tasks.map{it.id}) continue
            val t = Task(cursor.getInt(idIndex), cursor.getString(desIndex), cursor.getInt(costIndex).toString(), cursor.getString(catIndex), cursor.getInt(avIndex), cursor.getInt(dlIndex), cursor.getInt(checkIndex))
            tasks.add(t)

        } while (cursor.moveToNext())
    } else Log.d("mLog", "0 rows")

}

// getting tasks (by calling the previous function) and categories-list (similar to the previous) from the database
fun SQLiteDatabase.getdata(tasks:MutableList<Task>, cats:MutableList<String>, id: Int = -1){
    this.getdata(tasks, id)
    val cursor: Cursor =
        this.query(DBHelper.TC, null, null, null, null, null, null)
    if(cursor.moveToFirst()) {
        val nameIndex = cursor.getColumnIndex(DBHelper.TC_NAME)
        do {
            cats.add(cursor.getString(nameIndex))
        } while (cursor.moveToNext())
    } else Log.d("mLog", "0 rows")
}

// writing data to the database by sql-query:
    // putting data in a special container and executing an insert request
// the function returns the id of the written row
fun SQLiteDatabase.setdata(t: Task): Long {
    val contentValues = ContentValues()
    contentValues.put(DBHelper.TA_DES, t.description)
    contentValues.put(DBHelper.TA_COST, t.cost)
    contentValues.put(DBHelper.TA_AV, t.available)
    contentValues.put(DBHelper.TA_DL, t.dl)
    contentValues.put(DBHelper.TA_CHECK, t.check)
    contentValues.put(DBHelper.TA_CAT, t.cat)
    return this.insert(DBHelper.TA, null, contentValues)
}

// updating data in the database:
    // checking flag for updating
    // checking the need the need for either updating or adding data (by calling the previous functions)
    // putting data in a special container and executing an update request
fun SQLiteDatabase.upddata(tasks: MutableList<Task>){
    for (t in tasks){
        if (t.id == -1){
            t.id=this.setdata(t).toInt()
            continue
        }
        if(t.upd){
            val contentValues = ContentValues()
            contentValues.put(DBHelper.TA_DES, t.description)
            contentValues.put(DBHelper.TA_COST, t.cost)
            contentValues.put(DBHelper.TA_AV, t.available)
            contentValues.put(DBHelper.TA_DL, t.dl)
            contentValues.put(DBHelper.TA_CHECK, t.check)
            contentValues.put(DBHelper.TA_CAT, t.cat)
            this.update(DBHelper.TA, contentValues, "_id = ?", arrayOf(t.id.toString()))
            t.upd = false
        }
    }
}

// deleting row by id
fun SQLiteDatabase.deletedata(id: Int){
    this.delete(DBHelper.TA, "_id ="+id.toString(), null)
}