package org.eclipse.sed.ifl.util.event;

public interface IListenerInvoker<TEvent, TListener extends IListener<TEvent>> extends IListenerCollection<TEvent, TListener> {
	void invoke(TEvent event);
}
