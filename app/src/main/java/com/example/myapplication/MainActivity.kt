package com.example.myapplication

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // object to connect to the database
    lateinit var dbhelper: DBHelper

    // list of tasks from the database
    var tasks = mutableListOf<Task>()
    // list of available task categories
    var cats = mutableListOf<String>()

    // variable for referring to activity elements
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialization of the object for working with the database
        dbhelper = DBHelper(this)
        // getting access to the database with the ability to write
        val database = dbhelper.writableDatabase
        // getting data from the database
        database.getdata(tasks, cats)
        // sorts tasks by category
        tasks.sortBy { it.cat }

        // adding an action on the "Daily List" button click:
            // updating data in the database,
            // starting DailyList activity
        binding.btDL.setOnClickListener{
            database.upddata(tasks)
            val intent = Intent(binding.root.context, DailyList::class.java)
            binding.root.context.startActivity(intent)
        }

        // assigning adapter for RecyclerView, which represents a list of tasks view
        val adapter = GeneralListAdapter(tasks)
        binding.rvGeneralList.adapter = adapter
        binding.rvGeneralList.layoutManager=LinearLayoutManager(this)

        // initialization of the drop-down list to select the category of the new task by cats list
        val spinner: Spinner = binding.spcat
        ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_item,
            cats
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // adding an action on the "Add task" button click:
        // creating new Task-object
        // cleaning the fields
        // adding it to the list of tasks
        // updating data in the database
        // notifying adapter of RecyclerView about data change (new item has been inserted)
        binding.btnNewTask.setOnClickListener{
            if(binding.etNewTask.text.toString()!="" && binding.etCost.text.toString()!="") {
                val task = Task(
                    -1,
                    binding.etNewTask.text.toString(),
                    binding.etCost.text.toString(),
                    spinner.selectedItem.toString(),
                    1,
                    0,
                    0,
                    upd = true
                )
                tasks.add(task)
                binding.etNewTask.setText("")
                binding.etCost.setText("")
                database.upddata(tasks)
                adapter.notifyItemInserted(tasks.size - 1)
            }
        }

    }
    // need to update the data before closing the activity
    override fun onStop() {
        val database = dbhelper.writableDatabase
        database.upddata(tasks)
        super.onStop()
    }



}