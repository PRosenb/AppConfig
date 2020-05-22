package ch.pete.appconfig

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

internal class AppConfigContentProviderApplyValueTest {
    private val appConfigContentProvider = AppConfigContentProvider()

    @Test
    fun checkApplyValueWithBoolean() {
        // given
        val testKey = "key"
        val testValue = true

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putBoolean(testKey, testValue)
    }

    @Test
    fun checkApplyValueWithByte() {
        // given
        val testKey = "key"
        val testValue: Byte = 2

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putInt(testKey, testValue.toInt())
    }

    @Test
    fun checkApplyValueWithDouble() {
        // given
        val testKey = "key"
        val testValue: Double = 2.10003

        // when
        val editorMock: SharedPreferences.Editor = mock()
        val exception = assertThrows(IllegalArgumentException::class.java) {
            try {
                callApplyValue(testKey, testValue, editorMock)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }

        // then
        assertThat(exception.message).isEqualTo("Double it not supported by SharedPreferences, use Short or String instead.")
    }

    @Test
    fun checkApplyValueWithFloat() {
        // given
        val testKey = "key"
        val testValue = 2.10003f

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putFloat(testKey, testValue)
    }

    @Test
    fun checkApplyValueWithInt() {
        // given
        val testKey = "key"
        val testValue = 23452

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putInt(testKey, testValue)
    }

    @Test
    fun checkApplyValueWithLong() {
        // given
        val testKey = "key"
        val testValue: Long = 23452

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putLong(testKey, testValue)
    }

    @Test
    fun checkApplyValueWithShort() {
        // given
        val testKey = "key"
        val testValue: Short = 23452

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putInt(testKey, testValue.toInt())
    }

    @Test
    fun checkApplyValueWithString() {
        // given
        val testKey = "key"
        val testValue = "value"

        // when
        val editorMock: SharedPreferences.Editor = mock()
        callApplyValue(testKey, testValue, editorMock)

        // then
        verify(editorMock).putString(testKey, testValue)
    }

    @Test
    fun checkApplyValueWithUnsupportedType() {
        // given
        val testKey = "key"
        val testValue = listOf("")
        val editorMock: SharedPreferences.Editor = mock()

        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            callApplyValue(testKey, testValue, editorMock)
        }

        // then
        assertThat(exception.message).isEqualTo("Unsupported data type ${testValue::class.java}")
    }

    private fun callApplyValue(key: String, value: Any, editor: SharedPreferences.Editor) {
        try {
            val method: Method = AppConfigContentProvider::class.java.getDeclaredMethod(
                "applyValue",
                String::class.java, Any::class.java, SharedPreferences.Editor::class.java
            )
            method.isAccessible = true
            method.invoke(appConfigContentProvider, key, value, editor)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}
