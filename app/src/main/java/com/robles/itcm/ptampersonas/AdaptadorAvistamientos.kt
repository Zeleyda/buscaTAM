package com.robles.itcm.ptampersonas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorAvistamientos(private var avistamientosLista: ArrayList<Avistamientos>): RecyclerView.Adapter<AdaptadorAvistamientos.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setFilteredList(filteredList: ArrayList<Avistamientos>)
    {
        avistamientosLista = filteredList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Avistamientos>{
        return avistamientosLista
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_avistamientos, parent, false)
        return MyViewHolder(itemView, mListener)

    }

    override fun getItemCount(): Int {
        return avistamientosLista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = avistamientosLista[position]
        holder.title.text = currentItem.titulo
        holder.description.text = currentItem.fecha

    }

    class MyViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.txt_list_avistamiento_title)
        val description: TextView = itemView.findViewById(R.id.txt_list_avistamiento_descripcion)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}