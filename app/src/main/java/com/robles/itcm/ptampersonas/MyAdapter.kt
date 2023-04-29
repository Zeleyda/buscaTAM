package com.robles.itcm.ptampersonas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val personsList: ArrayList<Persons>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_persons, parent, false)
        return MyViewHolder(itemView, mListener)

    }

    override fun getItemCount(): Int {
        return personsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = personsList[position]
        holder.title.setText(currentItem.title)
        holder.description.setText(currentItem.description)
        holder.image.setImageBitmap(currentItem.image)
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.txt_list_title)
        val description: TextView = itemView.findViewById(R.id.txt_list_description)
        val image: ImageView = itemView.findViewById(R.id.img_list_personimage)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}