package com.example.rxjavaexample.presentation.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjavaexample.R
import com.example.rxjavaexample.domain.model.History

class RecyclerAdapter(private val recycler: Recycler) : RecyclerView.Adapter<RecyclerHolder>(){

    var recyclerItemList = ArrayList<History>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
       return RecyclerHolder(
           LayoutInflater
               .from(parent.context)
               .inflate(R.layout.recycler_item, parent, false),
               this.recycler
       )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val dataItem = this.recyclerItemList[position]
        holder.bindWithView(dataItem)
    }


    override fun getItemCount(): Int = this.recyclerItemList.size

    fun submitList(historyList : ArrayList<History>){
        this.recyclerItemList = historyList
    }
}