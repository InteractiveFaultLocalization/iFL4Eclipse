package org.eclipse.sed.ifl.ide.gui;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.ResourceManager;

import swing2swt.layout.BorderLayout;

public class MainPart implements IEmbeddable, IEmbedee {

	public MainPart() {
		System.out.println("mainpart ctor");
	}

	public static final String ID = "org.eclipse.sed.ifl.views.IFLMainView";

	private Composite composite;

	@PostConstruct
	public void createPartControl(Composite parent) {
		composite = parent;
		parent.setLayout(new BorderLayout(0, 0));
		makeActions();
		hookDoubleClickAction();
	}

	private NonGenericListenerCollection<Action> scoreLoadRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Action> eventScoreLoadRequested() {
		return scoreLoadRequested;
	}

	private NonGenericListenerCollection<Boolean> hideUndefinedRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Boolean> eventHideUndefinedRequested() {
		return hideUndefinedRequested;
	}

	private NonGenericListenerCollection<Action> scoreRecalculateRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Action> eventScoreRecalculateRequested() {
		return scoreRecalculateRequested;
	}

	private Action loadScoreAction;
	private Action hideUndefinedAction;
	private Action recalculateScoreAction;

	private void fillLocalToolBar(IToolBarManager manager) {
		ActionContributionItem loadScore = new ActionContributionItem(loadScoreAction);
		loadScore.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		manager.add(loadScore);
		ActionContributionItem hideUndefined = new ActionContributionItem(hideUndefinedAction);
		hideUndefined.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		manager.add(hideUndefined);
		hideUndefinedAction.setChecked(true);
		ActionContributionItem recalculateScore = new ActionContributionItem(recalculateScoreAction);
		recalculateScore.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		manager.add(recalculateScore);
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

			@Override
			public ImageDescriptor getImageDescriptor() {
				return ImageDescriptor.createFromImage(
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/load-button-icon.png"));
			}
		};
		hideUndefinedAction = new Action() {
			@Override
			public void run() {
				System.out.println("hiding undefined scores requested on the GUI to set to "
						+ (isChecked() ? "enabled" : "disabled"));
				hideUndefinedRequested.invoke(isChecked());
			}

			@Override
			public int getStyle() {
				return AS_CHECK_BOX;
			}

			@Override
			public String getText() {
				return "Hide undefined";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return ImageDescriptor.createFromImage(
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/hide-undefined-icon.png"));
			}
		};

		recalculateScoreAction = new Action() {
//			@Override
//			public void run() {
//				super.run();
//				scoreRecalculateRequested.invoke(this);
//			}

			@Override
			public String getText() {
				return "Recalculate scores...";
			}

//			@Override
//			public ImageDescriptor getImageDescriptor() {
//				return ImageDescriptor.createFromImage(
//						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/recalculate-button-icon.png"));
//			}
		};

	}

	private void hookDoubleClickAction() {
	}

	@Focus
	public void setFocus() {
	}

	@Override
	public void setParent(Composite parent) {
		composite.setParent(parent);
	}

	@Override
	public void embed(IEmbeddable embedded) {
		embedded.setParent(composite);
	}
}
