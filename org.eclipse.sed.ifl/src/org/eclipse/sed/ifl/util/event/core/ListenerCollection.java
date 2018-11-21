package org.eclipse.sed.ifl.util.event.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.IListenerInvoker;

public class ListenerCollection<TEvent, TListener extends IListener<TEvent>> implements IListenerInvoker<TEvent, TListener> {

	private List<TListener> listeners = Collections.synchronizedList(new ArrayList<TListener>());
	
	@Override
	public void add(TListener listener) {
		listeners.add(listener);
	}

	@Override
	public void remove(TListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void invoke(TEvent event) {
		List<TListener> currents = new ArrayList<>();
		synchronized (listeners) {
			currents.addAll(listeners);
		}
		for (TListener listener : currents) {
			listener.invoke(event);
		}
	}

}
