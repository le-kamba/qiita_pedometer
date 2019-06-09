package jp.les.kasa.sample.mykotlinapp

import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel : ViewModel() {

    val stepCountList = MutableLiveData<MutableList<Int>>()

    init {
        stepCountList.value = mutableListOf()
    }

    @UiThread
    fun addStepCount(count: Int) {
        val list = stepCountList.value ?: return
        list.add(count)
        stepCountList.value = list
    }
}
