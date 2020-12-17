package com.aprosoft.webseries.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aprosoft.webseries.Fragments.SeriesByCategoryFragment
import com.aprosoft.webseries.R
import com.aprosoft.webseries.Shared.Singleton
import com.bumptech.glide.Glide
import org.json.JSONArray

class CategorySeriesAdapter(var context: Context, var jsonArray: JSONArray, var seriesByCategoryFragment: SeriesByCategoryFragment)
    :RecyclerView.Adapter<CategorySeriesAdapter.MyViewHolder>(){

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val iv_categorySeriesImage = view.findViewById<ImageView>(R.id.iv_CategorySeriesImage)
        val tv_categorySeriesName = view.findViewById<TextView>(R.id.tv_CategorySeriesName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySeriesAdapter.MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_categoryseries_layout,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategorySeriesAdapter.MyViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        Glide.with(context!!)
            .load(Singleton().imageUrl+jsonObject.getString("webseriesposter"))
            .into(holder.iv_categorySeriesImage)
        holder.tv_categorySeriesName.text = jsonObject.getString("showname")
        holder.itemView.setOnClickListener {
            val listObject = jsonArray.getJSONObject(position)
            seriesByCategoryFragment.moveToNextFragment(listObject)

        }

    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}