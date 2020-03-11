package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HostAndPortInputDialog extends TitleAreaDialog {

	 	private Text txtHost;
	    private Text txtPort;

	    private String host;
	    private String port;

	    public HostAndPortInputDialog(Shell parentShell) {
	        super(parentShell);
	    }

	    @Override
	    public void create() {
	        super.create();
	        getButton(IDialogConstants.OK_ID).setEnabled(false);
	        setTitle("Enter host name and port number");
	        setMessage("Neither of the required fields are allowed to be left blank.", IMessageProvider.INFORMATION);
	    }

	    @Override
	    protected Control createDialogArea(Composite parent) {
	        Composite area = (Composite) super.createDialogArea(parent);
	        Composite container = new Composite(area, SWT.NONE);
	        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        GridLayout layout = new GridLayout(2, false);
	        container.setLayout(layout);

	        createHost(container);
	        createPort(container);
	        
	        return area;
	    }

	    private void createHost(Composite container) {
	        Label lbtFirstName = new Label(container, SWT.NONE);
	        lbtFirstName.setText("Host name");

	        GridData dataHost = new GridData();
	        dataHost.grabExcessHorizontalSpace = true;
	        dataHost.horizontalAlignment = GridData.FILL;

	        txtHost = new Text(container, SWT.BORDER);
	        txtHost.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					getButton(IDialogConstants.OK_ID).setEnabled(validateInput());
				}
	        	
	        });
	        txtHost.setLayoutData(dataHost);
	    }

	    private void createPort(Composite container) {
	        Label lbtPort = new Label(container, SWT.NONE);
	        lbtPort.setText("Port number");

	        GridData dataPort = new GridData();
	        dataPort.grabExcessHorizontalSpace = true;
	        dataPort.horizontalAlignment = GridData.FILL;
	        txtPort = new Text(container, SWT.BORDER);
	        txtHost.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					getButton(IDialogConstants.OK_ID).setEnabled(validateInput());
				}
	        	
	        });
	        txtPort.setLayoutData(dataPort);
	    }



	    @Override
	    protected boolean isResizable() {
	        return false;
	    }

	    private void saveInput() {
	        host = txtHost.getText();
	        port = txtPort.getText();

	    }

	    @Override
	    protected void okPressed() {
	        saveInput();
	        super.okPressed();
	    }

	    public String getHost() {
	        return host;
	    }

	    public String getPort() {
	        return port;
	    }

	    private boolean validateInput(){
	    	boolean rValue = true;
	    	if(txtHost.equals("") || txtHost == null || txtPort.equals("") || txtPort == null) {
	    		rValue = false;
	    	}
	    	return rValue;
	    }
	    
	    @Override
	    protected boolean canHandleShellCloseEvent() {
	        return false;
	    }
	    
	    @Override
	    protected Button createButton(Composite parent, int id,
	            String label, boolean defaultButton) {
	        if (id == IDialogConstants.CANCEL_ID) return null;
	        return super.createButton(parent, id, label, defaultButton);
	    }
}
