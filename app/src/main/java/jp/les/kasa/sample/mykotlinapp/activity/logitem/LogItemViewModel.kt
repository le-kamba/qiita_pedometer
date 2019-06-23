package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.graphics.Bitmap
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import java.util.*

/**
 * ログアイテム表示画面用のViewModel
 * @date 2019/06/19
 **/
class LogItemViewModel : ViewModel() {

    private val _stepCountLog = MutableLiveData<StepCountLog>()
    private val _selectDate = MutableLiveData<Calendar>()
    private val _ocrResultText = MutableLiveData<String>()
    private val _ocrBitmapSource = MutableLiveData<Bitmap>()
    private val _ocrResultStepCount = MutableLiveData<Int?>()

    val stepCountLog = _stepCountLog as LiveData<StepCountLog>
    val selectDate = _selectDate as LiveData<Calendar>
    var ocrResultText = _ocrResultText as LiveData<String>
    var ocrBitmapSource = _ocrBitmapSource as LiveData<Bitmap>
    val ocrResultStepCount = _ocrResultStepCount as LiveData<Int?>

    @UiThread
    fun changeLog(data: StepCountLog) {
        _stepCountLog.value = data
    }

    @UiThread
    fun dateSelected(selectedDate: Calendar) {
        _selectDate.value = selectedDate
    }


    @UiThread
    fun ocrSource(sourceImage: Bitmap) {
        _ocrBitmapSource.value = sourceImage
    }

    @UiThread
    fun ocrResult(resultText: String) {
        _ocrResultText.value = resultText
    }

    @UiThread
    fun ocrResultTextToEdit() {
        _ocrResultStepCount.value = ocrResultText.value?.toInt()
    }
}