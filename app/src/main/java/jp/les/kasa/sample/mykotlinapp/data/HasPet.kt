package jp.les.kasa.sample.mykotlinapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Firestoreの試験用データクラス
 */
@Parcelize
data class HasPet(val map: @RawValue Map<String, Any>) : Parcelable {
    private val hasPet: Boolean
        get() {
            return map["petDog"] as Boolean
        }
    private val petName: String?
        get() {
            return map["petName"] as String?
        }
    private val born: Long?
        get() {
            return map["born"] as Long?
        }

    fun titleString(): String {
        return if (hasPet) {
            "$petName($born)"
        } else {
            "ペットを飼っていない"
        }
    }
}
