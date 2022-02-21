 package com.aprosoft.webseries.Fragments

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.adrianotelesc.expandablesearchbar.ExpandableSearchBar
import com.aprosoft.webseries.Fragments.Platforms.AllPlatformsFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.custom_category_webseries.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


var categoryArray = JSONArray()
var uid:String?= null
//var aviLoader:AVLoadingIndicatorView?= null
var categoryViewArrayList:ArrayList<View>? = null
var categoryArrayList:ArrayList<View>?= null
var seriesArray = JSONArray()


var pageNo = 1
var count = 0


class HomeFragment : Fragment() {

    var MyShowListArray = JSONArray()
    lateinit var videoPlayer: YouTubePlayerView

    var videoId:String?= null

    private lateinit var horizontalScrollView:HorizontalScrollView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val ll_posterLayout:LinearLayout = view.findViewById(R.id.ll_photosLayout)
        //val ll_videoLayout = view.findViewById<LinearLayout>(R.id.ll_videoLayout)
        val ll_categoryLayout = view.findViewById<LinearLayout>(R.id.ll_categoryLayout)
        val ll_categoryPhotosLayout = view.findViewById<LinearLayout>(R.id.ll_categoryPhotosLayout)
        view.tv_moreSeries.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager?.beginTransaction()!!
            val moreSeriesFragment = MoreSeriesFragment()
            fragmentTransaction.replace(R.id.frame_main, moreSeriesFragment)
            fragmentTransaction.addToBackStack("Fragments")
            fragmentTransaction.commit()
        }
        val platformImageSlider:ImageSlider = view.findViewById(R.id.image_slider)

        //  horizontalScrollView = view.findViewById(R.id.horizontalScrollView)

        val call:Call<ResponseBody> =ApiClient.getClient.viewTrailer()


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                aviLoader?.visibility = View.VISIBLE
                val res = response.body()?.string()
                val trailerArray = JSONArray(res)
                Log.d("trailerLength","${trailerArray.length()}")
                val totalTrailer:Int
                if (trailerArray.length() > 0) {
                    val jsonObject = trailerArray.getJSONObject(count)
                    totalTrailer= jsonObject.getInt("total")
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        videoPlayer = view.findViewById(R.id.youtube_player)
                        videoPlayer.getPlayerUiController()
                        videoPlayer.isFullScreen()
                        videoPlayer.toggleFullScreen()

                        videoPlayer.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                videoPlayer.enterFullScreen()
                                videoPlayer.exitFullScreen()
                                videoId = jsonObject.getString("trailerId")
                                youTubePlayer.cueVideo(videoId!!, 0f)
                            }
                        })
                        randomTrailer(trailerArray)
                    } else {
                        Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
//                aviLoader?.visibility = View.GONE
            }
        })
        seriesPoster(ll_posterLayout)
        platformImageSlider(platformImageSlider)
        showCategory(ll_categoryLayout, ll_categoryPhotosLayout)
        myList()
        categoryPhotos(ll_categoryPhotosLayout, "Action")
