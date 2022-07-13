package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityDailyListBinding

class DailyList : AppCompatActivity() {

    // object to connect to the database
    lateinit var dbhelper: DBHelper

    // list of tasks from the database
    var tasks = mutableListOf<Task>()

    // variable for referring to activity elements
    private lateinit var binding: ActivityDailyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDailyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialization of the object for working with the database
        dbhelper = DBHelper(this)
        // getting access to the database with the ability to write
        val database = dbhelper.writableDatabase
        // getting data from the database
        database.getdata(tasks)
        // selection of data that is added to the daily list
        tasks = tasks.filter{it.dl==1} as MutableList<Task>
        // sorts tasks by category
        tasks.sortBy { it.cat }

        // adding an action on the "General List" button click:
            // updating data in the database,
            // starting GeneralList activity
        binding.btGL.setOnClickListener{
            database.upddata(tasks)
            val intent = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(intent)
        }

        // assigning adapter for RecyclerView, which represents a list of views of tasks
        val adapter = DailyListAdapter(tasks)
        val concatAdapter = ConcatAdapter(adapter)
        binding.rvDailyList.adapter = concatAdapter
        binding.rvDailyList.layoutManager= LinearLayoutManager(this)

        // adding an action on the "Done" button click:
            // congratulations dialog showing, adding an action on the dialog button click:
                // deleting all tasks from the daily list
        binding.btDone.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("CONGRATS!!!")
            builder.setMessage("Your total score: "+tasks.filter{it.check==1}.sumOf { it.cost.toInt() }.toString() + "/" +tasks.sumOf { it.cost.toInt() }.toString())
            builder.setPositiveButton("Thanks!", { dialogInterface: DialogInterface, i -> cleanTasks()})
            builder.show()
        }

        // updating the score field
        tvUpdate(tasks.filter{it.check==1}.sumOf { it.cost.toInt() }, tasks.sumOf { it.cost.toInt() })
    }

    // need to update the data before closing the activity
    override fun onStop() {
        val database = dbhelper.writableDatabase
        database.upddata(tasks)
        super.onStop()
    }

    // deleting all tasks from the daily list:
        // changing the corresponding fields of all tasks,
        // updating data in the database,
        // starting GeneralList activity
    fun cleanTasks(){
        tasks.forEach{it.dl=0}
        tasks.forEach{it.check=0}
        tasks.forEach{it.upd=true}
        val database = dbhelper.writableDatabase
        database.upddata(tasks)
        val intent = Intent(binding.root.context, MainActivity::class.java)
        binding.root.context.startActivity(intent)
    }

    // setting text to score-textview
    fun tvUpdate(currentValue:Int, totalValue:Int){
        binding.tvTotal.text = "Total: "+currentValue.toString()+"/"+totalValue.toString()
    }
}
