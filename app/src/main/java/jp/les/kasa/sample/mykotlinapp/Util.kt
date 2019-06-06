package jp.les.kasa.sample.mykotlinapp

/**
 * いろいろ便利処理を集めたUtilクラス
 * @date 2019/06/05
 * @author c2090
 **/

class Util {
    companion object {
        fun getVersionCode() = BuildConfig.VERSION_CODE

        fun getVersionName() = BuildConfig.VERSION_NAME
    }
}