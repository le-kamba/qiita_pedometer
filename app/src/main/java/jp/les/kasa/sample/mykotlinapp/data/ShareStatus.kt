package jp.les.kasa.sample.mykotlinapp.data

import java.io.Serializable

/**
 * SNSシェア情報
 * @date 2019-10-01
 **/
data class ShareStatus(
    val doPost: Boolean = false,
    val postTwitter: Boolean = false,
    val postInstagram: Boolean = false
): Serializable
