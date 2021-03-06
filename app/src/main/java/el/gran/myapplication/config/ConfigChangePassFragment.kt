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
                            LyContrase??a.setError(null)
                            LyContrase??a.setErrorEnabled(false)
                            LyRepContrase??a.setError(null)
                            LyRepContrase??a.setErrorEnabled(false)
                        } else {
                            // show error on input invalid password
                            LyContrase??a.setError(" ")
                            LyContrase??a.setErrorEnabled(true)
                            LyRepContrase??a.setError("Contrase??a invalida")
                            LyRepContrase??a.setErrorEnabled(true)
                        }
                    } else {
                        LyContrase??a.setError(" ")
                        LyContrase??a.setErrorEnabled(true)
                        LyRepContrase??a.setError("Las contrase??as no coinciden")
                        LyRepContrase??a.setErrorEnabled(true)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        reNewPassV.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContrase??a.setError(null)
                LyContrase??a.setErrorEnabled(false)
                LyRepContrase??a.setError(null)
                LyRepContrase??a.setErrorEnabled(false)
            }
        }
        reNewPassV.setOnFocusChangeListener { view, b ->
            if (b) {
                LyContrase??a.setError(null)
                LyContrase??a.setErrorEnabled(false)
                LyRepContrase??a.setError(null)
                LyRepContrase??a.setErrorEnabled(false)
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
                        oldPassServer = it.get("Contrase??a") as String

                    }
                } else {
                    Log.d("usuario no logeado", "usuario no logeado")
                }
            }


//                if (email != null) {
//                    db.collection("Usuario").document(email).get().addOnSuccessListener {
//                        oldPassServer = it.get("Contrase??a") as String
//
//                    }
//                }
                verificarViejaContrase??a(oldPass, oldPassServer)
                verificarContrase??a(newPass, reNewPass)


                if (verifyState) {
                    if (verifyOldState) {
//subimos la contrase??a nueva despues de ser actualizada
                        val data = hashMapOf(
                            "Contrase??a" to verifiPass,
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
                        builder.setTitle("Actualizar contrase??a")
                        builder.setMessage(
                            "Contrase??a actualizada con exito," +
                                    " debe colocar su contrase??a nueva la proxima vez"
                        )
                        builder.setPositiveButton("Aceptar", { dialog, id ->
                            findNavController().navigate(R.id.action_configChangePassFragment_to_profileFragment)
                        })
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    } else {
                        showAlert("Contrase??a anterior no coincide")
                    }
                } else {
                    showAlert("Contrase??a nueva no coincide")
                }
            }


            return view
        }


        private fun showAlert(msg: String) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Actualizar contrase??a")
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

        private fun verificarContrase??a(pass: String, repPass: String) {

            if (pass == repPass) {
                verifiPass = pass
                verifyState = true
            } else {
                verifyState = false
            }
        }

        private fun verificarViejaContrase??a(pass: String, repPass: String) {

            if (pass == repPass) {
                verifiOldPass = pass
                verifyOldState = true

            } else {
                verifyOldState = false
            }
        }

    }