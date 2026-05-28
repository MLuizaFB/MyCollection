package com.example.mycollection.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Usuario(
    @PrimaryKey val id: String,
    var nome: String,
    var caminhoImagem: String?
)


