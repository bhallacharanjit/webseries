package com.aprosoft.webseries.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RatingBar
import android.widget.Toast
import com.aprosoft.webseries.MainActivity
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_review.*
import kotlinx.android.synthetic.main.fragment_add_review.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddReviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        seriesString= arguments?.getString("seriesObject")
        jsonObject= JSONObject(seriesString)
//        Log.d("stringObject","$jsonObject")
        // Inflate the layout for this fragment

        val v = inflater.inflate(R.layout.fragment_add_review,container,false)
        val ratingBar = v.findViewById<RatingBar>(R.id.rBar)


        if (ratingBar!=null){
            v.btn_SaveReview.setOnClickListener {
                val rating = ratingBar.rating.toString()
                addReview(rating)
//                Toast.makeText(context, rating, Toast.LENGTH_SHORT).show()
            }
        }
        val imgUrl = Singleton().imageUrl + jsonObject.getString("poster512x512")
        Glide.with(context!!)
            .load(imgUrl)
            .into(v.iv_ReviewSeriesPoster)
        v.tv_Review_Series_Title.text = jsonObject.getString("showname")

        v.iv_backArrow.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(context?.applicationContext,R.anim.alpha)
            iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }




        return v
    }

    private fun addReview(rating: String) {

        val todayDate: Date = Date()
        @Suppress("SimpleDateFormat")
        val time: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val datetime=time.format(todayDate)

        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        val userid = userObject?.getString("token")
        val showid = jsonObject.getString("token")
//        Log.d("userid","$userid")
//        Log.d("showid", showid)




        val reviewParams = HashMap<String,String>()
        reviewParams["showid"] = showid
        reviewParams["userid"] = userid.toString()
        reviewParams["star"] = rating
        reviewParams["desc"] = et_ReviewDesc.text.toString()
        reviewParams["date"] = datetime

        val call:Call<ResponseBody> = ApiClient.getClient.addReview(reviewParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                Log.d("array","$jsonArray")
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {

                        val intent = Intent(context,MainActivity::class.java)
                        startActivity(intent)

//                        Toast.makeText(context, "review", Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(context, "not review", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                    Toast.makeText(context, "Nothing to show", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }

        })


    }

}