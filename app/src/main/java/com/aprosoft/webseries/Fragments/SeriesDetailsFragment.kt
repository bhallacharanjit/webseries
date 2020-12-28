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
import kotlinx.android.synthetic.main.fragment_series_details.*
import kotlinx.android.synthetic.main.fragment_series_details.view.*
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

var seriesString:String? = null
var jsonObject = JSONObject()
var myListString:String? = null
var listArray = JSONArray()
val imageUrl = Singleton().imageUrl
var showId:String? = null
private lateinit var recyclerView: RecyclerView
var trailerKey:String?= null
var spinnerItemPostion:Int? =0
var AdminReviewArray= JSONArray()
var userReviewArray= JSONArray()
var imdbReviewArray= JSONArray()
lateinit var v:View
/**
 * A simple [Fragment] subclass.
 * Use the [SeriesDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class   SeriesDetailsFragment : Fragment() {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        seriesString= arguments?.getString("seriesObject")
        jsonObject= JSONObject(seriesString)
        showId = jsonObject.getString("token")
        trailerKey = jsonObject.getString("trailer")
        Log.d("seriesObject", "$jsonObject")
        Log.d("key", trailerKey)


  //      myListString= arguments?.getString("myList")
//        listArray = JSONArray(myListString)

        //Log.d("listarray","$listArray")
        //Toast.makeText(context, "$listArray", Toast.LENGTH_SHORT).show()
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

            val animation: Animation = AnimationUtils.loadAnimation(
                context?.applicationContext,
                R.anim.alpha
            )
            iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }


        val spinner = v.findViewById(R.id.spinner) as MaterialSpinner
        spinner.setItems("App reviews", "User reviwes", "imdb reviews")
        spinner.setOnItemSelectedListener { view, position, id, item ->
//            Snackbar.make(view,"Clicked $position",Snackbar.LENGTH_LONG).show()
            spinnerItemPostion= position
//            Toast.makeText(context, "$spinnerItemPostion", Toast.LENGTH_SHORT).show()
            review()
        }
        review()

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SeriesDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeriesDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
                                bundle.putString("actorid","${jsonArray.getJSONObject(i).getString("token")}")
                                bundle.putString("actorname","${jsonArray.getJSONObject(i).getString("actorname")}")
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
        val showid = jsonObject.getString("token")
        val reviewParams = HashMap<String, String>()
        reviewParams["showid"] = showid

        val call:Call<ResponseBody> = ApiClient.getClient.review(reviewParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                Log.d("jsonArray", "$jsonArray")


                if (jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {

                        val role = jsonArray.getJSONObject(i).getString("role")
                        if (spinnerItemPostion == 0 && role == "admin") {
                            AdminReviewArray = JSONArray()
                            val reviewObject = jsonArray.getJSONObject(i)
                            AdminReviewArray.put(reviewObject)
                            Log.d("reviewObject", "$AdminReviewArray")
                            val reviewsAdapter = ReviewsAdapter(context!!, AdminReviewArray)
                            reviewsAdapter.notifyDataSetChanged()
                            recyclerView.adapter = reviewsAdapter
                        }
                        if (spinnerItemPostion == 1 && role == "user") {
                            userReviewArray = JSONArray()
                            val reviewObject = jsonArray.getJSONObject(i)
                            userReviewArray.put(reviewObject)
                            Log.d("reviewObject", "$userReviewArray")
                            val reviewsAdapter = ReviewsAdapter(context!!, userReviewArray)
                            reviewsAdapter.notifyDataSetChanged()
                            recyclerView.adapter = reviewsAdapter
                        }
                        if (spinnerItemPostion == 2 && role == "imdb") {
                            imdbReviewArray = JSONArray()
                            val reviewObject = jsonArray.getJSONObject(i)
                            imdbReviewArray.put(reviewObject)
                            Log.d("reviewObject", "$imdbReviewArray")
                            val reviewsAdapter = ReviewsAdapter(context!!, imdbReviewArray)
                            reviewsAdapter.notifyDataSetChanged()
                            recyclerView.adapter = reviewsAdapter
                        }

                    }
//                    val jsonObject = jsonArray.getJSONObject(0)
//                    val success = jsonObject.getBoolean("success")
//                    if (success) {
//                        val reviewsAdapter = ReviewsAdapter(context!!, jsonArray)
//                        recyclerView.adapter = reviewsAdapter
//
//                        //Toast.makeText(context, "review working", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "review not working", Toast.LENGTH_SHORT).show()
//                    }
                } else {
//                    Toast.makeText(context, "Array is null", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
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