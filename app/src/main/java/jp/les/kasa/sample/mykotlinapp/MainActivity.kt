package jp.les.kasa.sample.mykotlinapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityMainBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemStepLogBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TAG = "input_dialog"
    }

    lateinit var viewModel: MainViewModel
    lateinit var adapter: LogRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        // RecyclerViewの初期化
        log_list.layoutManager = LinearLayoutManager(this)
        adapter = LogRecyclerAdapter(viewModel.stepCountList.value!!)
        log_list.adapter = adapter
        // 区切り線を追加
        val decor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        log_list.addItemDecoration(decor)

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


class LogRecyclerAdapter(private var list: List<StepCountLog>) :
    RecyclerView.Adapter<LogRecyclerAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding: ItemStepLogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_step_log, parent, false
        )
        return LogViewHolder(binding)
    }

    fun setList(newList: List<StepCountLog>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        if (position >= list.size) return
        holder.binding.stepLog = list[position]
    }

    class LogViewHolder(val binding: ItemStepLogBinding) : RecyclerView.ViewHolder(binding.root)
}