package com.example.mycollection.dao

import androidx.room.*
import com.example.mycollection.model.ItemColecao

@Dao
interface ItemColecaoDao {

    @Insert
    suspend fun inserir(item: ItemColecao)

    @Update
    suspend fun atualizar(item: ItemColecao)

    @Delete
    suspend fun deletar(item: ItemColecao)

    @Query("SELECT * FROM itens WHERE categoriaId = :categoriaId")
    suspend fun listarItensPorCategoria(categoriaId: Int): List<ItemColecao>

    @Query("SELECT * FROM itens WHERE id = :id")
    suspend fun buscarPorId(id: Int): ItemColecao?
}


