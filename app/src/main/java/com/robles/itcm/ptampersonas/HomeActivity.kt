package com.robles.itcm.ptampersonas

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.databinding.ActivityHomeBinding
import org.w3c.dom.Text

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var email: String
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_persons, R.id.nav_addperson, R.id.nav_editperson, R.id.nav_profile,
                R.id.nav_logout, R.id.nav_admin
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val adminItemMenu = navView.menu.findItem(R.id.nav_admin);
        val headerView: View = navView.getHeaderView(0)
        val txtName: TextView = headerView.findViewById(R.id.txt_menu_name)
        val txtEmail: TextView = headerView.findViewById(R.id.txt_menu_email)
        val prefs = getSharedPreferences("session_data", Context.MODE_PRIVATE)
        email = SessionData.getData("email").toString()
        name = SessionData.getData("nombre").toString()

        txtName.text = name
        txtEmail.text = email

        if(SessionData.getData("admin") as Boolean)
            adminItemMenu.isVisible = true

        Toast.makeText(this, "${SessionData.getData("email")} ${SessionData.getData("admin")}", Toast.LENGTH_SHORT).show()

        }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}