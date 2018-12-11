package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import javax.inject.Inject;
import swing2swt.layout.BorderLayout;

public class MainPart extends ViewPart {
	
	public MainPart() {
		System.out.println("mainpart ctor");
	}

	public static final String ID = "org.eclipse.sed.ifl.views.IFLMainView";

	@Inject IWorkbench workbench;
	 
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

	private Composite composite;
	
	@Override
	public void createPartControl(Composite parent) {
		composite = parent;
		parent.setLayout(new BorderLayout(0, 0));
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
	
	private NonGenericListenerCollection<Action> scoreLoadRequested = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Action> eventScoreLoadRequested() {
		return scoreLoadRequested;
	}
	
	private Action loadScoreAction;
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(loadScoreAction);
	}

	private void makeActions() {
		loadScoreAction = new Action() {
			@Override
			public void run() {
				super.run();
				scoreLoadRequested.invoke(this);
			}
			
			@Override
			public String getText() {
				return "Load scores...";
			}
		};
	}

	private void hookDoubleClickAction() {
	}

	@Override
	public void setFocus() {
	}
	
	public Composite getUI() {
		return composite;
	}
}
