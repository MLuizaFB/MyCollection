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
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.model.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AdicionarCategoriaActivity : AppCompatActivity() {

    private lateinit var imageCategoria: ImageView
    private lateinit var editTextNomeCategoria: EditText

    private var uriImagemCategoria: Uri? = null

    private val REQUEST_IMAGE = 100

    private lateinit var categoriaDao: com.example.mycollection.dao.CategoriaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_categoria)

        imageCategoria = findViewById(R.id.imagemCategoria)
        editTextNomeCategoria = findViewById(R.id.editTextNomeCategoria)

        val db = AppDatabase.getDatabase(this)
        categoriaDao = db.categoriaDao()

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnSalvar).setOnClickListener {
            salvarCategoria()
        }

        findViewById<Button>(R.id.btnSelecionarImg).setOnClickListener {
            escolherImagem()
        }
    }

    private fun escolherImagem() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE) {
            uriImagemCategoria = data?.data
            Glide.with(this).load(uriImagemCategoria).circleCrop() .into(imageCategoria)
        }
    }

    private fun salvarCategoria() {
        val nome = editTextNomeCategoria.text.toString().trim()

        if (nome.isEmpty() || uriImagemCategoria == null) {
            Toast.makeText(this, "Preencha o nome e selecione uma imagem.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val caminhoImagem = salvarImagemLocal(uriImagemCategoria!!)

                    val novaCategoria = Categoria(
                        nome = nome,
                        caminhoImagem = caminhoImagem
                    )
                    categoriaDao.inserir(novaCategoria)
                }

                Toast.makeText(this@AdicionarCategoriaActivity, "Categoria salva!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@AdicionarCategoriaActivity, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
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
