package jp.les.kasa.sample.mykotlinapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.random.Random

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

    companion object {
        val names = listOf("Hachi", "Coma", "Suzuri")
        val years = listOf(1923, 2006, 2018)
        val messages = listOf("Test", "Sample", "Cute")
        fun randomPet(): HashMap<String, Any> {
            val i = Random(System.currentTimeMillis()).nextInt(3)
            return hashMapOf(
                "petDog" to true,
                "message" to messages[i],
                "petName" to names[i],
                "born" to years[i]
            )
        }
    }
}