//        trailers(ll_videoLayout)

        return view
    }

    private fun randomTrailer(jsonArray: JSONArray){
        count+=1
        Log.d("trailerCount","$count")
        if(count>= jsonArray.length()){
            count = 0
        }
    }

    private fun seriesPoster(ll_posterLayout: LinearLayout) {
        val inflater =context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val seriesParams= HashMap<String, String>()
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = Singleton().getUserFromSharedPrefrence(context!!)
            seriesParams["userId"]=userObject?.getString("token").toString()
        }else{
            seriesParams["userId"]=""
        }
        seriesParams["PageNumber"]= pageNo.toString()
        seriesParams["PageSize"]= Singleton().NUMBER_OF_RECORDS.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.viewAllsWebseries(seriesParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                seriesArray = JSONArray(res)
                Log.d("seriesArray", "$seriesArray")
                val jsonObject = seriesArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    for (i in 0 until 5) {
                        val seriesObject = seriesArray.getJSONObject(i)
                        val v: View = inflater.inflate(R.layout.custom_series_photos_layout, null)
                        val popular_webSeries_layout: LinearLayout =
                            v.findViewById(R.id.ll_popular_Webseries)
                        val seriesPoster = v.findViewById<ImageView>(R.id.iv_seriesPoster)
                        var ratingbar = v.findViewById<RatingBar>(R.id.rating)
                        val seriesName = v.findViewById<TextView>(R.id.tv_seriesName)
                        val imageUrl =
                            Singleton().imageUrl + seriesObject.getString("webseriesposter")
                        Glide.with(context!!)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_play_arrow_white_18dp)
                            .into(seriesPoster)
                        seriesName.text = seriesObject.getString("showname")

                        var rating: String? = null
                        if (seriesObject.getString("averageRating") == "null") {
                            ratingbar.visibility = View.GONE
                        } else {
                            ratingbar.visibility = View.VISIBLE
                            rating = seriesObject.getString("averageRating")
                            ratingbar.rating = rating.toFloat()
                        }

                        seriesPoster.tag = i
                        seriesPoster.setOnClickListener {
                            val fragmentTransaction: FragmentTransaction =
                                fragmentManager?.beginTransaction()!!
                            val bundle = Bundle()
                            bundle.putString("seriesObject", "$seriesObject")
                            bundle.putString("myList", "$MyShowListArray")
                            val seriesDetailsFragment = SeriesDetailsFragment()
                            fragmentTransaction.replace(R.id.frame_main, seriesDetailsFragment)
                            fragmentTransaction.addToBackStack("Fragments")
                            fragmentTransaction.commit()
                            seriesDetailsFragment.arguments = bundle
                        }

                        ll_posterLayout.addView(v)

//                        getRatingStars(ratingbar)
                    }
