package com.aprosoft.webseries.Fragments.Platforms

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.MyListAdapter
import com.aprosoft.webseries.Adapters.PlatformsSeriesAdapter
import com.aprosoft.webseries.Fragments.SeriesDetailsFragment
import com.aprosoft.webseries.Fragments.jsonArray

import com.aprosoft.webseries.Fragments.uid
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.aprosoft.webseries.User.LoginActivity
import kotlinx.android.synthetic.main.fragment_platform_series.view.*
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
lateinit var seriesView:View
var platformId:String?= null
lateinit var rv_PlatformSeries: RecyclerView
var myListArray= JSONArray()
var platformName:String?= null
//lateinit var notificationOn:ImageView
//lateinit var notificationOff:ImageView
var userObject= JSONObject()
var isActive: Boolean?=null
/**
 * A simple [Fragment] subclass.
 * Use the [PlatformSeriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlatformSeriesFragment : Fragment() {
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
    ): View {
        // Inflate the layout for this fragment
        platformId = arguments?.getString("platformId")
        platformName = arguments?.getString("platformName")


         seriesView=inflater.inflate(R.layout.fragment_platform_series, container, false)
        seriesView.tv_platformTitle.text = platformName
//        notificationOn = seriesView.findViewById(R.id.iv_notificationIcon)
//        notificationOff = seriesView.findViewById(R.id.iv_notificationOnIcon)

        if (Singleton().getUserFromSharedPrefrence(context!!)!=null){
            checkNotification()
        }else{

        }


        seriesView.iv_notificationIcon.setOnClickListener {

            if (Singleton().getUserFromSharedPrefrence(context!!)!= null){
                notificationOnOff()
//            checkNotification()
                seriesView.iv_notificationIcon.visibility = View.GONE
                seriesView.iv_notificationOnIcon.visibility = View.VISIBLE
                seriesView.tv_notificationText.text = "Turn off notifications"
            }else{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        seriesView.iv_notificationOnIcon.setOnClickListener {
            if (Singleton().getUserFromSharedPrefrence(context!!)!= null){
                notificationOnOff()
//            checkNotification()
                seriesView.iv_notificationIcon.visibility = View.VISIBLE
                seriesView.iv_notificationOnIcon.visibility = View.GONE
                seriesView.tv_notificationText.text = "Turn on notifications"
            }else{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }


        rv_PlatformSeries = seriesView.findViewById(R.id.rv_PlatformSeries)
        rv_PlatformSeries.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,2)
        rv_PlatformSeries.layoutManager = mLayoutManager
        rv_PlatformSeries.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        rv_PlatformSeries.itemAnimator = DefaultItemAnimator()

        myList()
        platformSeries()

        return seriesView

    }

    private fun platformSeries(){
        val kProgressHUD = Singleton().createLoading(context,"","")
        val seriesParams = HashMap<String,String>()
        seriesParams["platformId"] = platformId.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.platformSeries(seriesParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                kProgressHUD?.dismiss()
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                var msg: String? = null
                if (success) {
                    msg = jsonObject.getString("msg")
                    //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    val platformsSeriesAdapter = PlatformsSeriesAdapter(context!!,
                        jsonArray,
                        this@PlatformSeriesFragment)
                    rv_PlatformSeries.adapter = platformsSeriesAdapter
                } else {
                    msg = jsonObject.getString("msg")
                    seriesView.tv_nothingToShow.visibility = View.VISIBLE
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    
//                    Handler().postDelayed({
//                        activity?.onBackPressed()
//                    },3000)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                kProgressHUD?.dismiss()
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                Log.d("error", "$t")
            }
        })
    }

    private fun notificationOnOff() {
        userObject = Singleton().getUserFromSharedPrefrence(context!!)!!
        val notificationParams = HashMap<String,String>()
        notificationParams["platformId"] = platformId.toString()
        notificationParams["userId"]= userObject.getString("token")

        val call: Call<ResponseBody> = ApiClient.getClient.notification(notificationParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                val jsonObject = jsonArray.getJSONObject(0)
                val success = jsonObject.getBoolean("success")
                var msg: String? = null
                if (success) {
                    msg = jsonObject.getString("msg")
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }else{
                    msg = jsonObject.getString("msg")
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                Log.d("error","$t")
            }
        })
    }

    private fun checkNotification(){
        userObject = Singleton().getUserFromSharedPrefrence(context!!)!!
        val notificationParams = HashMap<String, String>()
        notificationParams["uid"] = userObject.getString("token").toString()

        val call:Call<ResponseBody> = ApiClient.getClient.checkNotification(notificationParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                Log.d("jsonArray", "$jsonArray")
                if (jsonArray.length()>0){
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    var msg: String? = null
                    if (success) {
                        msg = jsonObject.getString("msg")
                        isActive = jsonObject.getBoolean("isActive")
                        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (isActive == true) {
                            //Toast.makeText(context, "$isActive", Toast.LENGTH_SHORT).show()
                            seriesView.iv_notificationIcon.visibility = View.GONE
                            seriesView.iv_notificationOnIcon.visibility = View.VISIBLE
                            seriesView.tv_notificationText.text = "Turn off notifications"
                        }
                        else{
                            //Toast.makeText(context, "$isActive", Toast.LENGTH_SHORT).show()
                            seriesView.iv_notificationIcon.visibility = View.VISIBLE
                            seriesView.iv_notificationOnIcon.visibility = View.GONE
                            seriesView.tv_notificationText.text = "Turn on notifications"
                        }
                    } else {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        //isActive = jsonObject.getBoolean("isActive")
                    }
                }
                else{
                    Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlatformSeriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlatformSeriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun myList(){
        val userObject = Singleton().getUserFromSharedPrefrence(context!!)
        uid = userObject?.getString("token")

        val listParmas = HashMap<String,String>()
        listParmas["uid"] = uid.toString()
        val call: Call<ResponseBody> = ApiClient.getClient.myShowsList(listParmas)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                myListArray= JSONArray(res)
                Log.d("mylist","$myListArray")
                val msg: String
                if (myListArray.length() > 0) {

                    val jsonObject = myListArray.getJSONObject(0)
                    if (jsonObject.getBoolean("success")) {
//                        val myListAdapter = MyListAdapter(context!!, jsonArray,this@MyListFragment)
//                        recyclerView.adapter = myListAdapter
//                        msg = jsonObject.getString("msg")
//                        Toast.makeText(context, "$msg my list", Toast.LENGTH_SHORT).show()
                    } else {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun moveToNextFragment(listObject: JSONObject) {
        val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        val bundle = Bundle()
        bundle.putString("seriesObject", "$listObject")
        bundle.putString("myList", "$myListArray")
        val seriesDetailsFragment = SeriesDetailsFragment()
        fragmentTransaction!!.replace(R.id.frame_main,seriesDetailsFragment)
        fragmentTransaction.addToBackStack("Fragments")
        fragmentTransaction.commit()
        seriesDetailsFragment.arguments= bundle
    }

//    private fun checkNotification(){
//
//        if (isActive == true){
////            Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()
//            seriesView.iv_notificationIcon.visibility = View.GONE
//            seriesView.iv_notificationOnIcon.visibility = View.VISIBLE
//        }else{
////            Toast.makeText(context, "0", Toast.LENGTH_SHORT).show()
//            seriesView.iv_notificationIcon.visibility = View.VISIBLE
//            seriesView.iv_notificationOnIcon.visibility = View.GONE
//        }
//    }
}