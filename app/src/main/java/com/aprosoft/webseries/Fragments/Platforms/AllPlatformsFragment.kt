package com.aprosoft.webseries.Fragments.Platforms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.AllPlatformsAdapter
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var platformView:View
lateinit var rvPlatform:RecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [AllPlatformsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllPlatformsFragment : Fragment() {
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

        platformView=inflater.inflate(R.layout.fragment_all_platforms, container, false)

        rvPlatform = platformView.findViewById(R.id.rv_PlatformList)
        rvPlatform.setHasFixedSize(true)

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,3)
        rvPlatform.layoutManager = mLayoutManager
        rvPlatform.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        rvPlatform.itemAnimator = DefaultItemAnimator()

        allPlatforms()
        return platformView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllPlatformsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllPlatformsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun allPlatforms(){
        val kProgressHUD = Singleton().createLoading(context,"","")
        val call: Call<ResponseBody> = ApiClient.getClient.allPlatforms()
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
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    val allPlatformsAdapter = AllPlatformsAdapter(context!!,jsonArray,this@AllPlatformsFragment)
                    rvPlatform.adapter= allPlatformsAdapter
                }else{
                    msg = jsonObject.getString("msg")
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                kProgressHUD?.dismiss()
            }
        })
    }

    fun moveToNextFragment(token:String,platformName:String){
        val fragmentTransaction: FragmentTransaction =fragmentManager?.beginTransaction()!!
        val bundle = Bundle()
        bundle.putString("platformId", token)
        bundle.putString("platformName",platformName)
        val platformSeriesFragment = PlatformSeriesFragment()
        fragmentTransaction.replace(R.id.frame_main,platformSeriesFragment)
        fragmentTransaction.addToBackStack("Fragments")
        fragmentTransaction.commit()
        platformSeriesFragment.arguments = bundle
    }
}