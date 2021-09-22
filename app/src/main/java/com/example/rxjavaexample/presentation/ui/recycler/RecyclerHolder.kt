package com.example.rxjavaexample.presentation.ui.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjavaexample.R
import com.example.rxjavaexample.domain.model.History

class RecyclerHolder(itemView: View, val recycler: Recycler): RecyclerView.ViewHolder(itemView), View.OnClickListener{

    private val textView = itemView.findViewById<TextView>(R.id.recycler_item_text_view)!!

    override fun onClick(p0: View?) {
        recycler.onClickedItem(adapterPosition)
    }

    fun bindWithView(history: History){
        textView.text = history.item
    }
}