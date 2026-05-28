package com.example.mycollection.dao

import androidx.room.*
import com.example.mycollection.model.Categoria

@Dao
interface CategoriaDao {

    @Insert
    suspend fun inserir(categoria: Categoria)

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun deletar(categoria: Categoria)

    @Query("SELECT * FROM categorias")
    suspend fun listarCategorias(): List<Categoria>
}







