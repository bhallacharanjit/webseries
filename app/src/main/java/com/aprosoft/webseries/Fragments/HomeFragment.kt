package com.aprosoft.webseries.Fragments

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aprosoft.webseries.Fragments.Platforms.AllPlatformsFragment
import com.aprosoft.webseries.Fragments.Platforms.PlatformSeriesFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.custom_category_webseries.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_series_details.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var categoryArray = JSONArray()
var uid:String?= null
//var aviLoader:AVLoadingIndicatorView?= null
var categoryViewArrayList:ArrayList<View>? = null
/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var MyShowListArray = JSONArray()

    private lateinit var horizontalScrollView:HorizontalScrollView

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val ll_posterLayout:LinearLayout = view.findViewById(R.id.ll_photosLayout)
        val ll_videoLayout = view.findViewById<LinearLayout>(R.id.ll_videoLayout)
        val ll_categoryLayout = view.findViewById<LinearLayout>(R.id.ll_categoryLayout)
        val ll_categoryPhotosLayout = view.findViewById<LinearLayout>(R.id.ll_categoryPhotosLayout)
//        aviLoader = view.findViewById(R.id.avi_homefrag)

        val platformImageSlider:ImageSlider = view.findViewById(R.id.image_slider)

        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)

        val call:Call<ResponseBody> =ApiClient.getClient.viewTrailer()


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                aviLoader?.visibility = View.VISIBLE
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length()>0){

                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        for (i in 0 until jsonArray.length()) {
                            val videoObject = jsonArray.getJSONObject(i)
                            Log.d("counting","$i")
                            val v:View = inflater.inflate(R.layout.custom_video_layout, null)
                            val videoPlayer:YouTubePlayerView = v.findViewById(R.id.youtube_player_view)

                            videoPlayer.getPlayerUiController()


//                            videoPlayer.isFullScreen()
//                            videoPlayer.toggleFullScreen()

                            val params = videoPlayer.layoutParams
                            val displayMetrics = DisplayMetrics()
                            this@HomeFragment.activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                            val width = displayMetrics.widthPixels
                            params.width = width
                            videoPlayer.layoutParams = params
                            lifecycle.addObserver(videoPlayer)
                            videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {

                                    videoPlayer.enterFullScreen()
                                    videoPlayer.exitFullScreen()
//                                val videoId = "xMKzdQrC5TI"
                                    val videoId = videoObject.getString("trailerId")
//                    var videoId = null
                                    youTubePlayer.cueVideo(videoId, 0f)

                                }
                            })
                            ll_videoLayout.addView(v)
                        }
                    }else{
                        Toast.makeText(context, " not Working", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error","$t")
//                aviLoader?.visibility = View.GONE
            }
        })
        seriesPoster(ll_posterLayout)
        platformImageSlider(platformImageSlider)
        showCategory(ll_categoryLayout, ll_categoryPhotosLayout)
        myList()
        categoryPhotos(ll_categoryPhotosLayout,"Action")
