package el.gran.myapplication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import el.gran.myapplication.R
import kotlinx.android.synthetic.main.activity_recuperar_password.*

class RecuperarPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)


        btnRecuperarContraseña.setOnClickListener {

            val email=etReset.text.toString().trim{it <= ' '} //trim recorta los espacios del email

            if(email.isEmpty()){

                showAlert("Por favor Ingrese su email")

            }else{
                 FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                     .addOnCompleteListener { task ->
                         if (task.isSuccessful){

                             val builder = AlertDialog.Builder(this)
                             builder.setTitle("Autenticacion")
                             builder.setMessage("Se a enviado un Email con los pasos para restablecer su contraseña")
                             builder.setPositiveButton("Aceptar", { dialog, id ->
                                 startActivity(Intent(this, LoginActivity::class.java))
                                 finish()
                                     })
                             val dialog: AlertDialog =builder.create()
                             dialog.show()
                         }else{
                             showAlert("El Email ingresado es incorecto o no existe")
                         }
                     }
             }
        }
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Autenticacion")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}