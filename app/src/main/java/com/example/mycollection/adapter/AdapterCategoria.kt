package com.example.mycollection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycollection.R
import com.example.mycollection.model.Categoria
import java.io.File

class AdapterCategoria(
    private val lista: List<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<AdapterCategoria.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView>(R.id.categoryName)
        val imagem = itemView.findViewById<ImageView>(R.id.categoryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = lista[position]
        holder.nome.text = categoria.nome

        Glide.with(holder.itemView.context)
            .load(File(categoria.caminhoImagem))
            .circleCrop()
            .into(holder.imagem)

        holder.itemView.setOnClickListener { onClick(categoria) }
    }

    override fun getItemCount(): Int = lista.size
}