//        trailers(ll_videoLayout)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun seriesPoster(ll_posterLayout: LinearLayout) {
//        aviLoader?.visibility = View.VISIBLE
        val inflater =context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val seriesParams= HashMap<String,String>()
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = Singleton().getUserFromSharedPrefrence(context!!)
            seriesParams["userId"]=userObject?.getString("token").toString()
        }else{
            //Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            seriesParams["userId"]=""
        }
       val call:Call<ResponseBody> = ApiClient.getClient.viewAllsWebseries(seriesParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                aviLoader?.visibility = View.GONE
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    for (i in 0 until jsonArray.length()) {
                        val seriesObject = jsonArray.getJSONObject(i)
                        val v: View = inflater.inflate(R.layout.custom_series_photos_layout, null)
                        val popular_webSeries_layout: LinearLayout =
                            v.findViewById(R.id.ll_popular_Webseries)
                        val seriesPoster = v.findViewById<ImageView>(R.id.iv_seriesPoster)
                        val seriesName = v.findViewById<TextView>(R.id.tv_seriesName)
                        val imageUrl =
                            Singleton().imageUrl + seriesObject.getString("webseriesposter")
                        Glide.with(context!!)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_play_arrow_white_18dp)
                            .into(seriesPoster)
                        seriesName.text = seriesObject.getString("showname")

                        seriesPoster.tag = i
                        seriesPoster.setOnClickListener {
//                            val name = seriesObject.getString("showname")
//                            Toast.makeText(context, "$name", Toast.LENGTH_SHORT).show()

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

//                        popular_webSeries_layout.tag = i
//                        popular_webSeries_layout.setOnClickListener {
//
////                val intent  = Intent(this,)
////                            Toast.makeText(context, "working", Toast.LENGTH_SHORT).show()
//                        }
                        ll_posterLayout.addView(v)
                    }
//                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Not done", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
//                aviLoader?.visibility = View.GONE
            }
        })
    }

    private fun platformImageSlider(platformImageSlider: ImageSlider) {

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.netflix_poster, "Netflix"))
        imageList.add(SlideModel(R.drawable.amazon_prime_poster, "Amazon Prime"))
        imageList.add(SlideModel(R.drawable.mx_player_poster, "MX player original"))
        platformImageSlider.setImageList(imageList)

        platformImageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                //Toast.makeText(context, "$position", Toast.LENGTH_SHORT).show()
                val fragmentTransaction: FragmentTransaction = fragmentManager?.beginTransaction()!!
                val allPlatformsFragment = AllPlatformsFragment()
                fragmentTransaction.replace(R.id.frame_main, allPlatformsFragment)
                fragmentTransaction.addToBackStack("Fragments")
                fragmentTransaction.commit()
            }
        })
    }

    private  fun showCategory(ll_categoryLayout: LinearLayout, ll_categoryPhotosLayout: LinearLayout) {
//        aviLoader?.visibility = View.VISIBLE
        val inflater =context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val call:Call<ResponseBody> = ApiClient.getClient.viewCategory()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                aviLoader?.visibility = View.GONE
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    for (i in 0 until jsonArray.length()) {
                        val categoryObject = jsonArray.getJSONObject(i)
                        val v: View = inflater.inflate(R.layout.custom_series_category_layout, null)
                        val tv_category = v.findViewById<TextView>(R.id.tv_seriesCategory)

                        tv_category.text = categoryObject.getString("CategoryName")

                        tv_category.tag = i

                        tv_category.setOnClickListener {
//                            tv_category.setBackgroundResource(R.drawable.button_background)
                            val token = categoryObject.getString("token")
                            val categoryName = categoryObject.getString("CategoryName")
//                            Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
                            categoryPhotos(ll_categoryPhotosLayout,categoryName)
                        }
//                    Toast.makeText(context, "category", Toast.LENGTH_SHORT).show()
                        ll_categoryLayout.addView(v)
                    }
                } else {
//                    Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
//                aviLoader?.visibility = View.GONE

            }

        })
    }

    private fun categoryPhotos(ll_categoryPhotosLayout: LinearLayout, categoryType: String) {
//        aviLoader?.visibility = View.VISIBLE
        val categoryParams = HashMap<String,String>()
        categoryParams["categoryId"] = categoryType
        val call:Call<ResponseBody> = ApiClient.getClient.seriesByCategory(categoryParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                aviLoader?.visibility = View.GONE
                val res = response.body()?.string()
                categoryArray = JSONArray(res)
                val arryOfString = ArrayList<String>()
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
                        Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    clearCategoryView()
                    Toast.makeText(context, "nothing to show", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
//                aviLoader?.visibility = View.GONE
            }
        })
    }

    fun clearCategoryView() {
//        for (i in 0 until categoryViewArrayList!!.size) {
//            categoryViewArrayList!![i]
//        }
        ll_categoryPhotosLayout.removeAllViews()
    }

    private fun myList(){
        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        uid = userObject?.getString("token")

        val listParmas = HashMap<String,String>()
        listParmas["uid"] = uid.toString()
        val call:Call<ResponseBody> = ApiClient.getClient.myShowsList(listParmas)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                MyShowListArray= JSONArray(res)
                Log.d("mylist","$MyShowListArray")
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




}