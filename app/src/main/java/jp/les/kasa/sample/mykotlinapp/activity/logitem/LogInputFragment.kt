package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.*
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import kotlinx.android.synthetic.main.fragment_log_input.*
import kotlinx.android.synthetic.main.fragment_log_input.view.*
import java.util.*


class LogInputFragment : Fragment() {

    companion object {
        const val TAG = "LogInputFragment"
        const val DATE_SELECT_TAG = "date_select"

        fun newInstance(): LogInputFragment {
            val f = LogInputFragment()
            return f
        }
    }

    private val today = Calendar.getInstance().clearTime()
    lateinit var viewModel: LogItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_log_input, container, false)

        contentView.radio_group.check(R.id.radio_normal)

        contentView.text_date.text = today.getDateStringYMD()

        contentView.button_update.setOnClickListener {
            validation()?.let {
                val fgm = fragmentManager ?: return@setOnClickListener
                ErrorDialog.Builder().message(it).create().show(fgm, null)
                return@setOnClickListener
            }

            val dateText = text_date.text.toString()
            val stepCount = edit_count.text.toString().toInt()
            val level = levelFromRadioId(radio_group.checkedRadioButtonId)
            val weather = weatherFromSpinner(spinner_weather.selectedItemPosition)
            val stepCountLog = StepCountLog(dateText, stepCount, level, weather)

            viewModel.changeLog(stepCountLog)
        }

        // 日付を選ぶボタンで日付選択ダイアログを表示
        contentView.button_date.setOnClickListener {
            val fgm = fragmentManager ?: return@setOnClickListener // nullチェック
            DateSelectDialogFragment().show(fgm, DATE_SELECT_TAG)
        }

        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        // 日付の選択を監視
        viewModel.selectDate.observe(this, Observer {
            text_date.text = it.getDateStringYMD()
        })
    }

    private fun validation(): Int? {
        val selectDate = viewModel.selectDate.value?.clearTime()
        return logInputValidation(today, selectDate, edit_count.text.toString())
    }
}

fun logInputValidation(
    today: Calendar, selectDate: Calendar?,
    stepCountText: String?
): Int? {
    if (today.before(selectDate)) {
        // 今日より未来はNG
        return R.string.error_validation_future_date
    }
    // ステップ数が1文字以上入力されていること
    if (stepCountText.isNullOrEmpty()) {
        return R.string.error_validation_empty_count
    }
    return null
}
