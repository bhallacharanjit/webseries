package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.MyListFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class MyListAdapter(context: Context, var jsonArray: JSONArray,myListFragment: MyListFragment)
    :RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {

    var context = context
    private var myListFragment:MyListFragment = myListFragment



    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        var iv_myList_photo = view.findViewById<ImageView>(R.id.iv_myList_Photo)
        var tv_myList_name = view.findViewById<TextView>(R.id.tv_myList_Name)
        var myListRating = view.findViewById<RatingBar>(R.id.MyListRating)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_my_list_layout,parent,false)

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyListAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        val data = jsonArray[position]
        var rating:String?= null
        if (jsonObject.getString("averageRating")=="null"){
            holder.myListRating.visibility = View.GONE
        }else{
            holder.myListRating.visibility = View.VISIBLE
            rating = jsonObject.getString("averageRating")
            holder.myListRating.rating = rating.toFloat()
        }
        holder.tv_myList_name.text = jsonObject.getString("showname")
        val imageURL = Singleton().imageUrl+jsonObject.getString("webseriesposter")
        Glide.with(context)
            .load(imageURL)
            .into(holder.iv_myList_photo)

        holder.itemView.setOnClickListener {

            val listObject = jsonArray.getJSONObject(position)
            myListFragment.moveToNextFragment(listObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

}