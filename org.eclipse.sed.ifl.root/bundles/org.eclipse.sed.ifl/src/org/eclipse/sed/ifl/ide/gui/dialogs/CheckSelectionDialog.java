package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CheckSelectionDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table table;
	private List<Entry<IMethodDescription, Score>> currentlySelected;
	private TableColumn signatureColumn;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CheckSelectionDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		signatureColumn = new TableColumn(table, SWT.LEFT);
		signatureColumn.setResizable(false);
		signatureColumn.setWidth(100);
		signatureColumn.setText("Element signature");
		
		Button removeButton = new Button(shell, SWT.NONE);
		removeButton.setText("Remove");
		
		Button removeAllButton = new Button(shell, SWT.NONE);
		removeAllButton.setText("Remove all");

		addTableContents();
	}

	private void addTableContents() {
		for(Entry<IMethodDescription, Score> element : currentlySelected) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(table.indexOf(signatureColumn), element.getKey().getId().getSignature());
		}
		
	}
}
