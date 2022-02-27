package el.gran.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class ProviderType {
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.setupWithNavController(navController)


        //recupera datos de inicio de session
        val bundle =intent.extras
        val email  = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        //guarda los datos de inicio de session en gestor de preferencias
        val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
        pref.putString("email",email)
        pref.putString("provider",provider)
        pref.apply()

    }
}