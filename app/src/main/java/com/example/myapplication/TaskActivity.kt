package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.myapplication.databinding.ActivityMainBinding

import com.example.myapplication.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    lateinit var dbhelper: DBHelper
    var tasks = mutableListOf<Task>()
    var cats = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbhelper = DBHelper(this)
        val database = dbhelper.writableDatabase
        val id = intent.getIntExtra("TASK_INDEX", 0)
        database.getdata(tasks, cats, id)

        binding.etDes.setText(tasks[0].description)
        binding.etCost1.setText(tasks[0].cost)
        val spinner: Spinner = binding.spinner
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_spinner_item,
            cats
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner

            spinner.adapter = adapter
            spinner.setSelection(adapter.getPosition(tasks[0].cat))
        }
        binding.btMainActivity.setOnClickListener{
            tasks[0].upd=true
            tasks[0].description=binding.etDes.text.toString()
            tasks[0].cost = binding.etCost1.text.toString()
            tasks[0].cat = binding.spinner.selectedItem.toString()
            database.upddata(tasks)
            val intent = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(intent)
        }
        binding.btCancel.setOnClickListener{
            val intent = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(intent)
        }
        binding.btDelete.setOnClickListener{
            database.deletedata(tasks[0].id)
            val intent = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(intent)
        }

    }
}