package jp.les.kasa.sample.mykotlinapp

import android.content.DialogInterface
import android.content.Intent
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
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.alert.ConfirmDialog
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityMainBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemStepLogBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
    , LogRecyclerAdapter.OnItemClickListener
    , ConfirmDialog.ConfirmEventListener {

    companion object {
        const val REQUEST_CODE_LOGITEM = 100

        const val RESULT_CODE_DELETE = 10

        const val DIALOG_TAG_DELTE_CONRIFM = "delete_confirm"
        const val DIALOG_BUNDLE_KEY_DATA = "data"
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
        adapter = LogRecyclerAdapter(this)
        log_list.adapter = adapter
        // 区切り線を追加
        val decor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        log_list.addItemDecoration(decor)
    }

    override fun onItemClick(data: StepCountLog) {
        val intent = Intent(this, LogItemActivity::class.java)
        intent.putExtra(LogItemActivity.EXTRA_KEY_DATA, data)
        startActivityForResult(intent, REQUEST_CODE_LOGITEM)
    }

    override fun onLongItemClick(data: StepCountLog) {
        // ダイアログを表示
        val dialog = ConfirmDialog.Builder()
            .message(R.string.message_delete_confirm)
            .data(Bundle().apply {
                putSerializable(DIALOG_BUNDLE_KEY_DATA, data)
            })
            .create()
        dialog.show(supportFragmentManager, DIALOG_TAG_DELTE_CONRIFM)
    }

    override fun onConfirmResult(which: Int, bundle: Bundle?, requestCode: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                // 削除を実行
                val stepCountLog = bundle?.getSerializable(DIALOG_BUNDLE_KEY_DATA) as StepCountLog?
                viewModel.deleteStepCount(stepCountLog!!)
            }
        }
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
                    val intent = Intent(this, LogItemActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_LOGITEM)
                    true
                }
                else -> false
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CODE_LOGITEM -> {
                onStepCountLogChanged(resultCode, data)
                return
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onStepCountLogChanged(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val log = data!!.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
                viewModel.addStepCount(log)
            }
            RESULT_CODE_DELETE -> {
                val log = data!!.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
                viewModel.deleteStepCount(log)
            }
        }
    }
}


class LogRecyclerAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<LogRecyclerAdapter.LogViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: StepCountLog)
        fun onLongItemClick(data: StepCountLog)
    }

    private var list: List<StepCountLog> = emptyList()

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
        val data = list[position]
        holder.binding.stepLog = data
        holder.binding.logItemLayout.setOnClickListener {
            listener.onItemClick(data)
        }
        holder.binding.logItemLayout.setOnLongClickListener {
            listener.onLongItemClick(data)
            return@setOnLongClickListener true
        }
    }

    class LogViewHolder(val binding: ItemStepLogBinding) : RecyclerView.ViewHolder(binding.root)
}