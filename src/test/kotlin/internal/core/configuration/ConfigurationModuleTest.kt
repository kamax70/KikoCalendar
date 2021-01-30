package internal.core.configuration

import internal.core.exception.BusinessValidationException
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.assertj.core.api.Assertions.*
import org.junit.Test

class ConfigurationModuleTest {

    @Test
    fun `test configuration`() {
        val config = buildConfiguration("config.properties")
        assertEquals(10, config.int("testNumber"))
        assertEquals("string", config.string("testString"))

        assertThatThrownBy {
            assertNull(config.string("invalid"))
        }
            .isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Missed property 'invalid'.")

        assertThatThrownBy {
            assertNull(config.int("testString"))
        }
            .isInstanceOf(NumberFormatException::class.java)
            .hasMessage("""For input string: "string"""")
    }
}