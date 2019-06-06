package jp.les.kasa.sample.mykotlinapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TAG = "input_dialog"
    }

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.inputStepCount.observe(this, Observer<Int> {
            textView.text = it.toString()
        })

        InputDialogFragment().show(supportFragmentManager, INPUT_TAG)
    }
}
