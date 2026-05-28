package com.example.mycollection.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.mycollection.adapter.AdapterCategoriaPerfil
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.databinding.ActivityPerfilBinding
import com.example.mycollection.model.Categoria
import com.example.mycollection.model.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    private lateinit var adapter: AdapterCategoriaPerfil
    private var listaCategorias = listOf<Categoria>()

    private lateinit var googleSignInClient: GoogleSignInClient

    private var modoExclusao = false

    private val requestEditarPerfil = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.recyclerCategoriasPerfil.layoutManager = LinearLayoutManager(this)

        adapter = AdapterCategoriaPerfil(
            listaCategorias,
            onClickExcluir = { categoria -> excluirCategoria(categoria) },
            onClickEditar = { categoria ->
                val intent = Intent(this, EditarCategoriaActivity::class.java)
                intent.putExtra("idCategoria", categoria.id)
                intent.putExtra("nomeCategoria", categoria.nome)
                intent.putExtra("imagemCategoria", categoria.caminhoImagem)
                startActivity(intent)
            }
        )
        binding.recyclerCategoriasPerfil.adapter = adapter

        carregarDadosUsuario()
        carregarCategorias()

        binding.btnVoltar.setOnClickListener { finish() }

        binding.btnDeslogar.setOnClickListener { deslogar() }

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivityForResult(intent, requestEditarPerfil)
        }

        binding.btnEditarCategorias.setOnClickListener {
            modoExclusao = !modoExclusao
            adapter.setModoExclusao(modoExclusao)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestEditarPerfil && resultCode == Activity.RESULT_OK) {
            carregarDadosUsuario()
        }
    }

    private fun carregarDadosUsuario() {
        val db = AppDatabase.getDatabase(this)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {
            val usuario = withContext(Dispatchers.IO) {
                usuarioDao.obterUsuario()
            }

            if (usuario != null) {
                binding.txtNomeUsuario.text = usuario.nome

                if (!usuario.caminhoImagem.isNullOrEmpty()) {
                    val isLocalFile = !usuario.caminhoImagem!!.startsWith("http")

                    val glideReq = Glide.with(this@PerfilActivity)
                        .load(usuario.caminhoImagem)
                        .circleCrop()
                    if (isLocalFile) {
                        val arquivoImagem = File(usuario.caminhoImagem!!)
                        glideReq.signature(ObjectKey(arquivoImagem.lastModified()))
                    }

                    glideReq.into(binding.imgPerfil)
                }
            } else {
                val account = GoogleSignIn.getLastSignedInAccount(this@PerfilActivity)
                binding.txtNomeUsuario.text = account?.displayName

                Glide.with(this@PerfilActivity)
                    .load(account?.photoUrl)
                    .circleCrop()
                    .into(binding.imgPerfil)

                withContext(Dispatchers.IO) {
                    val novoUsuario = Usuario(
                        id = account?.id ?: "usuario_padrao",
                        nome = account?.displayName ?: "Usuário",
                        caminhoImagem = account?.photoUrl?.toString()
                    )
                    usuarioDao.inserirOuAtualizar(novoUsuario)
                }
            }
        }
    }

    private fun carregarCategorias() {
        val db = AppDatabase.getDatabase(this)
        val categoriaDao = db.categoriaDao()
        lifecycleScope.launch {
            val lista = withContext(Dispatchers.IO) {
                categoriaDao.listarCategorias()
            }
            adapter.atualizarLista(lista)
        }
    }

    private fun excluirCategoria(categoria: Categoria) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir a categoria \"${categoria.nome}\"?")
            .setPositiveButton("Sim") { _, _ ->
                val db = AppDatabase.getDatabase(this)
                val categoriaDao = db.categoriaDao()

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val itensDaCategoria = db.itemColecaoDao().listarItensPorCategoria(categoria.id)

                        for (item in itensDaCategoria) {
                            if (!item.caminhoImagemPrincipal.isNullOrEmpty()) File(item.caminhoImagemPrincipal).delete()
                            if (!item.caminhoImagemSecundaria1.isNullOrEmpty()) File(item.caminhoImagemSecundaria1).delete()
                            if (!item.caminhoImagemSecundaria2.isNullOrEmpty()) File(item.caminhoImagemSecundaria2).delete()
                        }

                        if (!categoria.caminhoImagem.isNullOrEmpty()) {
                            val arquivoImagem = File(categoria.caminhoImagem!!)
                            if (arquivoImagem.exists()) arquivoImagem.delete()
                        }

                        categoriaDao.deletar(categoria)
                    }

                    Toast.makeText(this@PerfilActivity, "Categoria excluída", Toast.LENGTH_SHORT).show()
                    carregarCategorias()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun deslogar() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, "Deslogado com sucesso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

