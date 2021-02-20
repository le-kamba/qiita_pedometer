package jp.les.kasa.sample.mykotlinapp.activity.share

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.any
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.mockito.Mockito
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P, Build.VERSION_CODES.Q])
class InstagramShareViewModelTest : AutoCloseKoinTest() {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    val viewModel: InstagramShareViewModel by inject()

    @Before
    fun setUp() {
        loadKoinModules(mockModule)
    }

    @Test
    fun createShareImage() {
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565)

        runBlocking {
            viewModel.createShareImage(bitmap)
        }

        // resultの確認
        assertThat(viewModel.savedBitmapUri.value).isNotNull()

        // ContentResolverでファイルを読んで確認
        val resolver = ApplicationProvider.getApplicationContext<Application>().contentResolver
        resolver.openInputStream(viewModel.savedBitmapUri.value!!).use { stream ->
            // ファイルが出来ているかの確認
            val savedBitmap = BitmapFactory.decodeStream(stream)
            assertThat(savedBitmap).isNotNull()
        }
        // 画像が一致するかなどは、Robolectricがダミー画像を作って返すためチェック出来ない。
    }

    @Test
    fun createShareImage_VersionDispatch() {
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565)

        // ViewModelインスタンスをモック化する
        val mocked = Mockito.spy(viewModel)

        val resultUri = "contet://foo/bar".toUri()

        // 特定の関数の戻り値をダミーにする
        Mockito.doReturn(resultUri).`when`(mocked)
            .saveBitmapOver28(any(Bitmap::class.java), Mockito.anyString())
        Mockito.doReturn(resultUri).`when`(mocked)
            .saveBitmapUnder29(any(Bitmap::class.java), Mockito.anyString())

        runBlocking {
            mocked.createShareImage(bitmap)
        }

        // resultの確認と、バージョン別に正しい関数が呼ばれているかのチェック
        assertThat(mocked.savedBitmapUri.value).isEqualTo(resultUri)

        if (Build.VERSION.SDK_INT > 28) {
            Mockito.verify(mocked, Mockito.times(1))
                .saveBitmapOver28(any(Bitmap::class.java), Mockito.anyString())
            Mockito.verify(mocked, Mockito.times(0))
                .saveBitmapUnder29(any(Bitmap::class.java), Mockito.anyString())
        } else {
            Mockito.verify(mocked, Mockito.times(0))
                .saveBitmapOver28(any(Bitmap::class.java), Mockito.anyString())
            Mockito.verify(mocked, Mockito.times(1))
                .saveBitmapUnder29(any(Bitmap::class.java), Mockito.anyString())
        }
    }
}