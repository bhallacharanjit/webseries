package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.Platforms.AllPlatformsFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class AllPlatformsAdapter(var context: Context, var jsonArray: JSONArray,var allPlatformsFragment: AllPlatformsFragment)
    : RecyclerView.Adapter<AllPlatformsAdapter.MyViewHolder>() {


    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val platformImage = view.findViewById<ImageView>(R.id.iv_platformImage)
        val platformName = view.findViewById<TextView>(R.id.tv_platformName)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllPlatformsAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_platforms_layout,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: AllPlatformsAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        Glide.with(context)
            .load(Singleton().imageUrl+jsonObject.getString("platformImage"))
            .into(holder.platformImage)
        holder.platformName.text = jsonObject.getString("platformName")
        val platformName = jsonObject.getString("platformName")
        var token:String?= null
        holder.itemView.setOnClickListener {
            token = jsonObject.getString("token")
//            Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
            allPlatformsFragment.moveToNextFragment(token!!, platformName)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}