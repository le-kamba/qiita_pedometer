package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.les.kasa.sample.mykotlinapp.R


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_input, container, false)
    }
}