//                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(context, "Not done", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                Log.d("error","$t")
            }
        })
    }
    private fun platformImageSlider(platformImageSlider: ImageSlider) {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.netflix_poster))
        imageList.add(SlideModel(R.drawable.amazon_prime_poster))
        imageList.add(SlideModel(R.drawable.mx_player_poster))
        platformImageSlider.setImageList(imageList)
        platformImageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                //Toast.makeText(context, "$position", Toast.LENGTH_SHORT).show()
                if (position == 0) {
                }
                val fragmentTransaction: FragmentTransaction = fragmentManager?.beginTransaction()!!
                val allPlatformsFragment = AllPlatformsFragment()
                fragmentTransaction.replace(R.id.frame_main, allPlatformsFragment)
                fragmentTransaction.addToBackStack("Fragments")
                fragmentTransaction.commit()
            }
        })
    }

    private  fun showCategory(ll_categoryLayout: LinearLayout,ll_categoryPhotosLayout: LinearLayout) {
        val inflater =context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val call:Call<ResponseBody> = ApiClient.getClient.viewCategory()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length()>0){
                    if (categoryArrayList == null) {
                        categoryArrayList = ArrayList()
                    } else {
                        clearCategoryBackground()
                    }
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        for (i in 0 until jsonArray.length()) {
                            val categoryObject = jsonArray.getJSONObject(i)
                            val v: View = inflater.inflate(R.layout.custom_series_category_layout, null)
                            val tv_category = v.findViewById<TextView>(R.id.tv_seriesCategory)

                            tv_category.text = categoryObject.getString("CategoryName")
                            categoryArrayList!!.add(v)
                            tv_category.tag = i
                            tv_category.setOnClickListener {
                                clearCategoryBackground()
                                tv_category.setTextColor(resources.getColor(R.color.duskYellow))
                                val categoryName = categoryObject.getString("CategoryName")
                                categoryPhotos(ll_categoryPhotosLayout, categoryName)

                            }
                            ll_categoryLayout.addView(v)
                        }

                    } else {
//                    Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    clearCategoryBackground()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun categoryPhotos(ll_categoryPhotosLayout: LinearLayout, categoryType: String) {
        val categoryParams = HashMap<String, String>()
        categoryParams["categoryId"] = categoryType
        val call:Call<ResponseBody> = ApiClient.getClient.seriesByCategory(categoryParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                categoryArray = JSONArray(res)
                Log.d("categoryArray", "$categoryArray")
                if (categoryArray.length() > 0) {
                    val jsonObject = categoryArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (categoryViewArrayList == null) {
                        categoryViewArrayList = ArrayList()
                    } else {
                        clearCategoryView()
                    }
                    if (success) {
                        val inflater =
                            context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        for (i in 0 until categoryArray.length()) {
                            val categoryObject = categoryArray.getJSONObject(i)
                            val v: View = inflater.inflate(R.layout.custom_category_webseries, null)
                            v.tv_CategorySeriesName.text = categoryObject.getString("showname")
                            var rating: String? = null
                            if (categoryObject.getString("averageRating") == "null") {
                                v.rating_catgorySeries.visibility = View.GONE
                            } else {
                                v.rating_catgorySeries.visibility = View.VISIBLE
                                rating = categoryObject.getString("averageRating")
                                v.rating_catgorySeries.rating = rating.toFloat()
                            }
                            val imgUrl =
                                Singleton().imageUrl + categoryObject.getString("webseriesposter")
                            Glide.with(context!!)
                                .load(imgUrl)
                                .into(v.iv_categorySeriesPoster)
                            Log.d("category", "$categoryObject")
                            categoryViewArrayList!!.add(v)
                            ll_categoryPhotosLayout.addView(v)
                            v.iv_categorySeriesPoster.tag = i
                            v.iv_categorySeriesPoster.setOnClickListener {
                                val fragmentTransaction: FragmentTransaction =
                                    fragmentManager?.beginTransaction()!!
                                val bundle = Bundle()
                                bundle.putString("seriesObject", "$categoryObject")
                                bundle.putString("myList", "$MyShowListArray")
                                val seriesDetailsFragment = SeriesDetailsFragment()
                                fragmentTransaction.replace(R.id.frame_main, seriesDetailsFragment)
                                fragmentTransaction.addToBackStack("Fragments")
                                fragmentTransaction.commit()
                                seriesDetailsFragment.arguments = bundle
                            }
                        }
//                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                    } else {
                        //Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    clearCategoryView()
                    //Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun clearCategoryView() {
//        for (i in 0 until categoryViewArrayList!!.size) {
//            categoryViewArrayList!![i]
//        }
        ll_categoryPhotosLayout.removeAllViews()
    }
    fun clearCategoryBackground(){


        for (i in 0 until categoryArrayList!!.size) {
            val v = categoryArrayList!![i]
            val tv_category = v.findViewById<TextView>(R.id.tv_seriesCategory)
            tv_category.setTextColor(Color.WHITE)
        }

    }

    private fun myList(){
        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        uid = userObject?.getString("token")

        val listParmas = HashMap<String, String>()
        listParmas["uid"] = uid.toString()
        val call:Call<ResponseBody> = ApiClient.getClient.myShowsList(listParmas)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                MyShowListArray = JSONArray(res)
                Log.d("mylist", "$MyShowListArray")
                val msg: String
                if (MyShowListArray.length() > 0) {
                    val jsonObject = MyShowListArray.getJSONObject(0)
                    if (jsonObject.getBoolean("success")) {
                        msg = jsonObject.getString("msg")
//                        Toast.makeText(context, "$msg my list", Toast.LENGTH_SHORT).show()
                    } else {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRatingStars(ratingbar: RatingBar) {
        val starParams = HashMap<String, String>()
        starParams["showid"] =""
        val call:Call<ResponseBody> = ApiClient.getClient.review(starParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)

                if (jsonArray.length() > 0) {
                    Log.d("jsonArray", "$jsonArray")
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {


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






}