package com.example.mycollection.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mycollection.database.AppDatabase
import com.example.mycollection.databinding.ActivityDetalhesItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DetalhesItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botaoVoltar.setOnClickListener {
            finish()
        }

        val nome = intent.getStringExtra("nome")
        val descricao = intent.getStringExtra("descricao")
        val caminhoImagemPrincipal = intent.getStringExtra("imagemPrincipal")
        val imagemSecundaria1 = intent.getStringExtra("imagemSecundaria1")
        val imagemSecundaria2 = intent.getStringExtra("imagemSecundaria2")

        binding.nomeItem.text = nome
        binding.descricaoItem.text = descricao

        Glide.with(this)
            .load(caminhoImagemPrincipal)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
            .into(binding.imagemPrincipal)

        val imagensSecundarias = listOfNotNull(imagemSecundaria1, imagemSecundaria2)

        if (imagensSecundarias.isNotEmpty()) {
            binding.layoutImagensSecundarias.visibility = View.VISIBLE

            imagensSecundarias.forEach { caminho ->
                val imageView = ImageView(this)
                val params = LinearLayout.LayoutParams(450, 450)
                params.setMargins(20, 8, 20, 8)
                imageView.layoutParams = params
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                Glide.with(this)
                    .load(caminho)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
                    .into(imageView)

                binding.layoutImagensSecundarias.addView(imageView)
            }
        }
        val itemId = intent.getIntExtra("itemId", -1)

        if (itemId != -1) {
            binding.botaoExcluirItem.setOnClickListener {
                excluirItem(itemId)
            }
        } else {
            Toast.makeText(this, "Erro ao identificar o item.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun excluirItem(itemId: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir este item?")
            .setPositiveButton("Sim") { _, _ ->
                val db = AppDatabase.getDatabase(this)
                val itemDao = db.itemColecaoDao()

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val item = itemDao.buscarPorId(itemId)
                        item?.let {
                            if (!it.caminhoImagemPrincipal.isNullOrEmpty()) File(it.caminhoImagemPrincipal).delete()
                            if (!it.caminhoImagemSecundaria1.isNullOrEmpty()) File(it.caminhoImagemSecundaria1).delete()
                            if (!it.caminhoImagemSecundaria2.isNullOrEmpty()) File(it.caminhoImagemSecundaria2).delete()

                            itemDao.deletar(it)
                        }
                    }

                    Toast.makeText(
                        this@DetalhesItemActivity,
                        "Item excluído com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
