package jp.les.kasa.sample.mykotlinapp.activity.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.alert.ConfirmDialog
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentMonthlyPageBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemStepLogBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 月ページFragment
 */
class MonthlyPageFragment : Fragment(),
    LogRecyclerAdapter.OnItemClickListener
    , ConfirmDialog.ConfirmEventListener {

    companion object {
        const val DIALOG_TAG_DELETE_CONFIRM = "delete_confirm"
        const val DIALOG_BUNDLE_KEY_DATA = "data"
    }

    val viewModel by sharedViewModel<MainViewModel>()
    lateinit var adapter: LogRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMonthlyPageBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_monthly_page, container, false
        )

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        // RecyclerViewの初期化
        binding.logList.layoutManager = LinearLayoutManager(context)
        adapter = LogRecyclerAdapter(this)
        binding.logList.adapter = adapter
        // 区切り線を追加
        val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.logList.addItemDecoration(decor)

        viewModel.setYearMonth("2020/02")

        return binding.root
    }

    override fun onItemClick(data: StepCountLog) {
        val intent = Intent(context, LogItemActivity::class.java)
        intent.putExtra(LogItemActivity.EXTRA_KEY_DATA, data)
        activity?.startActivityForResult(
            intent,
            MainActivity.REQUEST_CODE_LOGITEM
        )
    }

    override fun onLongItemClick(data: StepCountLog) {
        // ダイアログを表示
        val dialog = ConfirmDialog.Builder()
            .message(R.string.message_delete_confirm)
            .data(Bundle().apply {
                putSerializable(DIALOG_BUNDLE_KEY_DATA, data)
            })
            .target(this)
            .create()
        dialog.show(
            requireFragmentManager(),
            DIALOG_TAG_DELETE_CONFIRM
        )
    }

    override fun onConfirmResult(which: Int, bundle: Bundle?, requestCode: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                // 削除を実行
                val stepCountLog =
                    bundle?.getSerializable(DIALOG_BUNDLE_KEY_DATA) as StepCountLog?
                viewModel.deleteStepCount(stepCountLog!!)
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
            LayoutInflater.from(parent.context),
            R.layout.item_step_log, parent, false
        )
        return LogViewHolder(
            binding
        )
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