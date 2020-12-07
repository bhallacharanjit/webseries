package com.aprosoft.webseries.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var iv_profileImage:ImageView? = null
/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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
        val v =inflater.inflate(R.layout.fragment_profile, container, false)
        val userName = v.findViewById<TextView>(R.id.tv_userName)
        val phone = v.findViewById<TextView>(R.id.tv_userPhone)
        val email = v.findViewById<TextView>(R.id.tv_userEmail)
        iv_profileImage = v.findViewById(R.id.iv_profileImage)

        v.cv_profileImage.setOnClickListener {

            com.github.dhaval2404.imagepicker.ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(512,512)
                .start()
        }
        v.iv_backArrow.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(context?.applicationContext,R.anim.alpha)
            v.iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }


        profile(userName,phone,email)
        // Inflate the layout for this fragment
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            iv_profileImage?.setImageURI(fileUri)


            //You can get File object from intent
            val file: File = ImagePicker.getFile(data)!!

            //You can also get File Path from intent
            val filePath:String = ImagePicker.getFilePath(data)!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun profile(userName: TextView, phone: TextView, email: TextView) {
        val userObject = Singleton().getUserFromSharedPrefrence(requireContext())
        val token  = userObject?.getString("token")

        val profileParams = HashMap<String,String>()
        profileParams["token"] = token.toString()
        val call:Call<ResponseBody> = ApiClient.getClient.profile(profileParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        userName.text = jsonObject.getString("name")
//                        phone.text = jsonObject.getString("")
                        email.text = jsonObject.getString("email")
//                        Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No Profile", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Array is null", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }

        })
    }
}

