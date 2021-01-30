package internal.core.eventbus

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.glassfish.hk2.api.IterableProvider
import org.junit.Before
import org.junit.Test

class EventManagerTest {

    private val handler: EventHandler<Any> = mock()
    private val iterableProvider: IterableProvider<EventHandler<Any>> = mock()
    private lateinit var eventManager: EventManager

    @Before
    fun before() {
        whenever(handler.getEventClass()).thenReturn(TestEvent::class)
        whenever(iterableProvider.iterator()).thenReturn(mutableListOf(handler).iterator())
        eventManager = EventManager(iterableProvider)
    }

    @Test
    fun `handle event`() {
        val event = TestEvent("value")
        runBlocking { eventManager.notify(event) }
        verify(handler).handle(event)
    }

    data class TestEvent(
        val value: String
    )
}