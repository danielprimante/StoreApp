package el.gran.myapplication.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import el.gran.myapplication.R
import kotlinx.android.synthetic.main.fragment_config_change_pass.*
import java.util.regex.Pattern


var oldPass = ""
var oldPassServer = ""
var verifiOldPass = ""
var newPass = ""
var reNewPass = ""
var verifiPass = ""
var verifyState = true
var verifyOldState = true

class ConfigChangePassFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_config_change_pass, container, false)

        val oldPassV = view.findViewById<TextView>(R.id.editTextOldPass)
        val newPassV = view.findViewById<TextView>(R.id.editTextNewPass)
        val reNewPassV = view.findViewById<TextView>(R.id.editTextReNewPass)
        val updatePassBtn = view.findViewById<Button>(R.id.updateDataBtn)

        reNewPassV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.apply {
                    // check user input a valid formatted password
                    if (newPassV.text.toString()
                            .contentEquals(reNewPassV.text.toString())
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

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        reNewPassV.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContraseña.setError(null)
                LyContraseña.setErrorEnabled(false)
                LyRepContraseña.setError(null)
                LyRepContraseña.setErrorEnabled(false)
            }
        }
        reNewPassV.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContraseña.setError(null)
                LyContraseña.setErrorEnabled(false)
                LyRepContraseña.setError(null)
                LyRepContraseña.setErrorEnabled(false)
            }
        }

        updatePassBtn.setOnClickListener {

            oldPass = oldPassV.text.toString()
            newPass = newPassV.text.toString()
            reNewPass = reNewPassV.text.toString()

            val accountPref: SharedPreferences? =
                activity?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
            val email = accountPref?.getString("email", null)

            val db = FirebaseFirestore.getInstance()

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                if (email != null) {
                    db.collection("Usuario").document(email).get().addOnSuccessListener {
                        oldPassServer = it.get("Contraseña") as String

                    }
                } else {
                    Log.d("usuario no logeado", "usuario no logeado")
                }
            }


//                if (email != null) {
//                    db.collection("Usuario").document(email).get().addOnSuccessListener {
//                        oldPassServer = it.get("Contraseña") as String
//
//                    }
//                }
                verificarViejaContraseña(oldPass, oldPassServer)
                verificarContraseña(newPass, reNewPass)


                if (verifyState) {
                    if (verifyOldState) {
//subimos la contraseña nueva despues de ser actualizada
                        val data = hashMapOf(
                            "Contraseña" to verifiPass,
                        )

                        if (email != null) {
                            db.collection("Usuario")
                                .document(email)
                                .update(data as Map<String, Any>)
                        }

                        val accountPref: SharedPreferences? =
                            activity?.getSharedPreferences(
                                getString(R.string.pref_file),
                                Context.MODE_PRIVATE
                            )

                        accountPref?.edit()?.clear()?.apply()

                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setTitle("Actualizar contraseña")
                        builder.setMessage(
                            "Contraseña actualizada con exito," +
                                    " debe colocar su contraseña nueva la proxima vez"
                        )
                        builder.setPositiveButton("Aceptar", { dialog, id ->
                            findNavController().navigate(R.id.action_configChangePassFragment_to_profileFragment)
                        })
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    } else {
                        showAlert("Contraseña anterior no coincide")
                    }
                } else {
                    showAlert("Contraseña nueva no coincide")
                }
            }


            return view
        }


        private fun showAlert(msg: String) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Actualizar contraseña")
            builder.setMessage(msg)
            builder.setPositiveButton("Aceptar", { dialog, id ->
                //findNavController().navigate(R.id.action_configProfileDataFragment_to_profileFragment)
            })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun CharSequence.isValidPassword(): Boolean {
            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
            val pattern = Pattern.compile(passwordPattern)
            val matcher = pattern.matcher(this)
            return matcher.matches()
        }

        private fun verificarContraseña(pass: String, repPass: String) {

            if (pass == repPass) {
                verifiPass = pass
                verifyState = true
            } else {
                verifyState = false
            }
        }

        private fun verificarViejaContraseña(pass: String, repPass: String) {

            if (pass == repPass) {
                verifiOldPass = pass
                verifyOldState = true

            } else {
                verifyOldState = false
            }
        }

    }