package el.gran.myapplication.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import el.gran.myapplication.R


class ConfigProfileDataFragment : Fragment() {

    lateinit var cfNombre: TextView
    lateinit var cfApellido: TextView
    lateinit var cfLocalidad: TextView
    lateinit var cfDireccion: TextView
    lateinit var cfTelefono: TextView
    lateinit var cfEmail: TextView
    lateinit var updatebtn: Button

    var nombre =""
    var apellido =""
    var direccion =""
    var localidad =""
    var telefono =""
    var email =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_config_profile_data, container, false)

        cfNombre = view.findViewById(R.id.configNombre)
        cfApellido = view.findViewById(R.id.configApellido)
        cfLocalidad = view.findViewById(R.id.configLocalidad)
        cfDireccion = view.findViewById(R.id.configDireccion)
        cfTelefono = view.findViewById(R.id.configTelefono)
        cfEmail = view.findViewById(R.id.configRegistrarEmail)

        val accountPref: SharedPreferences? =
            activity?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)


        //leer datos del fichero sharedPreferences
         nombre = accountPref?.getString("nombre", "").toString()
         apellido = accountPref?.getString("apellido", "").toString()
         localidad = accountPref?.getString("localidad", "").toString()
         direccion = accountPref?.getString("direccion", "").toString()
         telefono = accountPref?.getString("telefono", "").toString()
         email = accountPref?.getString("email", "").toString()


        //escribir los datos para verificacion del usuario

        cfNombre.setText(nombre)
        cfApellido.setText(apellido)
        cfLocalidad.setText(localidad)
        cfDireccion.setText(direccion)
        cfTelefono.setText(telefono)
        cfEmail.setText(email)

        //si los datos son modificados al press el buttom se actualizan localmente y en el server

        updatebtn = view.findViewById<Button>(R.id.updateDataBtn)
        updatebtn.setOnClickListener {

            updateUserData()

        }

        return view
    }


    @SuppressLint("CommitPrefEdits")
    private fun updateUserData() {

        nombre = cfNombre.text.toString()
        apellido =  cfApellido.text.toString()
        localidad = cfLocalidad.text.toString()
        direccion = cfDireccion.text.toString()
        telefono = cfTelefono.text.toString()
        email = cfEmail.text.toString()


        val db = FirebaseFirestore.getInstance()

        //guardar localmente datos para consulta despues
        val accountPref: SharedPreferences? =
            activity?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        accountPref?.edit()?.putString("nombre", nombre)?.apply()
        accountPref?.edit()?.putString("apellido", apellido)?.apply()
        accountPref?.edit()?.putString("direccion", direccion)?.apply()
        accountPref?.edit()?.putString("localidad", localidad)?.apply()
        accountPref?.edit()?.putString("telefono", telefono)?.apply()
        accountPref?.edit()?.putString("email", email)?.apply()


        val data = hashMapOf(
            "Nombre" to nombre,
            "Apellido" to apellido,
            "Localidad" to localidad,
            "Direccion" to direccion,
            "Telefono" to telefono,
            "Email" to email,
        )


        db.collection("Usuario")
            .document(email)
            .update(data as Map<String, Any>)

        showAlert("Datos Actualizados")


    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Configuracion")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", { dialog, id ->
            findNavController().navigate(R.id.action_configProfileDataFragment_to_profileFragment)
        })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}