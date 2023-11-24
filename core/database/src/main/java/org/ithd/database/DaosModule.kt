package org.ithd.database

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.ithd.database.dao.UserDao

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesUserDao(
        database: MediaEditorDatabase
    ): UserDao = database.userDao()
}