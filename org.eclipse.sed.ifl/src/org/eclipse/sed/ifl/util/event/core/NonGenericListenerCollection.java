package org.eclipse.sed.ifl.util.event.core;

import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;

public class NonGenericListenerCollection<TEvent>
extends ListenerCollection<TEvent, IListener<TEvent>>
implements INonGenericListenerCollection<TEvent> {

}
