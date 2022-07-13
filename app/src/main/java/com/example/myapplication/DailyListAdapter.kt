package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityDailyListBinding
import com.example.myapplication.databinding.ItemDltaskBinding


class DailyListAdapter(var tasks: MutableList<Task>) : RecyclerView.Adapter<DailyListAdapter.DailyListViewHolder>() {
    fun Boolean.toInt() = if (this) 1 else 0
    lateinit var viewParent: ViewGroup
    inner class DailyListViewHolder(val binding: ItemDltaskBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.cbCheck.setOnCheckedChangeListener{
                    _, isChecked ->
                val position: Int =bindingAdapterPosition
                tasks[position].check = isChecked.toInt()
                tasks[position].upd = true
                Toast.makeText(binding.root.context, "Total: "+ tasks.filter{it.check==1}.sumOf { it.cost.toInt() }.toString() + "/"+tasks.sumOf { it.cost.toInt() }.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyListViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDltaskBinding.inflate(layoutInflater, parent, false)
        return DailyListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
    override fun onBindViewHolder(holder: DailyListViewHolder, position: Int) {
        holder.binding.apply {
            tvDesDL.text = tasks[position].description
            tvCatDL.text = tasks[position].cat
            tvCostDL.text = tasks[position].cost.toString()
            cbCheck.isChecked = (tasks[position].check==1)
        }
    }
}