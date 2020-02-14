package jp.les.kasa.sample.mykotlinapp.activity.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.alert.ConfirmDialog
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentMonthlyPageBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemCellBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 月ページFragment
 */
class MonthlyPageFragment : Fragment(),
    LogRecyclerAdapter.OnItemClickListener
    , ConfirmDialog.ConfirmEventListener {

    companion object {
        const val KEY_DATE_YEAR_MONTH = "dateYearMonth"

        fun newInstance(dateYearMonth: String): MonthlyPageFragment {
            val f = MonthlyPageFragment()
            f.arguments = Bundle().apply {
                putString(KEY_DATE_YEAR_MONTH, dateYearMonth)
            }
            return f
        }

        const val DIALOG_BUNDLE_KEY_DATA = "data"
    }

    val viewModel by viewModel<MonthlyPageViewModel>()
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
        binding.logList.layoutManager = GridLayoutManager(context, 7)
        adapter = LogRecyclerAdapter(this)
        binding.logList.adapter = adapter

        viewModel.setYearMonth(arguments!!.getString(KEY_DATE_YEAR_MONTH)!!)

        return binding.root
    }

    override fun onItemClick(data: StepCountLog?) {
        val intent = Intent(context, LogItemActivity::class.java)
        data?.let {
            intent.putExtra(LogItemActivity.EXTRA_KEY_DATA, data)
        }
        activity?.startActivityForResult(
            intent,
            MainActivity.REQUEST_CODE_LOGITEM
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
        fun onItemClick(data: StepCountLog?)
    }

    private var list: List<StepCountLog?> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding: ItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_cell, parent, false
        )
        return LogViewHolder(
            binding
        )
    }

    fun setList(newList: List<StepCountLog?>) {
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
    }

    class LogViewHolder(val binding: ItemCellBinding) : RecyclerView.ViewHolder(binding.root)
}