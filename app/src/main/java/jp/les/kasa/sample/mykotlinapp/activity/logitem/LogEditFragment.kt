package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentLogEditBinding
import jp.les.kasa.sample.mykotlinapp.levelFromRadioId
import jp.les.kasa.sample.mykotlinapp.weatherFromSpinner
import kotlinx.android.synthetic.main.fragment_log_input.*

/**
 * 編集画面
 * @date 2019-08-29
 **/
class LogEditFragment : Fragment() {

    companion object {
        const val TAG = "LogEditFragment"
        const val ARG_DATA = "data"

        fun newInstance(stepCountLog: StepCountLog): LogEditFragment {
            val f = LogEditFragment()
            f.arguments = Bundle().apply {
                putSerializable(ARG_DATA, stepCountLog)
            }
            return f
        }
    }

    lateinit var viewModel: LogItemViewModel
    private lateinit var stepCountLog: StepCountLog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentLogEditBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_log_edit, container, false
        )

        viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        stepCountLog = arguments!!.getSerializable(ARG_DATA) as StepCountLog

        binding.stepLog = stepCountLog

        binding.buttonUpdate.setOnClickListener {
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
        binding.buttonDelete.setOnClickListener {

            viewModel.deleteLog(stepCountLog)
        }

        return binding.root
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun validation(): Int? {
        // ステップ数が1文字以上入力されていること
        val stepCountText = edit_count.text.toString()
        if (stepCountText.isNullOrEmpty()) {
            return R.string.error_validation_empty_count
        }
        return null
    }
}