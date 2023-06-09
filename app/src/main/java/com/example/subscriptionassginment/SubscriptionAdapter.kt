package com.example.subscriptionassginment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SubscriptionAdapter(
    val subList: List<Subscription>
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_item, parent, false)
        return SubscriptionViewHolder(view)

    }

    override fun getItemCount(): Int {
        return subList.size
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")

        val from = dateFormat.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
        val to = dateFormat.parse(subList[position].expDate)
        val msDifference = kotlin.math.abs(from.time - to.time)
        val differenceDates = msDifference / (24 * 60 * 60 * 1000) //Gets the difference in days

        holder.apply {
            txtName.text = subList[position].subName
            txtExpDate.text = subList[position].expDate
            txtPrice.text = "€" + subList[position].price.toString()

            if(differenceDates < 30) {
                subItem.setBackgroundColor(Color.parseColor("#e8b654"))
            }
            if(differenceDates > 30) {
                subItem.setBackgroundColor(Color.parseColor("#0ccf16"))
            }
            if (to < from) {
                subItem.setBackgroundColor(Color.parseColor("#d95050"))
            }
        }
    }

    inner class SubscriptionViewHolder(row: View): RecyclerView.ViewHolder(row){
        val txtName: TextView
        val txtExpDate: TextView
        val txtPrice: TextView
        val subItem: ConstraintLayout

        init {
            txtName = row.findViewById(R.id.sub_name)
            txtExpDate = row.findViewById(R.id.sub_expDate)
            txtPrice = row.findViewById(R.id.sub_price)
            subItem = row.findViewById(R.id.subItems)
        }
    }

}

