package com.example.appasistencia.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDataBase(context: Context) :
    SQLiteOpenHelper(context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserApp.db"

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_GUARDAR_SESION = "guardar_sesion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_GUARDAR_SESION INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}