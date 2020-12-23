package com.aprosoft.webseries.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.*
import com.aprosoft.webseries.Adapters.MyListAdapter
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import kotlinx.android.synthetic.main.fragment_my_list.view.*
import kotlinx.android.synthetic.main.fragment_my_list.view.iv_backArrow
import kotlinx.android.synthetic.main.fragment_profile.view.*
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
private lateinit var recyclerView: RecyclerView
lateinit var MyListView:View
var jsonArray = JSONArray()
/**
 * A simple [Fragment] subclass.
 * Use the [MyListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyListFragment : Fragment() {
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
        MyListView =inflater.inflate(R.layout.fragment_my_list, container, false)

        MyListView.iv_backArrow.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(context?.applicationContext,R.anim.alpha)
            MyListView.iv_backArrow.startAnimation(animation)
            activity?.supportFragmentManager?.popBackStack()
        }

        recyclerView = MyListView.findViewById(R.id.rv_myShowList)
        recyclerView.setHasFixedSize(true)

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()

        myList()

        return MyListView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyListFragment().apply {
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
                jsonArray= JSONArray(res)
                Log.d("mylist","$jsonArray")
                val msg: String
                if (jsonArray.length() > 0) {

                    val jsonObject = jsonArray.getJSONObject(0)
                    if (jsonObject.getBoolean("success")) {
                        val myListAdapter = MyListAdapter(context!!, jsonArray,this@MyListFragment)
                        recyclerView.adapter = myListAdapter
                        msg = jsonObject.getString("msg")
//                        Toast.makeText(context, "$msg my list", Toast.LENGTH_SHORT).show()
                    } else {
                        msg = jsonObject.getString("msg")
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    MyListView.tv_nothingToShow.visibility = View.VISIBLE

//                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
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
//        bundle.putString("myList", "$jsonArray")
        val seriesDetailsFragment = SeriesDetailsFragment()
        fragmentTransaction!!.replace(R.id.frame_main,seriesDetailsFragment)
        fragmentTransaction.addToBackStack("Fragments")
        fragmentTransaction.commit()
        seriesDetailsFragment.arguments= bundle


    }


}