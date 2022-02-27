package el.gran.myapplication.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import el.gran.myapplication.R
import el.gran.myapplication.login.LoginActivity


class ProfileFragment : Fragment() {
    lateinit var datosbtn: ImageButton
    lateinit var changebtn: ImageButton
    lateinit var logOutTv:TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_profile, container, false)

         datosbtn = view.findViewById<ImageButton>(R.id.datosBtn)
        datosbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                findNavController().navigate(R.id.action_profileFragment_to_configProfileDataFragment)
            }
        })

        changebtn = view.findViewById<ImageButton>(R.id.changeBtn)
        changebtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                findNavController().navigate(R.id.action_profileFragment_to_configChangePassFragment)
            }
        })

        logOutTv = view.findViewById<TextView>(R.id.logOut)
        logOutTv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                logOut()
            }
        })

        return view
    }

    private fun logOut(){
        val accountPref : SharedPreferences? = activity?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        accountPref?.edit()?.remove("email")?.apply()
        accountPref?.edit()?.remove("provider")?.apply()
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)

    }
}



