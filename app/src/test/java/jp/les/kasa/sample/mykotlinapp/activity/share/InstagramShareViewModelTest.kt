package jp.les.kasa.sample.mykotlinapp.activity.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class InstagramShareViewModelTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: InstagramShareViewModel

    @Before
    fun setUp() {
        viewModel = InstagramShareViewModel()
    }

    @Test
    fun createShareImage() {
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565)

        val time = System.currentTimeMillis()

        // テスト用の出力ディレクトリはアプリのキャッシュディレクトリとする
        val dir = ApplicationProvider.getApplicationContext<Context>().cacheDir
        runBlocking {
            viewModel.createShareImage(bitmap, dir)
        }

        // パスセットの確認
        val value = viewModel.savedBitmapFile.value
        assertThat(value).isNotNull().isFile()
        assertThat(value!!.parent).isNotNull().isEqualTo(dir.absolutePath)

        // 拡張子とファイル名の確認
        val ext = value.extension
        val bodyName = value.name.replace(ext, "")
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
        val bodyTimeStamp = formatter.parse(bodyName)

        assertThat(ext).isEqualTo("jpg")

        // 処理直前の時間と僅差であることの確認
        assertThat(bodyTimeStamp.time).isCloseTo(time, Offset.offset(1000L))

        // ファイルが出来ているかの確認
        val savedBitmap = BitmapFactory.decodeFile(value.absolutePath)
        assertThat(savedBitmap).isNotNull()
        // 画像が一致するかなどは、Robolectricがダミー画像を作って返すためチェック出来ない。
    }
}