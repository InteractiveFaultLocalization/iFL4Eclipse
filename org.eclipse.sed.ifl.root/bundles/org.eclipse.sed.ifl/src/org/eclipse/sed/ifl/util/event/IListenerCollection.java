package org.eclipse.sed.ifl.util.event;

public interface IListenerCollection<TEvent, TListener extends IListener<TEvent>> {
	void add(TListener listener);
	
	void remove(TListener listener);
}
