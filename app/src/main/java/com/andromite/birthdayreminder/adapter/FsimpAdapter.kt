package com.andromite.birthdayreminder.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.fragments.HomeFragment
import com.andromite.birthdayreminder.fragments.ImportantFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_home_recyclerview.view.*
import java.io.Serializable

class FsimpAdapter(val birthdays: List<FSBirthday>, val onRecyclerItemClickListener: ImportantFragment) : RecyclerView.Adapter<FsimpAdapter.BirthdayViewHolder>() {

    private val onImageClickListener: OnImageClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        return BirthdayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.layout_home_recyclerview,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount() = birthdays.size

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {

//        holder.view.person_name.text = birthdays.get(position).person_name

        holder.view.name.text = birthdays.get(position).person_name

        holder.view.details.text = birthdays.get(position).date

        if (birthdays.get(position).event == "1"){
            holder.view.event.setImageResource(R.drawable.ic_birthdaycake_black)
        } else {
            holder.view.event.setImageResource(R.drawable.ic_anniversary)
        }

        if(birthdays.get(position).isImportant.equals("true")){
            holder.view.isImportant.visibility = View.VISIBLE
        }

//        holder.view.pp2.text = birthdays.get(position).date
//        holder.view.pp3.text = birthdays.get(position).isImportant.toString()
//        holder.view.pp4.text = birthdays.get(position).notes
//        holder.view.pp5.text = birthdays.get(position).event.toString()

        if (!birthdays.get(position).profilePic.equals("")) {
            Glide.with(holder.view)
                .load(birthdays[position].profilePic)
                .into(holder.view.profilePic)
        } else {
            holder.view.profilePic.setImageResource(R.drawable.ic_circle_avatar)
        }

        holder.view.cl_card_main.setOnClickListener {
//            Log.e("12345 inside adapter", birthdays.get(position).id.toString())
//            onImageClickListener?.onImageClick(birthdays.get(position).id
//            intent.putExtra("id",birthdays.get(position).id)

            onRecyclerItemClickListener.onImageClick(position)



        }

    }

    class BirthdayViewHolder(val view:View) : RecyclerView.ViewHolder(view)

    interface OnRecyclerItemClickListener {
        fun onImageClick(imageData: Int)
    }

}