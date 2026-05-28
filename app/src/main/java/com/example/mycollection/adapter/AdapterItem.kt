package com.example.mycollection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycollection.R
import com.example.mycollection.model.ItemColecao
import java.io.File

class AdapterItem(
    private var lista: List<ItemColecao>,
    private val onClick: (ItemColecao) -> Unit
) : RecyclerView.Adapter<AdapterItem.ItemViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView>(R.id.itemName)
        val imagem = itemView.findViewById<ImageView>(R.id.itemImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_colecao, parent, false)
        return ItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = lista[position]
        holder.nome.text = item.nome

        Glide.with(holder.itemView.context)
            .load(File(item.caminhoImagemPrincipal))
            .into(holder.imagem)

        holder.itemView.setOnClickListener { onClick(item) }
    }
    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<ItemColecao>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}
