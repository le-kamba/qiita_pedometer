package jp.les.kasa.sample.mykotlinapp

import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog

/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel : ViewModel() {

    val stepCountList = MutableLiveData<MutableList<StepCountLog>>()

    init {
        stepCountList.value = mutableListOf()
    }

    @UiThread
    fun addStepCount(stepLog: StepCountLog) {
        val list = stepCountList.value ?: return
        list.add(stepLog)
        stepCountList.value = list
    }
}
