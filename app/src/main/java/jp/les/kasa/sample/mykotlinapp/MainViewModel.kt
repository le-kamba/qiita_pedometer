package jp.les.kasa.sample.mykotlinapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel : ViewModel() {

    val inputStepCount = MutableLiveData<Int>()
}