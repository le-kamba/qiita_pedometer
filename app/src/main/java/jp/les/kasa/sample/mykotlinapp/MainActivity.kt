package jp.les.kasa.sample.mykotlinapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), InputDialogFragment.OnClickListerer {
    override fun onResist(step: Int) {

    }

    companion object {
        const val INPUT_TAG = "input_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InputDialogFragment().show(supportFragmentManager, INPUT_TAG)
    }
}
