package internal.core.eventbus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.glassfish.hk2.api.IterableProvider
import org.jvnet.hk2.annotations.Service
import javax.inject.Inject
import kotlin.reflect.KClass

@Service
class EventManager @Inject constructor(
    handlers: IterableProvider<EventHandler<Any>>
) {
    private val handlerMap: MutableMap<KClass<out Any>, MutableList<EventHandler<Any>>> = HashMap()

    init {
        handlers.forEach { register(it) }
    }

    fun register(handler: EventHandler<Any>) {
        val eventListeners = handlerMap.getOrPut(handler.getEventClass()) { ArrayList() }
        eventListeners.add(handler)
    }

    fun notify(event: Any) {
        GlobalScope.launch {
            handlerMap[event::class]?.forEach { it.handle(event) }
        }
    }
}

interface EventHandler<T> {
    fun handle(event: T)
    fun getEventClass(): KClass<*>
}
