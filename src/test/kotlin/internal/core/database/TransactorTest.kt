package internal.core.database

import org.assertj.core.api.Assertions.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class TransactorTest {

    private val transactor = Transactor()

    @Test
    fun `successful transaction`() {
        var done = false
        assertThat(transactor.transactionOpen.get()).isFalse
        transactor.inTransaction {
            done = true
            assertThat(transactor.transactionOpen.get()).isTrue
        }
        assertThat(done).isTrue
        assertThat(transactor.transactionOpen.get()).isFalse
    }

    @Test
    fun `failed transaction`() {
        assertThatThrownBy {
            transactor.inTransaction {
                throw NullPointerException()
            }
        }.isInstanceOf(NullPointerException::class.java)
    }

    @Test
    fun `nested transactions`() {
        var done = false
        assertThat(transactor.transactionOpen.get()).isFalse
        transactor.inTransaction {
            assertThat(transactor.transactionOpen.get()).isTrue
            transactor.inTransaction {
                assertThat(transactor.transactionOpen.get()).isTrue
                done = true
            }
        }
        assertThat(done).isTrue
        assertThat(transactor.transactionOpen.get()).isFalse
    }
}