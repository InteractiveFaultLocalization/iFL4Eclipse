package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wb.swt.SWTResourceManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class SelectedElementUI extends Composite {
	
	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	private Text scoreValueLabel;
	private Text signatureValueLabel;
	private Entry<IMethodDescription, Score> originData;

	public Entry<IMethodDescription, Score> getOriginData() {
		return originData;
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	public SelectedElementUI(Composite parent, int style, CodeElementUI origin) {
		super(parent, style);
		this.originData = (Entry<IMethodDescription, Score>) origin.getData("entry");
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 350;
		setLayoutData(data);
		GridLayout gridLayout = new GridLayout(4, false);
		setLayout(gridLayout);
		
		LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
		
		Label scoreIcon = new Label(this, SWT.NONE);
		scoreIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/score_blue.png"));
		
		Label scoreKeyLabel = new Label(this, SWT.NONE);
		scoreKeyLabel.setText("Score:");
		
		scoreValueLabel = new Text(this, SWT.READ_ONLY);
		scoreValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scoreValueLabel.setText(origin.getScoreValueLabel().getText());
		
		Button removeButton = new Button(this, SWT.NONE);
		removeButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/remove_selection.png"));
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRemoved.invoke(originData);
				((Control) e.getSource()).getParent().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		Label signatureIcon = new Label(this, SWT.NONE);
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_blue.png"));
		
		Label signatureKeyLabel = new Label(this, SWT.NONE);
		signatureKeyLabel.setText("Signature");
		
		signatureValueLabel = new Text(this, SWT.READ_ONLY);
		signatureValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		signatureValueLabel.setText(origin.getSignatureValueLabel().getText());
		new Label(this, SWT.NONE);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				((ScrolledComposite) getParent().getParent()).setMinSize(getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
				getParent().requestLayout();
			}
			
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if(event.button == 1) {
					showSelectedCard.invoke(originData);
				}
			}
		});
		
		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {	
				highlightOriginCard.invoke(originData);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				resetOriginHighlight.invoke(originData);
			}

			@Override
			public void mouseHover(MouseEvent e) {
				
			}
			
		});
		
		this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
		removeButton.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		
		for(Control control : this.getChildren()) {
			if(control instanceof Text) {
				control.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_IBEAM));
			} else if (!(control instanceof Button)){
				dispatchMouseEventToParent(control);
			}
		}
		
	}

	private void dispatchMouseEventToParent(Control child) {
		child.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(event.type == SWT.MouseDown) {
					child.getParent().notifyListeners(SWT.MouseDown, event);
				}
				
			}
			
		});
		child.addListener(SWT.MouseEnter, new Listener() {

			@Override
			public void handleEvent(Event event) {
				child.getParent().notifyListeners(SWT.MouseEnter, event);
				
			}
			
		});
		child.addListener(SWT.MouseExit, new Listener() {

			@Override
			public void handleEvent(Event event) {
				child.getParent().notifyListeners(SWT.MouseExit, event);
				
			}
			
		});
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean setFocus() {
		return super.forceFocus();
	}
	
	public void originChanged(Entry<IMethodDescription, Score> origin) {
		this.originData = origin;
		this.scoreValueLabel.setText(LIMIT_FORMAT.format(origin.getValue().getValue()));
		this.signatureValueLabel.setText(origin.getKey().getId().getSignature());
	}
	
	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> selectedRemoved = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventSelectedRemoved() {
		return selectedRemoved;
	}
	
	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> showSelectedCard = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventShowSelectedCard() {
		return showSelectedCard;
	}
	
	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> highlightOriginCard = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventHiglightOriginCard() {
		return highlightOriginCard;
	}
	
	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> resetOriginHighlight = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventResetOriginHighlight() {
		return resetOriginHighlight;
	}
}
