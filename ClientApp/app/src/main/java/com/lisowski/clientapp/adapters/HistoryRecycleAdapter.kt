package com.lisowski.clientapp.adapters

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lisowski.clientapp.R
import com.lisowski.clientapp.models.RideDetails
import kotlinx.android.synthetic.main.history_item.view.*
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class HistoryRecycleAdapter(
    private val rides: ArrayList<RideDetails>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<HistoryRecycleAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = rides[position]

        var timeStart = Instant.parse((currentItem.timeStart))
        timeStart = timeStart.minus(2, ChronoUnit.HOURS)
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d-M-u")
                .withLocale(Locale.GERMAN)
                .withZone(ZoneId.systemDefault())

        val endTime = Instant.parse(currentItem.endTime)
        val arrivalTime = Instant.parse(currentItem.arrivalTime)
        val ns: Long = Duration.between(arrivalTime, endTime).toMinutes()
        Log.d(TAG, "onBindViewHolder: end time $endTime arrtime $arrivalTime  minuty $ns")

        holder.time.text = ns.toString()
        holder.price.text = currentItem.price.toString()
        holder.rate.text = currentItem.rating.toString()
        holder.date.text = formatter.format(timeStart)
    }

    override fun getItemCount() = rides.size


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val time: TextView = itemView.historyTimeTV
        val price: TextView = itemView.historyPriceTV
        val rate: TextView = itemView.historyRatingTV
        val date: TextView = itemView.dateTV

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position :Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}