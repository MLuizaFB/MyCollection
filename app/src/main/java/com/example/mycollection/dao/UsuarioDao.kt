package com.example.mycollection.dao

import androidx.room.*
import com.example.mycollection.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirOuAtualizar(usuario: Usuario)

    @Update
    suspend fun atualizar(usuario: Usuario)

    @Query("SELECT * FROM Usuario LIMIT 1")
    suspend fun obterUsuario(): Usuario?

    @Delete
    suspend fun deletar(usuario: Usuario)
}


