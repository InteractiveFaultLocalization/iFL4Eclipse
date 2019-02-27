package org.eclipse.sed.ifl.util.event;

public interface IListenerInvoker<TEvent, TListener extends IListener<TEvent>> extends IListenerCollection<TEvent, TListener> {
	/**
	 * Calling this method will invoke all currently added event listeners for this collection.
	 * This method is thread-safe.
	 * The order of listener's invocation is not determined.
	 */
	void invoke(TEvent event);
}
