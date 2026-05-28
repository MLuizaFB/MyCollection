package com.example.mycollection

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mycollection.adapter.AdapterCategoria
import com.example.mycollection.adapter.AdapterItem
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.databinding.ActivityMainBinding
import com.example.mycollection.model.Categoria
import com.example.mycollection.model.ItemColecao
import com.example.mycollection.ui.AdicionarCategoriaActivity
import com.example.mycollection.ui.AdicionarItemActivity
import com.example.mycollection.ui.DetalhesItemActivity
import com.example.mycollection.ui.PerfilActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapterCategoria: AdapterCategoria
    private lateinit var adapterItem: AdapterItem

    private lateinit var db: AppDatabase

    private var listaCategorias: List<Categoria> = emptyList()
    private var listaItens: List<ItemColecao> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Clique no botão de adicionar
        binding.addIcon.setOnClickListener {
            mostrarMenuAdicionar(it)
        }

        // Clique no perfil
        binding.perfilIcon.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        // Setup inicial do RecyclerView de Itens
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.itemRecyclerView.layoutManager = layoutManager
        adapterItem = AdapterItem(emptyList(), onItemClick())
        binding.itemRecyclerView.adapter = adapterItem
    }

    override fun onResume() {
        super.onResume()
        carregarDadosDoBanco()
    }

    private fun carregarDadosDoBanco() {
        lifecycleScope.launch {
            listaCategorias = withContext(Dispatchers.IO) {
                db.categoriaDao().listarCategorias()
            }

            setupCategoriaRecyclerView(listaCategorias)

            val primeiraCategoria = listaCategorias.firstOrNull()
            if (primeiraCategoria != null) {
                filtrarItensPorCategoria(primeiraCategoria.id)
                binding.emptyStateLayout.visibility = View.GONE
            } else {
                adapterItem.atualizarLista(emptyList())
                binding.emptyStateLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupCategoriaRecyclerView(lista: List<Categoria>) {
        adapterCategoria = AdapterCategoria(lista) { categoria ->
            filtrarItensPorCategoria(categoria.id)
        }
        binding.categoriasRecyclerView.adapter = adapterCategoria
    }

    private fun filtrarItensPorCategoria(categoriaId: Int) {
        lifecycleScope.launch {
            val itensFiltrados = withContext(Dispatchers.IO) {
                db.itemColecaoDao().listarItensPorCategoria(categoriaId)
            }

            listaItens = itensFiltrados
            adapterItem.atualizarLista(itensFiltrados)

            binding.emptyStateLayout.visibility =
                if (itensFiltrados.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onItemClick(): (ItemColecao) -> Unit = { item ->
        val intent = Intent(this, DetalhesItemActivity::class.java).apply {
            putExtra("itemId", item.id)
            putExtra("nome", item.nome)
            putExtra("descricao", item.descricao)
            putExtra("imagemPrincipal", item.caminhoImagemPrincipal)
            putExtra("imagemSecundaria1", item.caminhoImagemSecundaria1)
            putExtra("imagemSecundaria2", item.caminhoImagemSecundaria2)
        }
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            carregarDadosDoBanco()
        }
    }

    private fun mostrarMenuAdicionar(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_adicionar, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_adicionar_item -> {
                    startActivity(Intent(this, AdicionarItemActivity::class.java))
                    true
                }

                R.id.menu_adicionar_categoria -> {
                    startActivity(Intent(this, AdicionarCategoriaActivity::class.java))
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}

