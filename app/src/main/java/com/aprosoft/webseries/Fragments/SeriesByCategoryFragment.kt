package com.aprosoft.webseries.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.CategorySeriesAdapter
import com.aprosoft.webseries.Adapters.PlatformsSeriesAdapter
import com.aprosoft.webseries.Fragments.Platforms.myListArray
import com.aprosoft.webseries.Fragments.Platforms.rv_PlatformSeries
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.fragment_series_by_category.view.*
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
var categoryName:String?= null
lateinit var rv_CategorySeries:RecyclerView
lateinit var CategorySeriesView:View

/**
 * A simple [Fragment] subclass.
 * Use the [SeriesByCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeriesByCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var pageNumber = 1
    var categorySeriesAdapter:CategorySeriesAdapter?=null
    var categorySeriesJSONArray: JSONArray?=null

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
        categoryName= arguments?.getString("categoryName")
        CategorySeriesView =inflater.inflate(R.layout.fragment_series_by_category, container, false)

        CategorySeriesView.tv_CategoryTitle.text= categoryName.toString()
        CategorySeriesView.rv_CategorySeries.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,2)
        CategorySeriesView.rv_CategorySeries.layoutManager = mLayoutManager
//        v.rv_CategorySeries.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        CategorySeriesView.rv_CategorySeries.itemAnimator = DefaultItemAnimator()

        seriesByCategory()


        return CategorySeriesView
    }

    private fun seriesByCategory(){
        val categoryParams = HashMap<String,String>()
        categoryParams["categoryId"] = categoryName.toString()
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = Singleton().getUserFromSharedPrefrence(context!!)
            categoryParams["userId"]=userObject?.getString("token").toString()
        }else{
            //Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            categoryParams["userId"]=""
        }
        categoryParams["PageNumber"]= 1.toString()
        categoryParams["PageSize"]= Singleton().NUMBER_OF_RECORDS.toString()

        val call:Call<ResponseBody> = ApiClient.getClient.seriesByCategory(categoryParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                Log.d("jsonArray","$jsonArray")
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        val msg = jsonObject.getString("msg")
                        if (categorySeriesAdapter==null){
                            categorySeriesJSONArray= jsonArray
                            categorySeriesAdapter = CategorySeriesAdapter(context!!,
                                    categorySeriesJSONArray!!,this@SeriesByCategoryFragment)
                            CategorySeriesView.rv_CategorySeries.adapter = categorySeriesAdapter
                        }else{
                            for (i in 0 until jsonArray.length()){
                                val jsonObject = jsonArray.getJSONObject(i)
                                categorySeriesJSONArray?.put(jsonObject)
                            }
                            categorySeriesAdapter!!.notifyChanges(categorySeriesJSONArray!!)
                        }

//                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        val msg = jsonObject.getString("msg")
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "empty array", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun callCategorySeries() {
        if (categorySeriesJSONArray!!.length() % Singleton().NUMBER_OF_RECORDS == 0) {
            pageNumber += 1
            seriesByCategory()
        } else {
            Toast.makeText(context,"You have reached the end of list",Toast.LENGTH_LONG).show()
        }
    }
    fun moveToNextFragment(listObject: JSONObject){
               val fragmentTransaction:FragmentTransaction?= fragmentManager?.beginTransaction()
        val seriesDetailsFragment = SeriesDetailsFragment()
        val bundle = Bundle()
        bundle.putString("seriesObject", "$listObject")
        fragmentTransaction!!.replace(R.id.frame_main,seriesDetailsFragment)
        fragmentTransaction.addToBackStack("Fragments")
        fragmentTransaction.commit()
        seriesDetailsFragment.arguments = bundle
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SeriesByCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeriesByCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}