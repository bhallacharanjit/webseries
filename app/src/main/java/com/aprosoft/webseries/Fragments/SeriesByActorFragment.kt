package com.aprosoft.webseries.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.ActorSeriesAdapter
import com.aprosoft.webseries.Fragments.Platforms.myListArray
import com.aprosoft.webseries.Fragments.Platforms.rv_PlatformSeries
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.fragment_series_by_actor.view.*
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
var actorID:String?= null
lateinit var ActorView:View
/**
 * A simple [Fragment] subclass.
 * Use the [SeriesByActorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeriesByActorFragment : Fragment() {
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
        actorID = arguments?.getString("actorid")
         ActorView=inflater.inflate(R.layout.fragment_series_by_actor, container, false)
        ActorView.tv_actorName.text = arguments?.getString("actorname")

        ActorView.rv_ActorSeries.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,2)
        ActorView.rv_ActorSeries.layoutManager = mLayoutManager
        // rv_PlatformSeries.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        ActorView.rv_ActorSeries.itemAnimator = DefaultItemAnimator()

        seriesByActor()

        return ActorView
    }

    private fun seriesByActor(){
        val actorParams = HashMap<String,String>()
        actorParams["actorid"] = actorID.toString()
        if(Singleton().getUserFromSharedPrefrence(context!!)!=null){
            val userObject = Singleton().getUserFromSharedPrefrence(context!!)
            actorParams["userId"]=userObject?.getString("token").toString()
        }else{
            //Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            actorParams["userId"]=""
        }
        val call:Call<ResponseBody> = ApiClient.getClient.seriesByActor(actorParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val res = response.body()?.string()
                val jsonArray = JSONArray(res)
                if (jsonArray.length() > 0) {
                    val success = jsonArray.getJSONObject(0).getBoolean("success")
                    if (success) {
                        val actorSeriesAdapter =
                            ActorSeriesAdapter(context!!, jsonArray, this@SeriesByActorFragment)
                        ActorView.rv_ActorSeries.adapter = actorSeriesAdapter
                    } else {
                      //  Toast.makeText(context, "Nothig to show", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("Array", "Array is null")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("error", "$t")
            }
        })
    }

    fun moveToNextFragment(listObject: JSONObject) {
        val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        val bundle = Bundle()
        bundle.putString("seriesObject", "$listObject")
        val seriesDetailsFragment = SeriesDetailsFragment()
        fragmentTransaction!!.replace(R.id.frame_main,seriesDetailsFragment)
        fragmentTransaction.addToBackStack("Fragments")
        fragmentTransaction.commit()
        seriesDetailsFragment.arguments= bundle
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SeriesByActorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeriesByActorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}