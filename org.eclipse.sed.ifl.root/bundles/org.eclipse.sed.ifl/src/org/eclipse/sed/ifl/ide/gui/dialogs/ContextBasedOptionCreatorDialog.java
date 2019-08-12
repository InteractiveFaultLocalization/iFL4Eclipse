package org.eclipse.sed.ifl.ide.gui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;

public class ContextBasedOptionCreatorDialog extends Dialog implements IEmbedee {

	public ContextBasedOptionCreatorDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(3, false));
		for (IEmbeddable embedded : contents) {
			embedded.setParent(composite);
		}
		return composite;
	}
	
	private List<Double> selected;

	private List<Double> context;
	
	private List<Double> other;
	
	public void setSelected(List<Defineable<Double>> selected) {
		this.selected = selected.stream()
		.filter(item -> item.isDefinit())
		.map(item -> item.getValue())
		.collect(Collectors.toList());
	}

	public void setContext(List<Defineable<Double>> context) {
		this.context = context.stream()
		.filter(item -> item.isDefinit())
		.map(item -> item.getValue())
		.collect(Collectors.toList());
	}

	public void setOthers(List<Defineable<Double>> other) {
		this.other = other.stream()
		.filter(item -> item.isDefinit())
		.map(item -> item.getValue())
		.collect(Collectors.toList());
	}

	private List<IEmbeddable> contents = new ArrayList<>();
	
	@Override
	public void embed(IEmbeddable embedded) {
		contents.add(embedded);
	}
}
