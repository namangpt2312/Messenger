package com.example.niggachat.fragments

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.niggachat.R
import com.example.niggachat.modals.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user : User, onClick : (name : String, photo : String, id : String) -> Unit) = with(itemView) {
        countTv.isVisible = false
        timeTv.isVisible = false

        titleTv.text = user.name
        subtitleTv.text = user.status

        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)

        setOnClickListener{
            onClick.invoke(user.name, user.thumbImage, user.uid)
        }
    }
}