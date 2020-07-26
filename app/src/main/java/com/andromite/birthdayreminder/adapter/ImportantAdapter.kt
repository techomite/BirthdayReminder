package com.andromite.birthdayreminder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromite.birthdayreminder.R

class ImportantAdapter() : RecyclerView.Adapter<ImportantAdapter.ImportantViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportantViewHolder {
        return ImportantViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.layout_home_recyclerview,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ImportantViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class ImportantViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}