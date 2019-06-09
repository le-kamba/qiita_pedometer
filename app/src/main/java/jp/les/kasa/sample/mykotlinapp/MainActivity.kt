package jp.les.kasa.sample.mykotlinapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_step_log.view.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TAG = "input_dialog"
    }

    lateinit var viewModel: MainViewModel
    lateinit var adapter: LogRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.stepCountList.observe(this, Observer { list ->
            list?.let {
                Log.d("MyKotlinApp", "stepCountList Changed!! :size = ${list.size}")
                adapter.setList(list)
            }
        })

        // RecyclerViewの初期化
        log_list.layoutManager = LinearLayoutManager(this)
        adapter = LogRecyclerAdapter(viewModel.stepCountList.value!!)
        log_list.adapter = adapter

        InputDialogFragment().show(supportFragmentManager, INPUT_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (it.itemId) {
                R.id.add_record -> {
                    InputDialogFragment().show(supportFragmentManager, INPUT_TAG)
                    true
                }
                else -> false
            }
        }
        return false
    }

}


class LogRecyclerAdapter(private var list: List<Int>) : RecyclerView.Adapter<LogRecyclerAdapter.LogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.item_step_log, parent, false)
        return LogViewHolder(rowView)
    }

    fun setList(newList: List<Int>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.textCount.text = if (position < list.size) list[position].toString() else ""
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCount = itemView.stepTextView!!
    }
}