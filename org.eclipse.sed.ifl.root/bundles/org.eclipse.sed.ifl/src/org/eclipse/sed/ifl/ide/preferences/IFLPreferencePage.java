package org.eclipse.sed.ifl.ide.preferences;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.PreferencePropertyChangedEvent;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class IFLPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
	
	public IFLPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences page of the iFL plug-in");
	}
	
	private StringFieldEditor userIdField;
	private StringFieldEditor scenarioIdField;
	private StringFieldEditor hostField;
	private StringFieldEditor portField;
	private Text desc;
	private Text agreement;
	private Button idButton;
	private Text idText;
	private Button connectButton;
	private Text connectText;
	private Button logCheckButton;
	private Text logCheckText;
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		//tároljuk el a userID, MAC, scenarioID konkatenált stringjét (MD5 esetleg) az ID node-ba
		//módosítás esetén újragenerálni
		//legyen kimásolható mezõbe ezek alatt
		
		//szerver tesztelés gomb
		
		agreement = new Text(getFieldEditorParent(), SWT.READ_ONLY);
		agreement.setText("User agreement: ");
		
		desc = new Text(getFieldEditorParent(), SWT.MULTI | SWT.READ_ONLY);
		desc.setText("By enabling the logging function, you accept that your given user ID, scenario ID, the\n"
				+ "unique ID generated from the aforementioned two fields, your MAC address or IP address and\n"
				+ "all your activity within the plug-in (including, but not limited to: highlighting elements,\n"
				+ "loading scores, giving feedback, clicking on any Eclipse panel) will be stored on our server.");
		
		logCheckText = new Text(getFieldEditorParent(), SWT.READ_ONLY);
		logCheckText.setText("Enable logging");
		
		logCheckButton = new Button(getFieldEditorParent(), SWT.CHECK);
		logCheckButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkLogEnabled();
				getPreferenceStore().setValue("logKey", logCheckButton.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			
		});
		
		userIdField = new StringFieldEditor("userId", "User ID: ", getFieldEditorParent());
		addField(userIdField);
		
		scenarioIdField = new StringFieldEditor("scenarioId", "Scenario ID: ", getFieldEditorParent());
		addField(scenarioIdField);
		
		hostField = new StringFieldEditor("host", "Host: ", getFieldEditorParent());
		addField(hostField);
		
		portField = new StringFieldEditor("port", "Port: ", getFieldEditorParent());
		addField(portField);
		
		idButton = new Button(getFieldEditorParent(), SWT.NONE);
		idButton.setText("Generate ID");
		idButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String generatedId = generateId();
				idText.setText(generatedId);
				getPreferenceStore().setValue("generatedId", generatedId);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		
		idText = new Text(getFieldEditorParent(), SWT.READ_ONLY);
		idText.setText("Your unique ID will be generated here. Press the button.");
		
		connectButton = new Button(getFieldEditorParent(), SWT.NONE);
		connectButton.setText("Test connection");
		connectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(testConnection()) {
					connectText.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					connectText.setText("Connection working.");
				} else {
					connectText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
					connectText.setText("Connection not working.");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		
		connectText = new Text(getFieldEditorParent(), SWT.READ_ONLY);
		connectText.setText("Test your connection to the server. Press the button.");
		
		checkLogEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				activityMonitor.log(new PreferencePropertyChangedEvent( event.getProperty(), event.getOldValue(), event.getNewValue()));
			}
			
		});
	}
	
	private String generateId() {
		String generatedId = scenarioIdField.getStringValue().concat(userIdField.getStringValue());
		byte[] mdBytes = generatedId.getBytes(StandardCharsets.UTF_8);
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			mdBytes = md.digest(mdBytes);
		    StringBuilder hex = new StringBuilder();
		    for (byte b : mdBytes) {
		        hex.append(String.format("%02x", b));
		    }
		    generatedId = hex.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm.");
		}
		System.out.println(generatedId);
		return generatedId;
	}
	
	private Boolean testConnection() {
		boolean rValue = false;
	
		try {
		GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using(hostField.getStringValue(),
					Integer.parseInt(portField.getStringValue()),"g"));
		List<Vertex> nodeList = g.V().toList();
		rValue = true;
		if (g != null) {
			EU.tryUnchecked(g::close);
		}
		} catch (Exception e){
			
		}
		
		return rValue;
	}
	
	public void checkLogEnabled() {
		userIdField.getTextControl(getFieldEditorParent()).setEnabled(logCheckButton.getSelection());
		scenarioIdField.getTextControl(getFieldEditorParent()).setEnabled(logCheckButton.getSelection());
		hostField.getTextControl(getFieldEditorParent()).setEnabled(logCheckButton.getSelection());
		portField.getTextControl(getFieldEditorParent()).setEnabled(logCheckButton.getSelection());
		idButton.setEnabled(logCheckButton.getSelection());
		connectButton.setEnabled(logCheckButton.getSelection());
		if (logCheckButton.getSelection() == false) {
			idText.setText("Your unique ID will be generated here. Press the button.");
			connectText.setText("Test your connection to the server. Press the button.");
			connectText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		}
	}
}