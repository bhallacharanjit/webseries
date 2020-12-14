package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.Platforms.PlatformSeriesFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Retrofit.ApiClient
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlatformsSeriesAdapter(var context: Context, var jsonArray: JSONArray, var platformSeriesFragment: PlatformSeriesFragment)
    :RecyclerView.Adapter<PlatformsSeriesAdapter.MyViewHolder>(){

    var platformId:String?= null
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val platformSeriesImage = view.findViewById<ImageView>(R.id.iv_PlatformSeriesImage)
        val platformSeriesName = view.findViewById<TextView>(R.id.tv_PlatformSeriesName)
//        val platformSeriesRating = view.findViewById<RatingBar>(R.id.PlatformSeriesrating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformsSeriesAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_platformseries_layout,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: PlatformsSeriesAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        Glide.with(context)
            .load(Singleton().imageUrl+jsonObject.getString("webseriesposter"))
            .into(holder.platformSeriesImage)
        holder.platformSeriesName.text = jsonObject.getString("showname")
        var token:String?= null
        holder.itemView.setOnClickListener {
            token = jsonObject.getString("token")
//            Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
            val listObject = jsonArray.getJSONObject(position)
            platformId = jsonObject.getString("platformId")
            Toast.makeText(context, platformId, Toast.LENGTH_SHORT).show()
            platformSeriesFragment.moveToNextFragment(listObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    private fun notificationOnOff(){
        val notificationParams = HashMap<String,String>()
        notificationParams["platformId"]
        notificationParams["userId"]

        val call: Call<ResponseBody> = ApiClient.getClient.notification(notificationParams)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }
}