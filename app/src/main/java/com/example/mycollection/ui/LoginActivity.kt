package com.example.mycollection.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollection.MainActivity
import com.example.mycollection.R
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInBtn = findViewById<Button>(R.id.btnGoogleSignIn)
        signInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                val name = account?.displayName
                val email = account?.email
                val photoUrl = account?.photoUrl.toString()

                Toast.makeText(this, "Bem-vindo, $name!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } catch (e: ApiException) {
                Toast.makeText(this, "Erro no login: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
