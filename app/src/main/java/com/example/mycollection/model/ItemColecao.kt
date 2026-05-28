package com.example.mycollection.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "itens",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["categoriaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ItemColecao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nome: String,
    var descricao: String,
    var caminhoImagemPrincipal: String,
    var caminhoImagemSecundaria1: String? = null,
    var caminhoImagemSecundaria2: String? = null,
    var categoriaId: Int
)


