package com.aprosoft.webseries.Fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aprosoft.webseries.AppReviewActivity
import com.aprosoft.webseries.MainActivity
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import kotlinx.android.synthetic.main.fragment_settings.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var userJsonObject= JSONObject()
private var reviewManager: ReviewManager? = null
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view =inflater.inflate(R.layout.fragment_settings, container, false)


        init(view)



        view.tv_account.setOnClickListener {
            val fragmentTransaction: FragmentTransaction =fragmentManager!!.beginTransaction()
            val profileFragment= ProfileFragment()
            fragmentTransaction.replace(R.id.frame_main, profileFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
        }
        view.iv_backArrow.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(
                context?.applicationContext,
                R.anim.alpha
            )
            view.iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }
        if (Singleton().getUserFromSharedPrefrence(context!!)!=null){
            userJsonObject = Singleton().getUserFromSharedPrefrence(context!!)!!
        }else{
            view.ll_acountLayout.visibility= View.GONE
            view.ll_logoutLayout.visibility=View.GONE
        }

        view.tv_Logout.setOnClickListener {
            logoutAlert()
        }
        return view
    }

    private fun logoutAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
        alertDialog.setTitle("Confirmation")
        alertDialog.setMessage("Are you sure you want to Logout?")
//        alertDialog.setIcon(R.drawable.ic_exit)
        alertDialog.setPositiveButton("Yes"){ _, _ ->


            val preferences =this.context?.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            val editor = preferences?.edit()
            editor?.clear()
            editor?.apply()
//            finish()


            val spreferences =this.context?.getSharedPreferences(
                "WebseriesPref",
                Context.MODE_PRIVATE
            )
            val editor1 = spreferences?.edit()
            editor1?.clear()
            editor1?.apply()
//            finish()

            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("finish", true) // if you are checking for this in your other Activities
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            logoutMethod()
        }
        alertDialog.setNegativeButton("No"){ _, _ ->

        }

        alertDialog.create()
        alertDialog.show()
    }

    private fun logoutMethod(){
        val logoutParams = HashMap<String, String>()
        logoutParams["token"] = userJsonObject.getString("token")
        val call:Call<ResponseBody> = ApiClient.getClient.logout(logoutParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val success = jsonArray.getJSONObject(0).getBoolean("success")
                    if (success) {
                        //Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("emptyarray", "$jsonArray")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
            }
        })
    }


    fun init(v: View){
        reviewManager = ReviewManagerFactory.create(context!!)
        val rateApp = v.findViewById<TextView>(R.id.tv_RateUs)
        rateApp.setOnClickListener {

//            val intent= Intent(context,AppReviewActivity::class.java)
//            startActivity(intent)

                    showRateApp()
        }
    }

    private fun showRateApp(){
        val request = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result
                val flow =
                    reviewManager!!.launchReviewFlow(context!! as Activity, reviewInfo)
                flow.addOnCompleteListener { task1: Task<Void?>? -> }
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog()
            }
        }
    }

    private fun showRateAppFallbackDialog() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_message)
            .setPositiveButton(R.string.rate_btn_pos) { dialog, which -> }
            .setNegativeButton(
                R.string.rate_btn_neg
            ) { dialog, which -> }
            .setNeutralButton(
                R.string.rate_btn_nut
            ) { dialog, which -> }
            .setOnDismissListener(DialogInterface.OnDismissListener { dialog: DialogInterface? -> })
            .show()
    }
}