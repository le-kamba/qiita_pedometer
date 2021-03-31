package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseFragment
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.FragmentLogEditBinding
import jp.les.kasa.sample.mykotlinapp.utils.levelFromRadioId
import jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 編集画面
 * @date 2019-08-29
 **/
class LogEditFragment : BaseFragment() {

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

        const val SCREEN_NAME = "ログ編集画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    val viewModel by sharedViewModel<LogItemViewModel>()

    private lateinit var stepCountLog: StepCountLog

    private lateinit var binding: FragmentLogEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_log_edit, container, false
        )

        stepCountLog = requireArguments().getSerializable(ARG_DATA) as StepCountLog

        binding.stepLog = stepCountLog

        binding.buttonUpdate.setOnClickListener {
            validation()?.let {
                ErrorDialog.Builder().message(it).create().show(parentFragmentManager, null)
                return@setOnClickListener
            }
            analyticsUtil.sendButtonEvent("更新ボタン")

            val dateText = binding.textDate.text.toString()
            val stepCount = binding.editCount.text.toString().toInt()
            val level =
                levelFromRadioId(binding.radioGroup.checkedRadioButtonId)
            val weather =
                weatherFromSpinner(
                    binding.spinnerWeather.selectedItemPosition
                )
            val newLog = StepCountLog(dateText, stepCount, level, weather)
            viewModel.changeLog(newLog, ShareStatus())
        }
        binding.buttonDelete.setOnClickListener {
            analyticsUtil.sendButtonEvent("削除ボタン")
            viewModel.deleteLog(stepCountLog)
        }

        return binding.root
    }

    private fun validation(): Int? {
        return logEditValidation(binding.editCount.text.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.log_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_sns -> {
                onShareSnsSelected()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onShareSnsSelected() {
        // 変更値を収集する
        validation()?.let {
            ErrorDialog.Builder().message(it).create().show(parentFragmentManager, null)
            return
        }
        analyticsUtil.sendButtonEvent("シェアボタン")

        val dateText = binding.textDate.text.toString()
        val stepCount = binding.editCount.text.toString().toInt()
        val level =
            levelFromRadioId(binding.radioGroup.checkedRadioButtonId)
        val weather = weatherFromSpinner(
            binding.spinnerWeather.selectedItemPosition
        )
        stepCountLog = StepCountLog(dateText, stepCount, level, weather)

        val dialog = SnsChooseDialog()
        dialog.show(requireActivity().supportFragmentManager, null)
    }
}

fun logEditValidation(
    stepCountText: String?
): Int? {
    // ステップ数が1文字以上入力されていること
    if (stepCountText.isNullOrEmpty()) {
        return R.string.error_validation_empty_count
    }
    return null
}


class SnsChooseDialog : DialogFragment() {
    private val viewModel: LogItemViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(arrayOf("Twitter", "Instagram")) { _, which ->
            viewModel.selectShareSns(which)
        }

        return builder.create()
    }
}
