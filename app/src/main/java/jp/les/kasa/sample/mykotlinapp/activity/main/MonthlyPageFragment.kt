package jp.les.kasa.sample.mykotlinapp.activity.main

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
import jp.les.kasa.sample.mykotlinapp.data.CalendarCellData
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentMonthlyPageBinding
import jp.les.kasa.sample.mykotlinapp.databinding.ItemCellBinding
import jp.les.kasa.sample.mykotlinapp.di.CalendarProviderI
import jp.les.kasa.sample.mykotlinapp.getDateStringYMD
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 * 月ページFragment
 */
class MonthlyPageFragment : Fragment(),
    LogRecyclerAdapter.OnItemClickListener {

    companion object {
        const val KEY_DATE_YEAR_MONTH = "dateYearMonth"

        fun newInstance(dateYearMonth: String): MonthlyPageFragment {
            val f = MonthlyPageFragment()
            f.arguments = Bundle().apply {
                putString(KEY_DATE_YEAR_MONTH, dateYearMonth)
            }
            return f
        }
    }

    val viewModel by viewModel<MonthlyPageViewModel>()
    lateinit var adapter: LogRecyclerAdapter

    val calendarProvider: CalendarProviderI by inject()

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

        val yearMonth = arguments!!.getString(KEY_DATE_YEAR_MONTH)!!

        // RecyclerViewの初期化
        binding.logList.layoutManager = GridLayoutManager(context, 7)
        adapter = LogRecyclerAdapter(this, Integer.valueOf(yearMonth.split('/')[1]))
        binding.logList.adapter = adapter

        viewModel.setYearMonth(yearMonth)

        return binding.root
    }

    override fun onItemClick(data: CalendarCellData) {
        val intent = Intent(context, LogItemActivity::class.java)
        if (data.stepCountLog != null) {
            intent.putExtra(LogItemActivity.EXTRA_KEY_DATA, data.stepCountLog)
        } else {
            if (canGoInput(data.calendar)) {
                intent.putExtra(LogItemActivity.EXTRA_KEY_INITIAL_DATE, data.calendar)
            } else {
                return
            }
        }
        activity?.startActivityForResult(
            intent,
            MainActivity.REQUEST_CODE_LOGITEM
        )
    }

    private fun canGoInput(date: Calendar): Boolean =
        (date.getDateStringYMD() == calendarProvider.now.getDateStringYMD())
}

class LogRecyclerAdapter(private val listener: OnItemClickListener, val month: Int) :
    RecyclerView.Adapter<LogRecyclerAdapter.LogViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: CalendarCellData)
    }

    private var list: List<CalendarCellData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding: ItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_cell, parent, false
        )
        return LogViewHolder(
            binding
        )
    }

    fun setList(newList: List<CalendarCellData>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        if (position >= list.size) return
        val data = list[position]
        holder.binding.cellData = data
        holder.binding.month = month
        holder.binding.logItemLayout.setOnClickListener {
            listener.onItemClick(data)
        }
    }

    class LogViewHolder(val binding: ItemCellBinding) : RecyclerView.ViewHolder(binding.root)
}

