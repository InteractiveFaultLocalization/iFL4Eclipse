package org.eclipse.sed.ifl.util.event;

@FunctionalInterface
public interface IListener<TEvent> {
	void invoke(TEvent event);
}
