package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import jp.les.kasa.sample.mykotlinapp.R
import kotlinx.android.synthetic.main.activity_log_item.*


class LogItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_item)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LogInputFragment.newInstance())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
