package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.SeriesByActorFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class ActorSeriesAdapter(var context: Context, var jsonArray: JSONArray, var seriesByActorFragment: SeriesByActorFragment)
    :RecyclerView.Adapter<ActorSeriesAdapter.MyViewHolder>(){

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val platformSeriesImage = view.findViewById<ImageView>(R.id.iv_PlatformSeriesImage)
        val platformSeriesName = view.findViewById<TextView>(R.id.tv_PlatformSeriesName)
        val actorSeriesRating =view.findViewById<RatingBar>(R.id.PlatformSeriesrating)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorSeriesAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_platformseries_layout,parent,false)
        return MyViewHolder(v)

    }

    override fun onBindViewHolder(holder: ActorSeriesAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)

        var rating:String?= null
        if (jsonObject.getString("averageRating")=="null"){
            holder.actorSeriesRating.visibility = View.GONE
        }else{
            holder.actorSeriesRating.visibility = View.VISIBLE
            rating = jsonObject.getString("averageRating")
            holder.actorSeriesRating.rating = rating.toFloat()
        }
        Glide.with(context)
            .load(Singleton().imageUrl+jsonObject.getString("webseriesposter"))
            .into(holder.platformSeriesImage)
        holder.platformSeriesName.text = jsonObject.getString("showname")

        holder.itemView.setOnClickListener {
            val listObject = jsonArray.getJSONObject(position)
            seriesByActorFragment.moveToNextFragment(listObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

}