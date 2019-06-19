package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.getDateStringYMD
import kotlinx.android.synthetic.main.fragment_log_input.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class LogInputFragment : Fragment() {

    companion object {
        fun newInstance(): LogInputFragment {
            val f = LogInputFragment()
            return f
        }
    }

    lateinit var viewModel: LogItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_log_input, container, false)

        contentView.radio_group.check(R.id.radio_normal)
        contentView.text_date.text = Calendar.getInstance().getDateStringYMD()

        contentView.button_resist.setOnClickListener {
            val dateText = contentView.text_date.text.toString()
            val stepCount = contentView.edit_count.text.toString().toInt()
            val level = levelFromRadioId(contentView.radio_group.checkedRadioButtonId)
            val weather = weatherFromSpinner(contentView.spinner_weather.selectedItemPosition)
            val stepCountLog = StepCountLog(dateText, stepCount, level, weather)

            viewModel.changeLog(stepCountLog)
        }

        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)
    }

    private fun levelFromRadioId(checkedRadioButtonId: Int): LEVEL {
        return when (checkedRadioButtonId) {
            R.id.radio_good -> LEVEL.GOOD
            R.id.radio_bad -> LEVEL.BAD
            else -> LEVEL.NORMAL
        }
    }

    private fun weatherFromSpinner(selectedItemPosition: Int): WEATHER {
        return WEATHER.values()[selectedItemPosition]
    }
}
