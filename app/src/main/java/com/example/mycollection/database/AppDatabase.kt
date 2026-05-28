package com.example.mycollection.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycollection.dao.CategoriaDao
import com.example.mycollection.dao.ItemColecaoDao
import com.example.mycollection.dao.UsuarioDao
import com.example.mycollection.model.Categoria
import com.example.mycollection.model.ItemColecao
import com.example.mycollection.model.Usuario

@Database (entities = [Categoria::class, ItemColecao::class, Usuario::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun itemColecaoDao(): ItemColecaoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_collection_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
