package org.eclipse.sed.ifl.view;


import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View implements IEmbedee {

	private ContextBasedOptionCreatorDialog dialog = new ContextBasedOptionCreatorDialog(Display.getCurrent().getActiveShell());
	
	
	public ContextBasedOptionCreatorDialog getDialog() {
		return dialog;
	}

	@Override
	public void init() {
		dialog.eventCustomFeedbackSelected().add(customOptionDialogListener);
		dialog.eventRefreshUi().add(refreshUiListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		dialog.eventCustomFeedbackSelected().remove(customOptionDialogListener);
		dialog.eventRefreshUi().remove(refreshUiListener);
		super.teardown();
	}
	
	public void display() {
		dialog.open();
	}

	@Override
	public void embed(IEmbeddable embedded) {
		dialog.embed(embedded);
	}
	
	private NonGenericListenerCollection<Boolean> customOptionDialog = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventCustomOptionDialog() {
		return customOptionDialog;
	}

	private IListener<Boolean> customOptionDialogListener = customOptionDialog::invoke;
	
	private NonGenericListenerCollection<Boolean> refreshUi = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventRefreshUi() {
		return refreshUi;
	}

	private IListener<Boolean> refreshUiListener = refreshUi::invoke;
}
