package com.aprosoft.webseries.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.aprosoft.webseries.MainActivity
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.iv_backArrow
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var userJsonObject= JSONObject()

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view =inflater.inflate(R.layout.fragment_settings, container, false)

        view.tv_account.setOnClickListener {
            val fragmentTransaction: FragmentTransaction =fragmentManager!!.beginTransaction()
            val profileFragment= ProfileFragment()
            fragmentTransaction.replace(R.id.frame_main,profileFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
        }
        view.iv_backArrow.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(context?.applicationContext,R.anim.alpha)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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


            val spreferences =this.context?.getSharedPreferences("WebseriesPref", Context.MODE_PRIVATE)
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
        val logoutParams = HashMap<String,String>()
        logoutParams["token"] = userJsonObject.getString("token")
        val call:Call<ResponseBody> = ApiClient.getClient.logout(logoutParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val success = jsonArray.getJSONObject(0).getBoolean("success")
                    if (success) {
                        Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("emptyarray","$jsonArray")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error","$t")
            }
        })
    }
}