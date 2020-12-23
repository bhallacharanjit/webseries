package com.aprosoft.webseries.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Adapters.MoreSeriesAdapter
import com.aprosoft.webseries.Adapters.PlatformsSeriesAdapter
import com.aprosoft.webseries.Fragments.Platforms.PlatformSeriesFragment
import com.aprosoft.webseries.Fragments.Platforms.myListArray
import com.aprosoft.webseries.Fragments.Platforms.rv_PlatformSeries
import com.aprosoft.webseries.R
import kotlinx.android.synthetic.main.fragment_more_series.view.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreSeriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreSeriesFragment : Fragment() {
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
        val seriesString = arguments?.getString("seriesArray")
        val seriesArray = JSONArray(seriesString)
        Log.d("seriesArray","$seriesArray")
        val view=inflater.inflate(R.layout.fragment_more_series, container, false)

        view.rv_MoreSeries.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context,2)
        view.rv_MoreSeries.layoutManager = mLayoutManager
        // rv_PlatformSeries.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        view.rv_MoreSeries.itemAnimator = DefaultItemAnimator()

        val moreSeriesAdapter = MoreSeriesAdapter(context!!,seriesArray,this@MoreSeriesFragment)
        view.rv_MoreSeries.adapter = moreSeriesAdapter
        return view
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
         * @return A new instance of fragment MoreSeriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoreSeriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}