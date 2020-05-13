package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseFragment
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import jp.les.kasa.sample.mykotlinapp.utils.getDateStringYMD
import jp.les.kasa.sample.mykotlinapp.utils.levelFromRadioId
import jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner
import kotlinx.android.synthetic.main.fragment_log_input.*
import kotlinx.android.synthetic.main.fragment_log_input.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class LogInputFragment : BaseFragment() {

    companion object {
        const val TAG = "LogInputFragment"
        const val DATE_SELECT_TAG = "date_select"
        const val KEY_INITIAL_DATE = "initial_date"

        fun newInstance(date: Calendar): LogInputFragment {
            val f = LogInputFragment()
            f.arguments = Bundle().apply {
                putSerializable(KEY_INITIAL_DATE, date)
            }
            return f
        }

        const val SCREEN_NAME = "ログ編集画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    private val today: Calendar by lazy {
        arguments!!.getSerializable(KEY_INITIAL_DATE) as Calendar
    }
    val viewModel by sharedViewModel<LogItemViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_log_input, container, false)

        contentView.radio_group.check(R.id.radio_normal)

        today.clearTime()
        contentView.text_date.text = today.getDateStringYMD()

        contentView.button_update.setOnClickListener {
            validation()?.let {
                ErrorDialog.Builder().message(it).create().show(parentFragmentManager, null)
                return@setOnClickListener
            }
            analyticsUtil.sendButtonEvent("登録ボタン")

            val dateText = text_date.text.toString()
            val stepCount = edit_count.text.toString().toInt()
            val level =
                levelFromRadioId(radio_group.checkedRadioButtonId)
            val weather =
                weatherFromSpinner(
                    spinner_weather.selectedItemPosition
                )
            val stepCountLog = StepCountLog(dateText, stepCount, level, weather)

            val postSns = switch_share.isChecked
            val postTwitter = checkBox_twitter.isChecked
            val postInstagram = checkBox_instagram.isChecked

            val shareStatus = ShareStatus(postSns, postTwitter, postInstagram)
            // 設定に保存
            viewModel.saveShareStatus(shareStatus)
            // 登録をpost
            viewModel.changeLog(stepCountLog, shareStatus)
        }

        // 日付を選ぶボタンで日付選択ダイアログを表示
        contentView.button_date.setOnClickListener {
            analyticsUtil.sendButtonEvent("日付選択ボタン")
            DateSelectDialogFragment().show(parentFragmentManager, DATE_SELECT_TAG)
        }

        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 日付の選択を監視
        viewModel.selectDate.observe(viewLifecycleOwner, Observer {
            text_date.text = it.getDateStringYMD()
        })

        // sns投稿設定
        val shareStatus = viewModel.readShareStatus()
        switch_share.isChecked = shareStatus.doPost
        checkBox_twitter.isChecked = shareStatus.postTwitter
        checkBox_instagram.isChecked = shareStatus.postInstagram
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
