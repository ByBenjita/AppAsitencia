package com.example.appasistencia.data.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class UserDao(private val database: UserDataBase) {

    companion object {
        private const val TAG = "UserDao"
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_GUARDAR_SESION = "guardar_sesion"
    }

    fun saveUser(user: UserEntity): Boolean {
        val db = database.writableDatabase
        return try {
            db.beginTransaction()

            // Eliminar todos los usuarios existentes
            val deleteCount = db.delete(TABLE_USERS, null, null)
            Log.d(TAG, "Usuarios eliminados: $deleteCount")

            // Insertar nuevo usuario
            val values = ContentValues().apply {
                put(COLUMN_USUARIO, user.usuario)
                put(COLUMN_PASSWORD, user.password)
                put(COLUMN_GUARDAR_SESION, if (user.guardarSesion) 1 else 0)
            }

            val result = db.insert(TABLE_USERS, null, values)
            db.setTransactionSuccessful()

            val success = result != -1L
            Log.d(TAG, "Usuario guardado: $success, ID: $result")
            success

        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar usuario: ${e.message}")
            false
        } finally {
            db.endTransaction()
        }
    }

    fun getSavedUser(): UserEntity? {
        val db = database.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_GUARDAR_SESION = ?",
            arrayOf("1"),
            null, null, null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                val user = UserEntity(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    usuario = it.getString(it.getColumnIndexOrThrow(COLUMN_USUARIO)),
                    password = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    guardarSesion = it.getInt(it.getColumnIndexOrThrow(COLUMN_GUARDAR_SESION)) == 1
                )
                Log.d(TAG, "Usuario recuperado: ${user.usuario}")
                user
            } else {
                Log.d(TAG, "No hay usuario guardado")
                null
            }
        }
    }

    fun deleteAllUsers(): Boolean {
        val db = database.writableDatabase
        return try {
            val result = db.delete(TABLE_USERS, null, null)
            Log.d(TAG, "Usuarios eliminados en logout: $result")
            result >= 0
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar usuarios: ${e.message}")
            false
        }
    }

    fun hasSavedUser(): Boolean {
        val user = getSavedUser()
        return user != null
    }
}