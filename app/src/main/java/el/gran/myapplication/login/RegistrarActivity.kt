package el.gran.myapplication.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import el.gran.myapplication.MainActivity
import el.gran.myapplication.ProviderType
import el.gran.myapplication.R
import kotlinx.android.synthetic.main.activity_registrar.*
import java.util.regex.Pattern


class RegistrarActivity : AppCompatActivity() {


    var contraseñaVerificada = ""
    var checkFlag = false
    var passFlag = false
    var bundle_Email = ""
    var bundle_Provider = ""
    var bundle_Registrar = ""

    //url de terminos y condiciones
    var urlTerminos = "plimplimplim"


    /*Creamos una instancia para guardar los datos del usuario en nuestra base  de datos*/
    val db = FirebaseFirestore.getInstance()

    /*Creamos una instancia para crear nuestra autenticación y guardar el usuario*/
    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)


        //recupera datos de inicio de session
        val bundle = intent.extras
        bundle_Email = bundle?.getString("email").toString()
        bundle_Provider = bundle?.getString("provider").toString()
        bundle_Registrar = bundle?.getString("registrar").toString()
        //guarda los datos de inicio de session en gestor de preferencias
        val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
        pref.putString("email", bundle_Email)
        pref.putString("provider", bundle_Provider)
        pref.putString("registrar", bundle_Registrar)
        pref.apply()

        linkTerminos.setOnClickListener {
            urlTerminos
        }

        checkTerminos.setOnCheckedChangeListener { compoundButton, b ->

            if (compoundButton.isChecked) {
                checkFlag = true
            } else {
                checkFlag = false
                showAlert("Debe aceptar nuestros terminos y condiciones")
            }
        }
        editTextRepContraseña.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.apply {
                    // check user input a valid formatted password
                    if (editTextContraseña.text.toString()
                            .contentEquals(editTextRepContraseña.text.toString())
                    ) {
                        if (isValidPassword() && toString().length >= 8) {
                            LyContraseña.setError(null)
                            LyContraseña.setErrorEnabled(false)
                            LyRepContraseña.setError(null)
                            LyRepContraseña.setErrorEnabled(false)
                        } else {
                            // show error on input invalid password
                            LyContraseña.setError(" ")
                            LyContraseña.setErrorEnabled(true)
                            LyRepContraseña.setError("Contraseña invalida")
                            LyRepContraseña.setErrorEnabled(true)
                        }
                    } else {
                        LyContraseña.setError(" ")
                        LyContraseña.setErrorEnabled(true)
                        LyRepContraseña.setError("Las contraseñas no coinciden")
                        LyRepContraseña.setErrorEnabled(true)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        editTextRepContraseña.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContraseña.setError(null)
                LyContraseña.setErrorEnabled(false)
                LyRepContraseña.setError(null)
                LyRepContraseña.setErrorEnabled(false)
            }
        }
        editTextContraseña.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContraseña.setError(null)
                LyContraseña.setErrorEnabled(false)
                LyRepContraseña.setError(null)
                LyRepContraseña.setErrorEnabled(false)
            }
        }

        updateDataBtn.setOnClickListener {

            if (checkFlag) {
                createNewAccount()
            }
        }
    }


    private fun createNewAccount() {

        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val localidad = editTextLocalidad.text.toString()
        val direccion = editTextDireccion.text.toString()
        val telefono = editTextTelefono.text.toString()
        val email = editTextRegistrarEmail.text.toString()
        var contraseña = editTextContraseña.text.toString()
        var repContraseña = editTextRepContraseña.text.toString()

//Verificamos que los campos estén llenos
        if (!TextUtils.isEmpty(nombre)
            && !TextUtils.isEmpty(apellido)
            && !TextUtils.isEmpty(localidad)
            && !TextUtils.isEmpty(direccion)
            && !TextUtils.isEmpty(telefono)
            && !TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(contraseña)
            && !TextUtils.isEmpty(repContraseña)

        ) {

            verificarContraseña(contraseña, repContraseña)
            log("edit text con datos validos->>" + email + "---" + contraseñaVerificada)

            if (passFlag) {

                //guardar localmente datos para consulta despues
                val accountPref =
                    getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
                accountPref.clear().apply()
                accountPref.putString("nombre", nombre)
                accountPref.putString("apellido", apellido)
                accountPref.putString("direccion", direccion)
                accountPref.putString("localidad", localidad)
                accountPref.putString("telefono", telefono)
                accountPref.putString("email", email)
                accountPref.apply()

                if (bundle_Provider == "GOOGLE") {

                    val user: FirebaseUser = auth.currentUser!!
                    val db = FirebaseFirestore.getInstance()

                    val pref2 = getSharedPreferences(
                        getString(R.string.pref_file),
                        Context.MODE_PRIVATE
                    ).edit()
                    pref2.putString("remember", "active")
                    pref2.apply()

                    val data = hashMapOf(
                        "Nombre" to nombre,
                        "Apellido" to apellido,
                        "Localidad" to localidad,
                        "Direccion" to direccion,
                        "Telefono" to telefono,
                        "Email" to email,
                        "Contraseña" to contraseñaVerificada
                    )
                    db.collection("Usuario").document(email)
                        .set(data)

                    verifyEmail(user)

                    showHomeGoogle(email, ProviderType.GOOGLE)

                } else {
                    if (bundle_Registrar == "BASIC") {

                        auth.createUserWithEmailAndPassword(email, contraseñaVerificada)
                            .addOnCompleteListener {

                                if (it.isSuccessful) {

                                    val user: FirebaseUser = auth.currentUser!!

                                    val data = hashMapOf(
                                        "Nombre" to nombre,
                                        "Apellido" to apellido,
                                        "Localidad" to localidad,
                                        "Direccion" to direccion,
                                        "Telefono" to telefono,
                                        "Email" to email,
                                        "Contraseña" to contraseñaVerificada
                                    )

                                    //Damos de alta la información del usuario
                                    val reference = db.collection("Usuario").document(email)
                                    reference.set(data)

                                    //enviamos email de verificación a la cuenta del usuario
                                    verifyEmail(user)
                                }
                            }.addOnFailureListener {
                                // si el registro falla se mostrara este mensaje
                                log("Error en el registro de usuario")
                                showAlert("Ya existe una cuenta asosiada a ese Email")
                            }
                    }
                }
            } else {
                showAlert("Las contraseñas no coinciden, por favor verifique")
            }
        } else {
            log("formulario vacio")
            showAlert("Debe rellenar todo el formulario")
        }
    }

    private fun verifyEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener(this) {
                //Verificamos que la tarea se realizó correctamente
                    task ->
                if (task.isSuccessful) {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Registro")
                    builder.setMessage("Registro completado, se a enviado un mail de verificacion ")
                    builder.setPositiveButton("Aceptar", { dialog, id ->
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    })

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    showAlert("Error al verificar su E-mail")
                }
            }
    }

    private fun verificarContraseña(pass: String, repPass: String) {

        if (pass == repPass) {
            contraseñaVerificada = pass
            passFlag = true
            log("contraseña verificada con exito")
        } else {
            passFlag = false
        }
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registro")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // validate password rules/patterns
    fun CharSequence.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    private fun log(msg: String) {
        val TAG = "el.gran.myapplication"
        Log.d(TAG, msg)
    }

    private fun showHomeGoogle(email: String, provider: ProviderType) {
        val homeIntent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
