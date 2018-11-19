package org.eclipse.sed.ifl.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import java.util.List;

import javax.inject.Inject;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;

public class MainPart extends ViewPart {
	
	public MainPart() {
	}

	public static final String ID = "org.eclipse.sed.ifl.views.IFLMainView";

	@Inject IWorkbench workbench;
	private Table table;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(100);
		tblclmnId.setText("Name");
		
		TableColumn tblclmnSigniture = new TableColumn(table, SWT.NONE);
		tblclmnSigniture.setWidth(100);
		tblclmnSigniture.setText("Signiture");
		
		TableColumn tblclmnReturnType = new TableColumn(table, SWT.NONE);
		tblclmnReturnType.setWidth(100);
		tblclmnReturnType.setText("Return type");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}
	
	public void setMethodList(List<IMethod> list) {
		table.setData(list);
		for (IMethod method : list) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, method.getElementName());
			try {
				item.setText(1, method.getSignature());
				item.setText(2, method.getReturnType());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainPart.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {
	}

	private void hookDoubleClickAction() {
	}

	@Override
	public void setFocus() {
	}
}
