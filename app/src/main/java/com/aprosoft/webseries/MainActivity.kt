package com.aprosoft.webseries

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aprosoft.webseries.Fragments.HomeFragment
import com.aprosoft.webseries.Fragments.MyListFragment
import com.aprosoft.webseries.Fragments.ProfileFragment
import com.aprosoft.webseries.Shared.Singleton
import com.aprosoft.webseries.User.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fragmentManager:FragmentManager= supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Singleton().getUserFromSharedPrefrence(this)!=null){
            BottomNavClick(bottomNav)
            bottomNav.menu.findItem(R.id.menu_profile_login).isVisible  = false
        }
        else{
            BottomNavClick(bottomNav)
            bottomNav.menu.findItem(R.id.menu_profile).isVisible = false
            bottomNav.menu.findItem(R.id.menu_profile_login).isVisible = true
            bottomNav.menu.findItem(R.id.menu_logout).isVisible = false
        }

//        val fragmentManager:FragmentManager =supportFragmentManager
        val fragmentTransaction:FragmentTransaction =fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        fragmentTransaction.add(R.id.frame_main,homeFragment)
        fragmentTransaction.commit()

//        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)





//        bottomNav.menu.removeItem(R.id.menu_logout)

    }


    private fun BottomNavClick(bottomNav: BottomNavigationView){

        bottomNav.setOnNavigationItemSelectedListener{
            when(it.itemId){

                R.id.menu_home ->{

                    intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)

                    true
                }

                R.id.myList->{

                    if (Singleton().getUserFromSharedPrefrence(this)!=null){
                        val fragmentTransaction:FragmentTransaction =fragmentManager.beginTransaction()
                        val myListFragment = MyListFragment()
                        fragmentTransaction.replace(R.id.frame_main,myListFragment)
                        fragmentTransaction.addToBackStack("Fragments")
                        fragmentTransaction.commit()
                    }else{
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
//                R.id.menu_notification ->{
//                    true
//                }
                R.id.menu_profile -> {

                    val fragmentTransaction:FragmentTransaction =fragmentManager.beginTransaction()
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.frame_main,profileFragment)
                    fragmentTransaction.addToBackStack("Fragments")
                    fragmentTransaction.commit()



                    true
                }
                R.id.menu_profile_login->{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    this.finish()

                    true
                }
                R.id.menu_logout ->{

                    logoutAlert()
                    true
                }
                else-> false
            }
        }
    }

    private fun logoutAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Are you sure ?")
        alertDialog.setMessage("you want Logout")
//        alertDialog.setIcon(R.drawable.ic_exit)
        alertDialog.setPositiveButton("Yes"){ _, _ ->

            val preferences =
                getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            finish()

            val spreferences =
                getSharedPreferences("WebseriesPref", Context.MODE_PRIVATE)
            val editor1 = spreferences.edit()
            editor1.clear()
            editor1.apply()
            finish()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("finish", true) // if you are checking for this in your other Activities
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        alertDialog.setNegativeButton("No"){ _, _ ->

        }

        alertDialog.create()
        alertDialog.show()
    }


}