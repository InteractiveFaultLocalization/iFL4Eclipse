package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.sed.ifl.control.score.displayable.DisplayableScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;

public class CardHolderComposite extends Composite {

	private HashMap<Integer, List<DisplayableScore>> contents = new HashMap<>();
	
	private Composite cardArea;
	private Composite buttonArea;
	private int pageCount = 1;
	private CLabel pageCountLabel;
	
	public CardHolderComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);
		
		cardArea = new Composite(this, SWT.NONE);
		cardArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_cardArea = new GridLayout(4, true);
		cardArea.setLayout(gl_cardArea);

		//nyilacsk�k hely�re fancy ikonok
		buttonArea = new Composite(this, SWT.NONE);
		RowLayout rl_buttonArea = new RowLayout(SWT.HORIZONTAL);
		rl_buttonArea.marginWidth = 5;
		rl_buttonArea.marginBottom = 0;
		rl_buttonArea.marginTop = 0;
		rl_buttonArea.marginRight = 0;
		rl_buttonArea.marginLeft = 0;
		rl_buttonArea.justify = true;
		buttonArea.setLayout(rl_buttonArea);
		buttonArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		setSize(1429, 575);
		
		Button home = new Button(buttonArea, SWT.NONE);
		home.setText("<<");
		home.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageCount(1, 0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		Button pageDown = new Button(buttonArea, SWT.NONE);
		pageDown.setText("<");
		pageDown.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageCount(pageCount, -1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			
		});
		
		pageCountLabel = new CLabel(buttonArea, SWT.CENTER);
		pageCountLabel.setLayoutData(new RowData(SWT.DEFAULT, 25));
		pageCountLabel.setAlignment(SWT.CENTER);
		pageCountLabel.setText("");
		
		Button pageUp = new Button(buttonArea, SWT.NONE);
		pageUp.setText(">");
		pageUp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageCount(pageCount, +1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			
		});
		
		Button end = new Button(buttonArea, SWT.NONE);
		end.setText(">>");
		end.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageCount(contents.size(), 0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
	}
	
	public void setContent(List<DisplayableScore> scores) {
		contents.clear();
		List<DisplayableScore> elements = new ArrayList<>();
		int numberOfElementsPerPage = 8;
		int counter = 1;
		int pageNumber = 1;
		for(DisplayableScore pageContent : scores) {
			if(counter<numberOfElementsPerPage) {
				elements.add(pageContent);
				counter++;
			} else {
				elements.add(pageContent);
				contents.put(pageNumber, elements);
				pageNumber++;
				elements = new ArrayList<DisplayableScore>();
				counter = 1;
			}
		}
		if(counter>1) {
			contents.put(pageNumber, elements);

		}
		setPageCount(1, 0);
	}
	
	private void setPageContent(int pageNumber) {
		clearMethodScores();
		for (DisplayableScore displayable : contents.get(pageNumber)) {
			CodeElementUI element = new CodeElementUI(cardArea, SWT.NONE,
					displayable.getScore(),
					displayable.getMethodDescription().getId().getName(),
					displayable.getMethodDescription().getId().getSignature(),
					displayable.getMethodDescription().getId().getParentType(),
					displayable.getMethodDescription().getLocation().getAbsolutePath(),
					displayable.getMethodDescription().getLocation().getBegining().getOffset().toString(),
					displayable.getMethodDescription().getContext().size(),
					displayable.getMethodDescription().isInteractive(),
					displayable.getLastAction());
			element.setData(displayable.getMethodDescription());
			element.setData("score", displayable.getScore());
			element.setData("entry", displayable);
			
			element.setChildrenBackgroundColor();
		}
		cardArea.requestLayout();
		displayedPageChanged.invoke(getDisplayedCards());
	}
	
	public List<CodeElementUI> getDisplayedCards() {
		ArrayList<CodeElementUI> rvList = new ArrayList<CodeElementUI>();
		for (Control control: cardArea.getChildren()) {
			rvList.add(((CodeElementUI)control));
		}
		return rvList;
	}
	
	//change f�l�s
	public void setPageCount(int currentPage, int change) {
		if(currentPage+change >= 1 && currentPage+change <= contents.size()) {
			setPageContent(currentPage+change);
			pageCount = currentPage+change;
			pageCountLabel.setText(pageCount + "/" + contents.size());
			pageCountLabel.requestLayout();
			pageCountLabel.setVisible(true);
		}
	}

	public void clearMethodScores() {
		for(Control control : cardArea.getChildren()) {
			control.setMenu(null);
			control.dispose();
		}
	}
	
	public HashMap<Integer, List<DisplayableScore>> getContents() {
		return contents;
	}
	
	private NonGenericListenerCollection<List<CodeElementUI>> displayedPageChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<CodeElementUI>> eventDisplayedPageChanged() {
		return displayedPageChanged;
	}
}
