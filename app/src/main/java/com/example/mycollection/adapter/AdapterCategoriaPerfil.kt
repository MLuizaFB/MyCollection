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

class AdapterCategoriaPerfil(
    private var listaCategorias: List<Categoria>,
    private val onClickExcluir: (Categoria) -> Unit,
    private val onClickEditar: (Categoria) -> Unit
) : RecyclerView.Adapter<AdapterCategoriaPerfil.CategoriaViewHolder>() {

    private var modoExclusao = false

    fun setModoExclusao(ativo: Boolean) {
        modoExclusao = ativo
        notifyDataSetChanged()
    }

    fun atualizarLista(novaLista: List<Categoria>) {
        listaCategorias = novaLista
        notifyDataSetChanged()
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCategoria: ImageView = itemView.findViewById(R.id.imgCategoria)
        val txtNomeCategoria: TextView = itemView.findViewById(R.id.txtNomeCategoria)
        val btnExcluir: ImageView = itemView.findViewById(R.id.btnExcluir)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_perfil, parent, false)
        return CategoriaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = listaCategorias[position]

        holder.txtNomeCategoria.text = categoria.nome

        if (!categoria.caminhoImagem.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(categoria.caminhoImagem)
                .into(holder.imgCategoria)
        }

        if (modoExclusao) {
            holder.btnExcluir.visibility = View.VISIBLE
            holder.btnEditar.visibility = View.VISIBLE
        } else {
            holder.btnExcluir.visibility = View.GONE
            holder.btnEditar.visibility = View.GONE
        }

        holder.btnExcluir.setOnClickListener {
            onClickExcluir(categoria)
        }

        holder.btnEditar.setOnClickListener {
            onClickEditar(categoria)
        }
    }

    override fun getItemCount() = listaCategorias.size
}

