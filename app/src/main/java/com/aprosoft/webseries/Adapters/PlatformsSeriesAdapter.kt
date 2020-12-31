package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.MoreSeriesFragment
import com.aprosoft.webseries.Fragments.Platforms.PlatformSeriesFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class PlatformsSeriesAdapter(var context: Context,
                             var jsonArray: JSONArray,
                             var platformSeriesFragment: PlatformSeriesFragment)
    :RecyclerView.Adapter<PlatformsSeriesAdapter.MyViewHolder>(){

    var platformId:String?= null



    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val platformSeriesImage = view.findViewById<ImageView>(R.id.iv_PlatformSeriesImage)
        val platformSeriesName = view.findViewById<TextView>(R.id.tv_PlatformSeriesName)
        val platformSeriesRating = view.findViewById<RatingBar>(R.id.PlatformSeriesrating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformsSeriesAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_platformseries_layout,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: PlatformsSeriesAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)

        var rating:String?= null
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
        platformId = jsonObject.getString("platformId")
        var token:String?= null
        holder.itemView.setOnClickListener {
            token = jsonObject.getString("token")
            val listObject = jsonArray.getJSONObject(position)
            platformSeriesFragment.moveToNextFragment(listObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}