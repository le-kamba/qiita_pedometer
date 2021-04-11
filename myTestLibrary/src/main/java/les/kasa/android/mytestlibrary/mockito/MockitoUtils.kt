package les.kasa.android.mytestlibrary.mockito

import org.mockito.Mockito

fun <T> any(clazz: Class<T>): T {
    return Mockito.any(clazz)
}
