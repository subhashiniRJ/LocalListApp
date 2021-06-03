package com.example.locallistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailListAdapter(var mContext : Context,var mList : ArrayList<EmailModelClass>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is EmailViewHolder)
            holder.bindView(mList[position])
    }

    override fun getItemCount(): Int {
      return mList.size
    }

    class EmailViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        fun bindView(emailModelClass: EmailModelClass) {
            var tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
            var tvMob = itemView.findViewById<TextView>(R.id.tvNumber)
            tvEmail.text = emailModelClass.email
            tvMob.text = emailModelClass.number

        }
    }
}