package com.aprosoft.webseries

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aprosoft.webseries.Fragments.HomeFragment
import com.aprosoft.webseries.Fragments.MyListFragment
import com.aprosoft.webseries.Fragments.ProfileFragment
import com.aprosoft.webseries.Fragments.SettingsFragment
import com.aprosoft.webseries.Shared.Singleton
import com.aprosoft.webseries.User.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.BannerListener
import com.ironsource.mediationsdk.sdk.InterstitialListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_PERMISSION_CODE = 123
    private val fragmentManager:FragmentManager= supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val banner = IronSource.createBanner(this, ISBannerSize.BANNER)



        requestNotificationPermission()


        if (Singleton().getUserFromSharedPrefrence(this)!=null){
            BottomNavClick(bottomNav)
            bottomNav.menu.findItem(R.id.menu_profile_login).isVisible  = false
        }
        else{
            BottomNavClick(bottomNav)
            bottomNav.menu.findItem(R.id.menu_profile).isVisible = false
            bottomNav.menu.findItem(R.id.myList).isVisible= false
            bottomNav.menu.findItem(R.id.menu_profile_login).isVisible = true
            bottomNav.menu.findItem(R.id.menu_settings).isVisible = true
        }

//        val fragmentManager:FragmentManager =supportFragmentManager
        val fragmentTransaction:FragmentTransaction =fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        fragmentTransaction.add(R.id.frame_main, homeFragment)
        fragmentTransaction.commit()

//        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        //Init Interstitial

        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
//                TODO("Not yet implemented")
                IronSource.showInterstitial("DefaultInterstitial")
            }

            override fun onInterstitialAdLoadFailed(p0: IronSourceError?) {
//                TODO("Not yet implemented")
            }

            override fun onInterstitialAdOpened() {
//                TODO("Not yet implemented")
            }

            override fun onInterstitialAdClosed() {
//                TODO("Not yet implemented")
            }

            override fun onInterstitialAdShowSucceeded() {
//                TODO("Not yet implemented")
            }

            override fun onInterstitialAdShowFailed(p0: IronSourceError?) {
//                TODO("Not yet implemented")
            }

            override fun onInterstitialAdClicked() {
//                TODO("Not yet implemented")
            }

        })



        IronSource.init(this, "e5c92431", IronSource.AD_UNIT.INTERSTITIAL)
        IronSource.loadInterstitial()

        bannerAdd()



//        bottomNav.menu.removeItem(R.id.menu_logout)

    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            == PackageManager.PERMISSION_GRANTED
        ) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        ) {
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {

        // Checking the request code of our request
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(
                    this,
                    "Permission granted now you can read the storage",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }

    private fun BottomNavClick(bottomNav: BottomNavigationView){

        bottomNav.setOnNavigationItemSelectedListener{
            when(it.itemId){

                R.id.menu_home -> {

                    val fragmentTransaction: FragmentTransaction =
                        fragmentManager.beginTransaction()
                    val homeFragment = HomeFragment()
                    fragmentTransaction.replace(R.id.frame_main, homeFragment)
                    fragmentTransaction.addToBackStack("Fragments")
                    fragmentTransaction.commit()

//                    intent = Intent(this,MainActivity::class.java)
//                    startActivity(intent)

                    true
                }

                R.id.myList -> {
                    if (Singleton().getUserFromSharedPrefrence(this) != null) {
                        val fragmentTransaction: FragmentTransaction =
                            fragmentManager.beginTransaction()
                        val myListFragment = MyListFragment()
                        fragmentTransaction.replace(R.id.frame_main, myListFragment)
                        fragmentTransaction.addToBackStack("Fragments")
                        fragmentTransaction.commit()
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
//                R.id.menu_notification ->{
//                    true
//                }
                R.id.menu_profile -> {

                    val fragmentTransaction: FragmentTransaction =
                        fragmentManager.beginTransaction()
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.frame_main, profileFragment)
                    fragmentTransaction.addToBackStack("Fragments")
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_profile_login -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    this.finish()

                    true
                }
                R.id.menu_settings -> {
                    val fragmentTransaction: FragmentTransaction =
                        fragmentManager.beginTransaction()
                    val settingsFragment = SettingsFragment()
                    fragmentTransaction.replace(R.id.frame_main, settingsFragment)
                    fragmentTransaction.addToBackStack("Fragments")
                    fragmentTransaction.commit()
                    true
                }
                else-> false
            }
        }
    }

    private fun bannerAdd(){
        IronSource.init(this, "e5c92431", IronSource.AD_UNIT.BANNER)
        val bannerContainer: FrameLayout = findViewById(R.id.bannerContainer)
        val banner = IronSource.createBanner(this, ISBannerSize.BANNER)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        bannerContainer.addView(banner, 0, layoutParams)
        banner.bannerListener = object : BannerListener {
            override fun onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
                banner.visibility = View.VISIBLE
                bannerContainer.visibility= View.VISIBLE
            }

            override fun onBannerAdLoadFailed(error: IronSourceError) {
                // Called after a banner has attempted to load an ad but failed.
                runOnUiThread { bannerContainer.removeAllViews() }
            }

            override fun onBannerAdClicked() {
                // Called after a banner has been clicked.
            }

            override fun onBannerAdScreenPresented() {
                // Called when a banner is about to present a full screen content.
            }

            override fun onBannerAdScreenDismissed() {
                // Called after a full screen content has been dismissed
            }

            override fun onBannerAdLeftApplication() {
                // Called when a user would be taken out of the application context.
            }
        }
        IronSource.loadBanner(banner)
    }


}