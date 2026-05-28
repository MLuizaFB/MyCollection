package com.example.mycollection.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mycollection.R
import com.example.mycollection.dao.CategoriaDao
import com.example.mycollection.dao.ItemColecaoDao
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.databinding.ActivityAdicionarItemBinding
import com.example.mycollection.model.Categoria
import com.example.mycollection.model.ItemColecao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AdicionarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding

    private lateinit var categoriaDao: CategoriaDao
    private lateinit var itemDao: ItemColecaoDao

    private var uriImagemPrincipal: Uri? = null
    private var uriImagemSec1: Uri? = null
    private var uriImagemSec2: Uri? = null

    private val REQUEST_IMAGE_PRINCIPAL = 1
    private val REQUEST_IMAGE_SECUNDARIA1 = 2
    private val REQUEST_IMAGE_SECUNDARIA2 = 3

    private var listaCategorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        categoriaDao = db.categoriaDao()
        itemDao = db.itemColecaoDao()

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener {
            salvarItem()
        }

        binding.btnSelecionarImgPrincipal.setOnClickListener {
            escolherImagem(REQUEST_IMAGE_PRINCIPAL)
        }

        binding.btnSelecionarImgSec.setOnClickListener {
            escolherImagem(REQUEST_IMAGE_SECUNDARIA1)
        }

        binding.imgSecundaria1.setOnClickListener {
            escolherImagem(REQUEST_IMAGE_SECUNDARIA1)
        }

        binding.imgSecundaria2.setOnClickListener {
            escolherImagem(REQUEST_IMAGE_SECUNDARIA2)
        }

        carregarCategoriasNoSpinner()
    }

    private fun escolherImagem(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            when (requestCode) {
                REQUEST_IMAGE_PRINCIPAL -> {
                    uriImagemPrincipal = imageUri
                    Glide.with(this).load(imageUri).into(binding.imgPrincipal)
                }
                REQUEST_IMAGE_SECUNDARIA1 -> {
                    uriImagemSec1 = imageUri
                    Glide.with(this).load(imageUri).into(binding.imgSecundaria1)
                }
                REQUEST_IMAGE_SECUNDARIA2 -> {
                    uriImagemSec2 = imageUri
                    Glide.with(this).load(imageUri).into(binding.imgSecundaria2)
                }
            }
        }
    }

    private fun carregarCategoriasNoSpinner() {
        lifecycleScope.launch {
            listaCategorias = withContext(Dispatchers.IO) {
                categoriaDao.listarCategorias()
            }

            if (listaCategorias.isEmpty()) {
                Toast.makeText(this@AdicionarItemActivity, "Adicione uma categoria antes.", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            val nomes = listaCategorias.map { it.nome }
            val adapter = ArrayAdapter(this@AdicionarItemActivity, android.R.layout.simple_spinner_item, nomes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategoria.adapter = adapter
        }
    }

    private fun salvarItem() {
        val nome = binding.editTextNome.text.toString().trim()
        val descricao = binding.editTextDescricao.text.toString().trim()

        if (nome.isEmpty() || uriImagemPrincipal == null) {
            Toast.makeText(this, "Preencha nome e imagem principal.", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaPos = binding.spinnerCategoria.selectedItemPosition
        val categoriaId = listaCategorias[categoriaPos].id

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val caminhoImagemPrincipal = uriImagemPrincipal?.let { salvarImagemLocal(it) } ?: ""
                    val caminhoImagemSec1 = uriImagemSec1?.let { salvarImagemLocal(it) }
                    val caminhoImagemSec2 = uriImagemSec2?.let { salvarImagemLocal(it) }

                    val novoItem = ItemColecao(
                        nome = nome,
                        descricao = descricao,
                        caminhoImagemPrincipal = caminhoImagemPrincipal,
                        caminhoImagemSecundaria1 = caminhoImagemSec1,
                        caminhoImagemSecundaria2 = caminhoImagemSec2,
                        categoriaId = categoriaId
                    )

                    itemDao.inserir(novoItem)
                }

                Toast.makeText(this@AdicionarItemActivity, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@AdicionarItemActivity, "Erro ao salvar o item.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarImagemLocal(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }
}
