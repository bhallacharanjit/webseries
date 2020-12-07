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
import com.aprosoft.webseries.R
import kotlinx.android.synthetic.main.custom_review_layout.view.*
import org.json.JSONArray

class ReviewsAdapter(context: Context, var jsonArray: JSONArray)
    :RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>() {

    var context = context
    var rating:String?= null

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        var iv_reviewerPhoto:ImageView = view.findViewById(R.id.iv_reviewer_photo)
        var tv_reviewerName:TextView = view.findViewById(R.id.tv_reviewerName)
        var tv_reviewDate:TextView = view.findViewById(R.id.tv_reviewDate)
        var tv_review:TextView = view.findViewById(R.id.tv_reviewDesc)
        var seriesRating:RatingBar = view.findViewById(R.id.ratingReviewBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.MyViewHolder {

        val v:View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_review_layout,parent,false)

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReviewsAdapter.MyViewHolder, position: Int) {

        val jsonObject = jsonArray.getJSONObject(position)

        rating = jsonObject.getString("star")
//        Toast.makeText(context, "$rating", Toast.LENGTH_SHORT).show()
        holder.tv_reviewerName.text = jsonObject.getString("name")
        holder.tv_reviewDate.text = jsonObject.getString("date")
        holder.tv_review.text = jsonObject.getString("description")
        holder.seriesRating.rating= rating!!.toFloat()
    }
    override fun getItemCount(): Int {
        return  jsonArray.length()
    }
}