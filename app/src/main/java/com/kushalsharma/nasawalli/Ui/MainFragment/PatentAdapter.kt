package com.kushalsharma.nasawalli.Ui.MainFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kushalsharma.nasawalli.Models.Patent
import com.kushalsharma.nasawalli.R
import com.kushalsharma.nasawalli.Ui.MainFragment.MainFragment

class PatentAdapter(val context: MainFragment, val patents: Patent, val listner : iPostAdapter) :
    RecyclerView.Adapter<PatentAdapter.PatentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatentViewHolder {

//        val view = LayoutInflater.from(parent.context).inflate(R.layout.patent_item,
//            parent, false)

        val viewHolder = PatentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.patent_item,parent,false))

        viewHolder.pCardView!!.setOnClickListener {
            val title = viewHolder.pTitle.text.toString()
            val descp = viewHolder.pDecp.text.toString()
            val imgUrl = viewHolder.pImgUrl

            listner.onItemClicked(title,descp,imgUrl)

            }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PatentViewHolder, position: Int) {
        val patentData: List<String> = patents.results[position]
        holder.pDecp.text = patentData[3]
        holder.pTitle.text = patentData[2]
        Glide.with(this.context).load(patentData[10].toString())
            .transform(CenterCrop(),RoundedCorners(90) )
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.pImg)
        holder.pImgUrl = patentData[10].toString()


    }

    override fun getItemCount(): Int {
        return patents.results.size
    }

    class PatentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pTitle = itemView.findViewById<TextView>(R.id.tv_patentTitle)
        var pDecp = itemView.findViewById<TextView>(R.id.tv_patentDescription)
        var pImg = itemView.findViewById<ImageView>(R.id.iv_patentImg)
        var pImgUrl : String = "Image Loading"
        var pCardView : CardView? = itemView.findViewById<CardView>(R.id.cardView_item)

    }


}

interface iPostAdapter{
    fun onItemClicked(title: String, descp: String, imgUrl: String)
}