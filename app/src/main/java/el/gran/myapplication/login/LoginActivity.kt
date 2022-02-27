package el.gran.myapplication.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import el.gran.myapplication.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    var errorFlag = false
    var googleError = false
    var nombre = ""
    var apellido = ""
    var direccion = ""
    var localidad = ""
    var telefono = ""
    var email = ""
    var userServer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inicioSession() //mantiene session abierta en el caso que exista una ya logeada

        editTextEmail.addTextChangedListener(afterTextChanged = {
            errorFlag = false
            showAlert()
        })

        editTextPassword.addTextChangedListener(afterTextChanged = {
            errorFlag = false
            showAlert()
        }
        )

        iniciarBtn.setOnClickListener {

            if (editTextEmail.text?.isNotEmpty() == true && editTextPassword.text?.isNotEmpty() == true) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        editTextEmail.text.toString(),
                        editTextPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHomeBasic(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            errorFlag = true
                            showAlert()
                        }
                    }
                    .addOnFailureListener {
                        errorFlag = true
                        showAlert()
                    }

            } else {
                errorFlag = true
                showAlert()
            }
        }

        googleBtn.setOnClickListener {

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            //googleSignInClient.signOut()

            startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
        }


        olvidePass.setOnClickListener {
            startActivity(Intent(this, RecuperarPasswordActivity::class.java))
            finish()
        }

        registrar.setOnClickListener {

            val registrarIntent: Intent = Intent(this, RegistrarActivity::class.java).apply {
                putExtra("registrar", "BASIC")
            }
            startActivity(registrarIntent)
            finish()

        }

    }

    private fun inicioSession() {

        val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        val email = pref.getString("email", null)
        val provider = pref.getString("provider", null)


        if (pref.getString("remember", "") == "active") {
            if (email != null && provider != null) {

                updateUserData(email)

                showHomeBasic(email, ProviderType.valueOf(provider))

            }
        }

        recordarPass.setOnCheckedChangeListener { compoundButton, b ->

            if (compoundButton.isChecked) {
                val pref2 =
                    getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
                pref2.putString("remember", "active")
                pref2.apply()
            }
        }
    }

    private fun showAlert() {


        if (errorFlag) {
            LyLoginEmail.setError(" ")
            LyLoginEmail.setErrorEnabled(true)
            LyLoginContraseña.setError("Revise sus credenciales e intente nuevamente")
            LyLoginContraseña.setErrorEnabled(true)

        } else {
            LyLoginEmail.setError(null)
            LyLoginEmail.setErrorEnabled(false)
            LyLoginContraseña.setError(null)
            LyLoginContraseña.setErrorEnabled(false)
        }

        if (googleError) {

            googleError = false
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Autenticacion")
            builder.setMessage("Revise sus credenciales e intente nuevamente")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            Log.d(this.toString(), "Task Google create account failed")

        }
    }

    //navega a la pantalla de inicio Pasando los datos para persistencia de los datos
    private fun showHomeBasic(email: String, provider: ProviderType) {
        val homeIntent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showHomeGoogle(email: String, provider: ProviderType) {
        val homeIntent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showGoogleRegister(email: String, provider: ProviderType) {
        val homeIntent: Intent = Intent(this, RegistrarActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun updateUserData(string: String) {

        val db = FirebaseFirestore.getInstance()

        db.collection("Usuario").document(string).get().addOnSuccessListener {

            nombre = it.get("Nombre") as String
            apellido = it.get("Apellido") as String
            direccion = it.get("Direccion") as String
            localidad = it.get("Localidad") as String
            telefono = it.get("Telefono") as String
            email = it.get("Email") as String

            val accountPref =
                getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()

            accountPref.putString("nombre", nombre)
            accountPref.putString("apellido", apellido)
            accountPref.putString("direccion", direccion)
            accountPref.putString("localidad", localidad)
            accountPref.putString("telefono", telefono)
            accountPref.putString("email", email)

            accountPref.apply()

        }
    }

    private fun checkUserGoogleExist(string: String) {

        val db = FirebaseFirestore.getInstance()
        db.collection("Usuario").document(string).get().addOnSuccessListener {

            userServer = (it.get("Email") as? String).toString()

            if (userServer == string) {
                val pref2 =
                    getSharedPreferences(
                        getString(R.string.pref_file),
                        Context.MODE_PRIVATE
                    ).edit()
                pref2.putString("remember", "active")
                pref2.apply()
                Log.w(this.toString(), "Usuario existe navegando hacia home....")
                showHomeGoogle(string, ProviderType.GOOGLE)
            } else {
                Log.w(this.toString(), "Usuario no existe creando....")
                showGoogleRegister(string, ProviderType.GOOGLE)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                checkUserGoogleExist(account.email.toString())

                            } else {
                                googleError = true
                                showAlert()
                                Log.d(this.toString(), "Task Google create account failed")
                            }
                        }
                }
            } catch (e: ApiException) {
                // Google Sign In fallido
                googleError = true
                showAlert()
                Log.d(this.toString(), "Task Google ApiException")

            }
        }
    }
}