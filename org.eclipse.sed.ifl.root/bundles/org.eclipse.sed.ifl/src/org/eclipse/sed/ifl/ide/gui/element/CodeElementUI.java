package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.wb.swt.SWTResourceManager;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public class CodeElementUI extends Composite {

	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	Text scoreValueLabel;
	Text nameValueLabel;
	Text pathValueLabel;
	Text positionValueLabel;
	Text parentTypeValueLabel;
	Text interactivityValueLabel;
	Text signatureValueLabel;
	Text contextSizeValueLabel;
	
	Label scoreIcon;
	Label nameIcon;
	Label pathIcon;
	Label positionIcon;
	Label parentTypeIcon;
	Label interactivityIcon;
	Label signatureIcon;
	Label contextSizeIcon;

	public void resetNeutralIcons() {
		scoreIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/score_blue.png"));
		nameIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/name_blue.png"));
		pathIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/path_blue.png"));
		positionIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/position_blue.png"));
		parentTypeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/parent_type_blue.png"));
		interactivityIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback_blue.png"));
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_blue.png"));
		contextSizeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/context_size_blue.png"));
	}
	
	public void setContextIcons() {
		scoreIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/score_red.png"));
		nameIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/name_red.png"));
		pathIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/path_red.png"));
		positionIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/position_red.png"));
		parentTypeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/parent_type_red.png"));
		interactivityIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback_red.png"));
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_red.png"));
		contextSizeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/context_size_red.png"));
	}
	
	public Label getScoreIcon() {
		return scoreIcon;
	}

	public Label getNameIcon() {
		return nameIcon;
	}

	public Label getPathIcon() {
		return pathIcon;
	}

	public Label getPositionIcon() {
		return positionIcon;
	}

	public Label getParentTypeIcon() {
		return parentTypeIcon;
	}

	public Label getInteractivityIcon() {
		return interactivityIcon;
	}

	public Label getSignatureIcon() {
		return signatureIcon;
	}

	public Label getContextSizeIcon() {
		return contextSizeIcon;
	}

	public Text getScoreValueLabel() {
		return scoreValueLabel;
	}

	public Text getNameValueLabel() {
		return nameValueLabel;
	}

	public Text getPathValueLabel() {
		return pathValueLabel;
	}

	public Text getPositionValueLabel() {
		return positionValueLabel;
	}

	public Text getParentTypeValueLabel() {
		return parentTypeValueLabel;
	}

	public Text getInteractivityValueLabel() {
		return interactivityValueLabel;
	}

	public Text getSignatureValueLabel() {
		return signatureValueLabel;
	}

	public Text getContextSizeValueLabel() {
		return contextSizeValueLabel;
	}
	
	public CodeElementUI(Composite parent, int style,
			Score score,
			String name,
			String signature,
			String parentType,
			String path,
			String position,
			Integer contextSize,
			Boolean interactivity,
			Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		super(parent, SWT.NONE);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 310;
		setLayoutData(data);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginTop = 5;
		gridLayout.marginLeft = 5;
		setLayout(gridLayout);
	//	setSize(300,213);
		
		scoreIcon = new Label(this, SWT.NONE);
		scoreIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/score_blue.png"));
		scoreIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label scoreKeyLabel = new Label(this, SWT.NONE);
		scoreKeyLabel.setText("Score:");
		
		scoreValueLabel = new Text(this, SWT.READ_ONLY);
		LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
		scoreValueLabel.setText(checkScore(score));
		
		Label lastActionLabel = new Label(this, SWT.NONE);
		lastActionLabel.setImage(checkLastAction(lastAction));
		
		nameIcon = new Label(this, SWT.NONE);
		nameIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/name_blue.png"));
		nameIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label nameKeyLabel = new Label(this, SWT.NONE);
		nameKeyLabel.setText("Name:");
		
		nameValueLabel = new Text(this, SWT.READ_ONLY);
		nameValueLabel.setText(name);
		new Label(this, SWT.NONE);
		
		signatureIcon = new Label(this, SWT.NONE);
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_blue.png"));
		signatureIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label signatureKeyLabel = new Label(this, SWT.NONE);
		signatureKeyLabel.setText("Signature:");
		
		signatureValueLabel = new Text(this, SWT.READ_ONLY);
		GridData gd_signatureValueLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_signatureValueLabel.widthHint = 140;
		signatureValueLabel.setLayoutData(gd_signatureValueLabel);
		signatureValueLabel.setText(signature);
		new Label(this, SWT.NONE);
		
		parentTypeIcon = new Label(this, SWT.NONE);
		parentTypeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/parent_type_blue.png"));
		parentTypeIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label parentTypeKeyLabel = new Label(this, SWT.NONE);
		parentTypeKeyLabel.setText("Parent type:");
		
		parentTypeValueLabel = new Text(this, SWT.READ_ONLY);
		parentTypeValueLabel.setText(parentType);
		new Label(this, SWT.NONE);
		
		pathIcon = new Label(this, SWT.NONE);
		pathIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/path_blue.png"));
		pathIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label pathKeyLabel = new Label(this, SWT.NONE);
		pathKeyLabel.setText("Path:");
		
		pathValueLabel = new Text(this, SWT.READ_ONLY);
		GridData gd_pathValueLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_pathValueLabel.widthHint = 140;
		pathValueLabel.setLayoutData(gd_pathValueLabel);
		pathValueLabel.setText(path);
		new Label(this, SWT.NONE);
		
		positionIcon = new Label(this, SWT.NONE);
		positionIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/position_blue.png"));
		positionIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label positionKeyLabel = new Label(this, SWT.NONE);
		positionKeyLabel.setText("Position:");
		
		positionValueLabel = new Text(this, SWT.READ_ONLY);
		positionValueLabel.setText(position);
		new Label(this, SWT.NONE);
		
		contextSizeIcon = new Label(this, SWT.NONE);
		contextSizeIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/context_size_blue.png"));
		contextSizeIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label contextSizeKeyLabel = new Label(this, SWT.NONE);
		contextSizeKeyLabel.setText("Context size:");
		
		contextSizeValueLabel = new Text(this, SWT.READ_ONLY);
		contextSizeValueLabel.setText(contextSize.toString());
		new Label(this, SWT.NONE);
		
		interactivityIcon = new Label(this, SWT.NONE);
		interactivityIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback_blue.png"));
		interactivityIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Label interactivityKeyLabel = new Label(this, SWT.NONE);
		interactivityKeyLabel.setText("Interactivity:");
		
		interactivityValueLabel = new Text(this, SWT.READ_ONLY);
		if(interactivity) {
			interactivityValueLabel.setText("User feedback enabled");
			interactivityValueLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		} else {
			interactivityValueLabel.setText("User feedback disabled");
			interactivityValueLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
		}
		
		new Label(this, SWT.NONE);
		
		this.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
		
		for(Control control : this.getChildren()) {
			if(control instanceof Text) {
				control.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_IBEAM));
			} else {
				dispatchMouseEventToParent(control);
			}
		}
		
	}	
	
	private String checkScore(Score score){
		if (score.isDefinit()) {
			LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
			return LIMIT_FORMAT.format(score.getValue());
		} else {
			return "undefined";
		}
	}
	
	private Image checkLastAction(Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		if (lastAction != null) {
			String iconPath = lastAction.getChange().getIconPath();
			if (iconPath != null) {
				Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
				return icon;
			}
		} return null;
	}
	
	 public boolean setFocus()
	    {
	        return super.forceFocus();
	    }

	public void setChildrenBackgroundColor() {
		for(Control control: this.getChildren()) {
			control.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
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
	}
}
