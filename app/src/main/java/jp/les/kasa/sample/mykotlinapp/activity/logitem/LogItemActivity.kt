package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import kotlinx.android.synthetic.main.activity_log_item.*


class LogItemActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_DATA = "data"
    }

    lateinit var viewModel: LogItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_item)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.logitem_container, LogInputFragment.newInstance())
                .commitNow()
        }

        viewModel = ViewModelProviders.of(this).get(LogItemViewModel::class.java)

        viewModel.stepCountLog.observe(this, Observer {
            val dataIntent = Intent()
            dataIntent.putExtra(EXTRA_KEY_DATA, it)
            setResult(RESULT_OK, dataIntent)
            finish()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
