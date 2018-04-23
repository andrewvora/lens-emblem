package com.andrewvora.apps.lensemblem.notifications

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.models.AppMessage
import kotlinx.android.synthetic.main.row_item_notification.view.*

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationsAdapter(private var notifications: List<AppMessage>) :
        RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_notification, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    fun updateNotifications(notifications: List<AppMessage>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: AppMessage) {
            itemView.notification_title.text = message.title
            itemView.notification_message.text = message.message
            message.posted?.let {
                itemView.notification_timestamp.text = it.toString()
            }
        }
    }
}