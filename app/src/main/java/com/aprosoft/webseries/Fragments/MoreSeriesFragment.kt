package com.aprosoft.webseries.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aprosoft.webseries.Adapters.MoreSeriesAdapter
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.fragment_more_series.*
import kotlinx.android.synthetic.main.fragment_more_series.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MoreSeriesFragment : Fragment() {
    private var moreSeriesAdapter:MoreSeriesAdapter? = null
    private var seriesJsonArray:JSONArray? = null
    var pageNumber = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        pageNumber=1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_more_series, container, false)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack()
            }
        })

           view.iv_backArrow_MoreSeries.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(
                context?.applicationContext,
                R.anim.alpha
            )
            view.iv_backArrow_MoreSeries.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }
        view.tv_All.setOnClickListener {
            val moreSeriesAdapter = MoreSeriesAdapter(
                context!!,
                seriesJsonArray!!,
                this@MoreSeriesFragment
            )
            view.rv_MoreSeries.adapter = moreSeriesAdapter
            if (view.rl_languages.visibility==View.VISIBLE){
                view.rl_languages.visibility= View.GONE
            }
            if (view.mainView.visibility== View.GONE){
                view.mainView.visibility= View.VISIBLE
            }
        }
        view.tv_SelectLanguage.setOnClickListener {
            view.rl_languages.visibility= View.VISIBLE
            view.mainView.visibility= View.GONE
        }
        view.iv_close.setOnClickListener {
            view.rl_languages.visibility= View.GONE
            view.mainView.visibility= View.VISIBLE
        }
        view.tv_EngLanguage.setOnClickListener {
            seriesByLanguage("English", view)
        }
        view.tv_HindiLanguage.setOnClickListener {
            seriesByLanguage("Hindi", view)
        }
        view.tv_PunjabiLanguage.setOnClickListener {
            seriesByLanguage("Punjabi", view)
        }
        view.tv_GermanLanguage.setOnClickListener {
            seriesByLanguage("German", view)
        }




        view.rv_MoreSeries.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        view.rv_MoreSeries.layoutManager = mLayoutManager
        // rv_PlatformSeries.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        view.rv_MoreSeries.itemAnimator = DefaultItemAnimator()
        moreSeries()

        return view
    }

    fun moveToNextFragment(listObject: JSONObject) {
        val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        val bundle = Bundle()
        bundle.putString("seriesObject", "$listObject")
        val seriesDetailsFragment = SeriesDetailsFragment()
        fragmentTransaction!!.add(R.id.frame_main, seriesDetailsFragment)
        fragmentTransaction.addToBackStack("Fragments")
        seriesDetailsFragment.arguments= bundle
        fragmentTransaction.commit()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun seriesByLanguage(lan: String, v: View) {
        val languageParams = HashMap<String, String>()
        languageParams["language"]= lan
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = com.aprosoft.webseries.Shared.Singleton().getUserFromSharedPrefrence(
                context!!
            )
            languageParams["userId"]=userObject?.getString("token").toString()
        }else{
            //Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            languageParams["userId"]=""
        }

        val call:Call<ResponseBody> = ApiClient.getClient.seriesByLanguage(languageParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
//                        Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
//                        v.rl_languages.visibility= View.GONE
//                        v.mainView.visibility= View.VISIBLE
                        if (v.rl_languages.visibility == View.VISIBLE) {
                            v.rl_languages.visibility = View.GONE
                        }
                        if (v.mainView.visibility == View.GONE) {
                            v.mainView.visibility = View.VISIBLE
                        }
                        val moreSeriesAdapter = MoreSeriesAdapter(
                            context!!,
                            jsonArray,
                            this@MoreSeriesFragment
                        )
                        v.rv_MoreSeries.adapter = moreSeriesAdapter
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

    override fun onPause() {
        super.onPause()

    }

    private fun moreSeries(){

        val moreSeriesParams= HashMap<String, String>()
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = Singleton().getUserFromSharedPrefrence(context!!)
            moreSeriesParams["userId"]=userObject?.getString("token").toString()
        }else{
            moreSeriesParams["userId"]=""
        }
        moreSeriesParams["PageNumber"]= pageNumber.toString()
        moreSeriesParams["PageSize"]=Singleton().NUMBER_OF_RECORDS.toString()
        Log.d("pageNUmber", "$pageNumber,${Singleton().NUMBER_OF_RECORDS}")

        val call:Call<ResponseBody> = ApiClient.getClient.viewAllsWebseries(moreSeriesParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    if (moreSeriesAdapter == null) {
                        Log.e(TAG, "more series adapter in null")
                        seriesJsonArray = jsonArray
                        moreSeriesAdapter =
                            MoreSeriesAdapter(context!!, seriesJsonArray!!, this@MoreSeriesFragment)
                        view?.rv_MoreSeries?.adapter = moreSeriesAdapter
                        Log.e(TAG, "in more series adapter not null")
                        moreSeriesAdapter!!.notifyDataSetChanged()
                    } else {
                        Log.e(TAG, "more series adapter in else")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            seriesJsonArray!!.put(jsonObject)
                        }
                        moreSeriesAdapter!!.notifyDataSetChanged()
                        moreSeriesAdapter!!.notifyChanges(seriesJsonArray!!)
                    }
                } else {
                    Log.d("ArrayError", "$jsonArray")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
            }
        })
    }

    fun callMoreSeries() {
        if (seriesJsonArray!!.length() % Singleton().NUMBER_OF_RECORDS == 0) {
            pageNumber += 1
            moreSeries()
        } else {
            Toast.makeText(context, "You have reached the end of list", Toast.LENGTH_LONG).show()
        }
    }
    companion object {
         val TAG = MoreSeriesFragment::class.java.canonicalName
    }


}