package com.example.mycollection.ui

import com.example.mycollection.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.model.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class EditarCategoriaActivity : AppCompatActivity() {

    private lateinit var imgCategoria: ImageView
    private lateinit var btnSelecionarImg: Button
    private lateinit var editTextNome: EditText
    private lateinit var btnSalvar: ImageView
    private lateinit var btnVoltar: ImageView

    private var imagemSelecionadaUri: Uri? = null
    private var caminhoImagemAtual: String? = null
    private var idCategoria: Int = 0

    private val REQUEST_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_categoria)

        imgCategoria = findViewById(R.id.imgCategoria)
        btnSelecionarImg = findViewById(R.id.btnSelecionarImg)
        editTextNome = findViewById(R.id.editTextNomeCategoria)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnVoltar = findViewById(R.id.btnVoltar)

        idCategoria = intent.getIntExtra("idCategoria", 0)
        val nome = intent.getStringExtra("nomeCategoria")
        caminhoImagemAtual = intent.getStringExtra("imagemCategoria")

        editTextNome.setText(nome)

        Glide.with(this)
            .load(caminhoImagemAtual)
            .circleCrop()
            .into(imgCategoria)

        btnSelecionarImg.setOnClickListener {
            escolherImagem()
        }

        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun escolherImagem() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            imagemSelecionadaUri = data?.data

            Glide.with(this)
                .load(imagemSelecionadaUri)
                .circleCrop()
                .into(imgCategoria)
        }
    }

    private fun salvarAlteracoes() {
        val nome = editTextNome.text.toString()

        if (nome.isBlank()) {
            Toast.makeText(this, "Informe o nome da categoria", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val caminhoFinalImagem = if (imagemSelecionadaUri != null) {

                        val novoCaminho = salvarImagemLocal(imagemSelecionadaUri!!)

                        if (!caminhoImagemAtual.isNullOrEmpty()) {
                            val arquivoAntigo = File(caminhoImagemAtual!!)
                            if (arquivoAntigo.exists()) {
                                arquivoAntigo.delete()
                            }
                        }
                        novoCaminho
                    } else {
                        caminhoImagemAtual ?: ""
                    }

                    val categoria = Categoria(
                        id = idCategoria,
                        nome = nome,
                        caminhoImagem = caminhoFinalImagem
                    )

                    val db = AppDatabase.getDatabase(this@EditarCategoriaActivity)
                    db.categoriaDao().atualizar(categoria)
                }

                Toast.makeText(this@EditarCategoriaActivity, "Categoria atualizada", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@EditarCategoriaActivity, "Erro ao atualizar categoria", Toast.LENGTH_SHORT).show()
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
