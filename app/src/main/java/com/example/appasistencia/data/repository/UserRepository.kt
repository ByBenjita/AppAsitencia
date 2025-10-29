package com.example.appasistencia.data.repository

import android.content.Context
import com.example.appasistencia.data.local.UserDao
import com.example.appasistencia.data.local.UserDataBase
import com.example.appasistencia.data.local.UserEntity

class UserRepository(context: Context) {
    private val userDao: UserDao

    init {
        val database = UserDataBase(context)
        userDao = UserDao(database)
    }

    fun saveUser(user: UserEntity): Boolean {
        return userDao.saveUser(user)
    }

    fun getSavedUser(): UserEntity? {
        return userDao.getSavedUser()
    }

    fun logout(): Boolean {
        return userDao.deleteAllUsers()
    }

    fun hasSavedUser(): Boolean {
        return userDao.hasSavedUser()
    }
}