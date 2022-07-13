package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemGltaskBinding


class GeneralListAdapter (var tasks: MutableList<Task>) : RecyclerView.Adapter<GeneralListAdapter.GeneralListViewHolder>() {
    fun Boolean.toInt() = if (this) 1 else 0
    inner class GeneralListViewHolder(val binding: ItemGltaskBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.cbDl.setOnCheckedChangeListener{
                    buttonView, isChecked ->
                val position: Int = bindingAdapterPosition
                if (!isChecked){
                    tasks[position].check=0
                }
                tasks[position].dl = isChecked.toInt()

                tasks[position].upd = true
                }
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, TaskActivity::class.java)
                intent.putExtra("TASK_INDEX", tasks[adapterPosition].id)
                binding.root.context.startActivity(intent)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGltaskBinding.inflate(layoutInflater, parent, false)
        return GeneralListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
    override fun onBindViewHolder(holder: GeneralListViewHolder, position: Int) {
        holder.binding.apply {
            tvNameTask.text = tasks[position].description
            tvCat.text = tasks[position].cat
            tvCost.text = tasks[position].cost.toString()
            cbDl.isChecked = (tasks[position].dl==1)
        }
    }
}