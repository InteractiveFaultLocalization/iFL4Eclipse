package org.eclipse.sed.ifl.util.event;

public interface IListener<TEvent> {
	void Invoke(TEvent event);
}
