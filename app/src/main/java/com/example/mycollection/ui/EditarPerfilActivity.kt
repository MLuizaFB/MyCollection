package com.example.mycollection.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.mycollection.R
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.databinding.ActivityEditarPerfilBinding
import com.example.mycollection.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding

    private var imagemUri: String? = null
    private val selecionarImagemRequest = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarDadosUsuario()

        binding.btnSelecionarImagem.setOnClickListener {
            selecionarImagemDaGaleria()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener {
            salvarPerfil()
        }
    }

    private fun selecionarImagemDaGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, selecionarImagemRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == selecionarImagemRequest && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {

                lifecycleScope.launch {
                    val caminhoInterno = withContext(Dispatchers.IO) {
                        salvarImagemInternamente(uri)
                    }

                    if (caminhoInterno != null) {
                        imagemUri = caminhoInterno

                        Glide.with(this@EditarPerfilActivity)
                            .load(caminhoInterno)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .circleCrop()
                            .into(binding.imgPerfilEditar)
                    }
                }
            }
        }
    }

    private fun salvarImagemInternamente(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val nomeArquivo = "imagem_perfil.jpg"
            val arquivo = File(filesDir, nomeArquivo)

            inputStream?.use { input ->
                arquivo.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            arquivo.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun carregarDadosUsuario() {
        val db = AppDatabase.getDatabase(this)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {

            val usuario = withContext(Dispatchers.IO) {
                usuarioDao.obterUsuario()
            }

            usuario?.let {
                binding.editNome.setText(it.nome)
                imagemUri = it.caminhoImagem

                if (!it.caminhoImagem.isNullOrEmpty()) {
                    val arquivoImagem = File(it.caminhoImagem)

                    Glide.with(this@EditarPerfilActivity)
                        .load(it.caminhoImagem)
                        .signature(ObjectKey(arquivoImagem.lastModified()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(binding.imgPerfilEditar)
                }
            }
        }
    }

    private fun salvarPerfil() {
        val nome = binding.editNome.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AppDatabase.getDatabase(this)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val usuarioExistente = usuarioDao.obterUsuario()

                val usuario = Usuario(
                    id = usuarioExistente?.id ?: "usuario_padrao",
                    nome = nome,
                    caminhoImagem = imagemUri
                )

                usuarioDao.inserirOuAtualizar(usuario)
            }

            Toast.makeText(this@EditarPerfilActivity, "Perfil atualizado", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
