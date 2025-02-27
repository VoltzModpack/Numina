/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.lehjr.numina.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Shamelessly ripped from Just Enough Items
 */
public class EventBusHelper {
	private static class Subscription {
		private final IEventBus eventBus;
		private final Consumer<? extends Event> listener;

		private <T extends Event> Subscription(IEventBus eventBus, Consumer<T> listener) {
			this.eventBus = eventBus;
			this.listener = listener;
		}

//		private void unregister() {
//			eventBus.unregister(listener);
//		}
	}

	private static final Map<Object, List<Subscription>> subscriptions = new HashMap<>();

	private static IEventBus getInstance() {
		return MinecraftForge.EVENT_BUS;
	}
//
//	/**
//	 * See {@link EventBusHelper#addListener(Object, IEventBus, Class, Consumer)}
//	 */
//	public static <T extends Event> void addListener(Object owner, Class<T> eventType, Consumer<T> listener) {
//		addListener(owner, getInstance(), eventType, listener);
//	}

	/**
	 * @param owner The owner of this listener. When the owner is unregistered, all listeners with the same owner will
	 *              also be unregistered.
	 */
	public static <T extends Event> void addListener(Object owner, IEventBus eventBus, Class<T> eventType, Consumer<T> listener) {
		subscriptions.computeIfAbsent(owner, e -> new ArrayList<>()).add(new Subscription(eventBus, listener));
		eventBus.addListener(EventPriority.NORMAL, false, eventType, listener);
	}

	/**
	 * See {@link EventBusHelper#addListener(Object, IEventBus, Class, Consumer)}
	 */
	public static <T extends ModLifecycleEvent> void addLifecycleListener(Object owner, IEventBus eventBus, Class<T> eventType, Consumer<T> listener) {
		addListener(owner, eventBus, eventType, listener);
	}
//
//	/**
//	 * See {@link EventBusHelper#addListener(Object, IEventBus, Class, Consumer)}
//	 */
//	public static <T extends Event> void removeListener(Object owner, Consumer<T> listener) {
//		List<Subscription> subs = subscriptions.get(owner);
//		if (subs != null) {
//			subs.removeIf(sub -> {
//				if (Objects.equals(sub.listener, listener)) {
//					sub.unregister();
//					return true;
//				}
//				return false;
//			});
//		}
//	}

	public static void register(Object owner) {
		IEventBus eventBus = getInstance();
		subscriptions.putIfAbsent(owner, new ArrayList<>());
		eventBus.register(owner);
	}
//
//	/**
//	 * Unregisters an object from the event bus.
//	 * All listeners owned by this object will also be unregistered.
//	 * See {@link EventBusHelper#addListener(Object, IEventBus, Class, Consumer)}
//	 */
//	public static void unregister(Object owner) {
//		IEventBus eventBus = getInstance();
//		List<Subscription> subs = subscriptions.remove(owner);
//		if (subs != null) {
//			for (Subscription sub : subs) {
//				sub.unregister();
//			}
//		}
//		eventBus.unregister(owner);
//	}
//
//	public static void post(Event event) {
//		IEventBus eventBus = getInstance();
//		eventBus.post(event);
//	}
//
//	/**
//	 * Registers a listener owned by the given object to the event bus. Due to MinecraftForge/EventBus#39
//	 * references to the registered listeners stay around even after they are unregistered. This method
//	 * works around the issue by only capturing a weak reference to the owner in the actual handler, and
//	 * passing the contained value to the passed handler if the reference is still valid.
//	 */
//	public static <T, E extends Event> void registerWeakListener(T owner, Class<E> eventType, BiConsumer<T, E> handler) {
//		WeakReference<T> weakOwner = new WeakReference<>(owner);
//		addListener(owner, eventType, event -> {
//			T nullableOwner = weakOwner.get();
//			if (nullableOwner != null) {
//				handler.accept(nullableOwner, event);
//			}
//		});
//	}
}
