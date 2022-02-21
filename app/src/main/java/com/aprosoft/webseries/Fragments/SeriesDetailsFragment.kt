package com.aprosoft.webseries.Fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.ReviewsAdapter
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.aprosoft.webseries.User.LoginActivity
import com.bumptech.glide.Glide
import com.jaredrummler.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.custom_actor_photos_layout.view.*
import kotlinx.android.synthetic.main.fragment_more_series.view.*
import kotlinx.android.synthetic.main.fragment_series_details.*
import kotlinx.android.synthetic.main.fragment_series_details.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



var seriesString:String? = null
var jsonObject = JSONObject()
val imageUrl = Singleton().imageUrl
var showId:String? = null
private lateinit var recyclerView: RecyclerView
var trailerKey:String?= null
var spinnerItemPostion:Int? =0
lateinit var v:View


class   SeriesDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack()
            }
        })
        seriesString= arguments?.getString("seriesObject")
        jsonObject= JSONObject(seriesString)
        showId = jsonObject.getString("token")
        trailerKey = jsonObject.getString("trailer")
        Log.d("seriesObject", "$jsonObject")
        Log.d("key", trailerKey)


        v =  inflater.inflate(R.layout.fragment_series_details, container, false)
        val webseriesPoster:ImageView = v.findViewById(R.id.iv_webseriesPoster)
        val btn_releaseYear: Button = v.findViewById(R.id.btn_releasedYear)
        val tv_title:TextView = v.findViewById(R.id.tv_title)
        val tv_storyLine:TextView = v.findViewById(R.id.tv_storyLine)
        val tv_showTitle:TextView = v.findViewById(R.id.tv_showTitle)
        val tv_showType:TextView = v.findViewById(R.id.tv_showType)
        val tv_showPremiereDate:TextView = v.findViewById(R.id.tv_showPremiereDate)
        val tv_showDescription:TextView = v.findViewById(R.id.tv_showDescription)

        v.tv_watchTime.text = jsonObject.getString("watchtime")

        if (jsonObject.getString("averageRating") == "null") {
            v.seriesDetailsRating.visibility= View.GONE
        }else{
            v.seriesDetailsRating.visibility= View.VISIBLE
            v.seriesDetailsRating.rating= jsonObject.getString("averageRating").toFloat()
        }


        v.iv_whiteHeart.setOnClickListener {
            if (Singleton().getUserFromSharedPrefrence(context!!)!=null){
                v.iv_whiteHeart.visibility = View.GONE
                v.iv_redHeart.visibility = View.VISIBLE
                addTomyList()
            }else{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        v.iv_redHeart.setOnClickListener {
            v.iv_redHeart.visibility = View.GONE
            v.iv_whiteHeart.visibility = View.VISIBLE
            removeFromList()
        }
        if (jsonObject.has("fav")){
                if (jsonObject.getString("fav")=="null"){
                }else{
                    v.iv_whiteHeart.visibility = View.GONE
                    v.iv_redHeart.visibility = View.VISIBLE
                }
        }else{
//            Toast.makeText(context, "not fav", Toast.LENGTH_SHORT).show()
        }
        recyclerView = v.findViewById(R.id.rv_reviews)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
//        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()

        if (jsonObject.getString("category")=="null"){
            v.tv_type1.visibility = View.GONE
            v.tv_type2.visibility = View.GONE
            v.tv_type3.visibility = View.GONE
        }else{
            v.tv_type1.text =  jsonObject.getString("category").substringBefore(",")
            v.tv_type3.text = jsonObject.getString("category").substringAfterLast(",")
            val category1 = jsonObject.getString("category").substringAfter(",")
            val type2 = category1.substringBefore(",")
            v.tv_type2.text =type2
        }

        v.tv_type1.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager?.beginTransaction()!!
            val bundle = Bundle()
            bundle.putString("categoryName", tv_type1.text.toString())
            val seriesByCategoryFragment = SeriesByCategoryFragment()
            fragmentTransaction.replace(R.id.frame_main, seriesByCategoryFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
            seriesByCategoryFragment.arguments = bundle
        }
        v.tv_type2.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager?.beginTransaction()!!
            val bundle = Bundle()
            bundle.putString("categoryName", tv_type2.text.toString())
            val seriesByCategoryFragment = SeriesByCategoryFragment()
            fragmentTransaction.replace(R.id.frame_main, seriesByCategoryFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
            seriesByCategoryFragment.arguments = bundle
        }
        v.tv_type3.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager?.beginTransaction()!!
            val bundle = Bundle()
            bundle.putString("categoryName", tv_type3.text.toString())
            val seriesByCategoryFragment = SeriesByCategoryFragment()
            fragmentTransaction.replace(R.id.frame_main, seriesByCategoryFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
            seriesByCategoryFragment.arguments = bundle
        }



        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        val role = userObject?.getString("role")
        if (role == "admin"){
            v.iv_Review_for_admin.visibility = View.VISIBLE
            v.iv_Review_for_admin.setOnClickListener {
                val fragmentTransaction: FragmentTransaction = fragmentManager?.beginTransaction()!!
                val bundle = Bundle()
                bundle.putString("seriesObject", "$jsonObject")
                val addReviewFragment = AddReviewFragment()
                fragmentTransaction.replace(R.id.frame_main, addReviewFragment)
                fragmentTransaction.addToBackStack("Fragments")
                fragmentTransaction.commit()
                addReviewFragment.arguments = bundle
//                Toast.makeText(context, "working", Toast.LENGTH_SHORT).show()
            }
        }else{
            v.iv_Review_for_admin.visibility = View.GONE
        }

        v.btn_rateMovie.setOnClickListener {

            if (Singleton().getUserFromSharedPrefrence(context!!)!=null){
                val fragmentTransaction: FragmentTransaction = fragmentManager?.beginTransaction()!!
                val bundle = Bundle()
                bundle.putString("seriesObject", "$jsonObject")
                val addReviewFragment = AddReviewFragment()
                fragmentTransaction.replace(R.id.frame_main, addReviewFragment)
                fragmentTransaction.addToBackStack("Fragments")
                fragmentTransaction.commit()
                addReviewFragment.arguments = bundle
            }
            else{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }


        }


        val ll_actorPhotos:LinearLayout =v.findViewById(R.id.ll_actorPhotosLayout)
        val iv_backArrow:ImageView = v.findViewById(R.id.iv_backArrow_seriesDetails)

//        Log.d("poster", imageUrl+ jsonObject.getString("webseriesposter"))

        val releasingYear = jsonObject.getString("releasingdate")
        btn_releaseYear.text = releasingYear.takeLast(4)
        tv_title.text = jsonObject.getString("showname")
        tv_storyLine.text = jsonObject.getString("storyline")
        tv_showTitle.text = jsonObject.getString("showname")
        tv_showType.text =jsonObject.getString("category")
        tv_showPremiereDate.text = jsonObject.getString("releasingdate")
        tv_showDescription.text = jsonObject.getString("showdesc")
        v.tv_showLanguage.text= jsonObject.getString("language")

        Glide.with(context!!)
            .load(imageUrl + jsonObject.getString("poster512x512"))
            .placeholder(R.drawable.ic_play_arrow_white_18dp)
            .into(webseriesPoster)


        iv_backArrow.setOnClickListener {

     //      activity?.getFragmentManager().popBackStack()

            val animation: Animation = AnimationUtils.loadAnimation(
                context?.applicationContext,
                R.anim.alpha
            )
            iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
             }

        review()

        val spinner = v.findViewById(R.id.spinner) as MaterialSpinner
        spinner.setItems("All Reviews","App reviews", "User reviwes", "imdb reviews")
        spinner.setOnItemSelectedListener { view, position, id, item ->
//            Snackbar.make(view,"Clicked $position",Snackbar.LENGTH_LONG).show()
            spinnerItemPostion= position
            if (position==0){
                review()
            }
            if (position==1){
                adminReview()
            }
            if (position==2){
                usersReview()
            }
        }

        v.btn_play_trailer.setOnClickListener {
            openYoutubeLink("$trailerKey")
            Log.d("youtubelink",""+Uri.parse("https://www.youtube.com/watch?v=$trailerKey"))
        }
        actorPhotos(ll_actorPhotos)
        return v
    }
    private fun openYoutubeLink(youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }
    }





    private fun actorPhotos(ll_actorPhotos: LinearLayout) {

        val token = jsonObject.getString("token")
//        Toast.makeText(context, "$token", Toast.LENGTH_SHORT).show()
        val inflater =context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val viewCastParams = HashMap<String, String>()
        viewCastParams["showId"] = token
        val call:Call<ResponseBody> = ApiClient.getClient.viewCast(viewCastParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        for (i in 0 until jsonArray.length()) {
                            val photosObject = jsonArray.getJSONObject(i)
                            val v: View =
                                inflater.inflate(R.layout.custom_actor_photos_layout, null)
                            val imageUrl =
                                Singleton().imageUrl + photosObject.getString("actorphoto")
                            Glide.with(context!!)
                                .load(imageUrl)
                                .into(v.iv_actorPhoto)

                            v.tv_actorName.text = photosObject.getString("actorname")
                            v.iv_actorPhoto.setOnClickListener {
                                val fragmentTransaction:FragmentTransaction = fragmentManager?.beginTransaction()!!
                                val bundle = Bundle()
                                bundle.putString("actorid",
                                    jsonArray.getJSONObject(i).getString("token")
                                )
                                bundle.putString("actorname",
                                    jsonArray.getJSONObject(i).getString("actorname")
                                )
                                val seriesByActorFragment = SeriesByActorFragment()
                                fragmentTransaction.replace(R.id.frame_main, seriesByActorFragment)
                                fragmentTransaction.addToBackStack("Fragments")
                                fragmentTransaction.commit()
                                seriesByActorFragment.arguments = bundle
                            }
                            ll_actorPhotos.addView(v)
                        }
                    } else {
//                        Toast.makeText(context, "not working", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
            }
        })
    }

    private fun review(){
        val reviewParams = HashMap<String, String>()
        reviewParams["showid"] = showId.toString()
        val call:Call<ResponseBody> = ApiClient.getClient.review(reviewParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                Log.d("jsonArray", "$jsonArray")
                if (jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val reviewsAdapter = ReviewsAdapter(context!!,jsonArray)
                        recyclerView.adapter = reviewsAdapter
                    }

                } else {
//                    Toast.makeText(context, "Array is null", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun usersReview(){
        val usersReviewParams = HashMap<String,String>()
        usersReviewParams["showid"]= showId.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.ReviewByUsers(usersReviewParams)
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res= response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length()>0){
                    for (i in 0 until jsonArray.length()) {
                        val reviewsAdapter = ReviewsAdapter(context!!,jsonArray)
                        recyclerView.adapter = reviewsAdapter
                    }
                }else{
                    Log.d("emptyArray","$jsonArray")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error","$t")
            }
        })
    }
    private fun adminReview(){
        val adminReviewParams = HashMap<String,String>()
        adminReviewParams["showid"]= showId.toString()
        val call:Call<ResponseBody> = ApiClient.getClient.ReviewByAdmin(adminReviewParams)
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res= response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length()>0){
                    for (i in 0 until jsonArray.length()) {
                        val reviewsAdapter = ReviewsAdapter(context!!,jsonArray)
                        recyclerView.adapter = reviewsAdapter
                    }
                }else{
                    Log.d("emptyArray","$jsonArray")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error","$t")
            }
        })
    }

    private fun addTomyList(){
        val showid = jsonObject.getString("token")
        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        val uid = userObject?.getString("token")
        val myListParam = HashMap<String, String>()
        myListParam["uid"] =uid.toString()
        myListParam["showid"] = showid

        val call:Call<ResponseBody> = ApiClient.getClient.addtoList(myListParam)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(context, "added to favourites", Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                    Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeFromList() {
        val showid = jsonObject.getString("token")
        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        val uid = userObject?.getString("token")
        val removeParams = HashMap<String, String>()
        removeParams["uid"] = uid.toString()
        removeParams["showid"] = showid

        val call:Call<ResponseBody> = ApiClient.getClient.removeFromList(removeParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val msg: String
                    if (jsonObject.getBoolean("success")) {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
                    } else {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}