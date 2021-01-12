package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.MoreSeriesFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class MoreSeriesAdapter(var context: Context,var jsonArray: JSONArray,
                        var moreSeriesFragment: MoreSeriesFragment):RecyclerView.Adapter<MoreSeriesAdapter.MyViewHolder>() {


    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val platformSeriesImage = view.findViewById<ImageView>(R.id.iv_PlatformSeriesImage)
        val platformSeriesName = view.findViewById<TextView>(R.id.tv_PlatformSeriesName)
        val platformSeriesRating = view.findViewById<RatingBar>(R.id.PlatformSeriesrating)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoreSeriesAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_platformseries_layout,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MoreSeriesAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        var token:String?= null
        var rating:String?= null


        if (position == jsonArray.length()-2) {
            moreSeriesFragment.callMoreSeries()
        }


        if (jsonObject.getString("averageRating")=="null"){
            holder.platformSeriesRating.visibility = View.GONE
        }else{
            holder.platformSeriesRating.visibility = View.VISIBLE
            rating = jsonObject.getString("averageRating")
            holder.platformSeriesRating.rating = rating.toFloat()
        }

        Glide.with(context)
            .load(Singleton().imageUrl+jsonObject.getString("webseriesposter"))
            .into(holder.platformSeriesImage)
        holder.platformSeriesName.text = jsonObject.getString("showname")


        holder.itemView.setOnClickListener {
            token = jsonObject.getString("token")
            val listObject = jsonArray.getJSONObject(position)
            moreSeriesFragment.moveToNextFragment(listObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }


    fun notifyChanges(jsonArray: JSONArray) {
        this.jsonArray = jsonArray
        this.notifyDataSetChanged()
    }
}