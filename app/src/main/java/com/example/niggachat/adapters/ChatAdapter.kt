package com.example.niggachat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.niggachat.R
import com.example.niggachat.Utils.formatAsTime
import com.example.niggachat.modals.ChatEvent
import com.example.niggachat.modals.DateHeader
import com.example.niggachat.modals.Message
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.*
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.content
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.time
//import kotlinx.android.synthetic.main.list_item_chat_sent_message.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(private val list : MutableList<ChatEvent>, private val mCurrentUid : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var highFiveClick: ((id: String, status: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout : Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }

        return when(viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_recv_message))
            }
            TEXT_MESSAGE_SENT -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_sent_message))
            }
            DATE_HEADER -> {
                DateViewHolder(inflate(R.layout.list_item_date_header))
            }
            else -> MessageViewHolder(inflate(R.layout.list_item_chat_recv_message))
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = list[position]) {
            is Message -> {
                holder.itemView.content.text = item.msg
                holder.itemView.time.text = item.sentAt.formatAsTime()

                when (getItemViewType(position)) {
                    TEXT_MESSAGE_RECEIVED -> {
                        holder.itemView.messageCardView.setOnClickListener(object :
                            DoubleClickListener() {
                            override fun onDoubleClick(v: View?) {
                                highFiveClick?.invoke(item.msgId, !item.liked)
                            }
                        })
                        holder.itemView.highFiveImg.apply {
                            isVisible = position == itemCount - 1 || item.liked
                            isSelected = item.liked
                            setOnClickListener {
                                highFiveClick?.invoke(item.msgId, !isSelected)
                            }
                        }
                    }

                    TEXT_MESSAGE_SENT -> {
                        holder.itemView.highFiveImg.apply {
                            isVisible = item.liked
                        }
                    }
                }
            }
            is DateHeader -> {
                holder.itemView.textView.text = item.date
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(val event = list[position]) {
            is Message -> {
                if(event.senderId == mCurrentUid)
                    TEXT_MESSAGE_SENT
                else
                    TEXT_MESSAGE_RECEIVED
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    class DateViewHolder(view : View) : RecyclerView.ViewHolder(view)

    class MessageViewHolder(view : View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
//        else {
//            onSingleClick(v)
//        }
        lastClickTime = clickTime
    }

    //    abstract fun onSingleClick(v: View?)
    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}