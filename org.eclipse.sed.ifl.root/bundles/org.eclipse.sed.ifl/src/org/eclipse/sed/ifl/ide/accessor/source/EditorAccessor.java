package org.eclipse.sed.ifl.ide.accessor.source;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorAccessor {
	public void open(String path, int offset) {
		File fileToOpen = new File(path);
		 
		if (fileToOpen.exists() && fileToOpen.isFile()) {
		    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		 
		    try {
		        IEditorPart editor = IDE.openEditorOnFileStore( page, fileStore );
		        if (editor instanceof ITextEditor) {
		        	ITextEditor textEditor = (ITextEditor)editor;
		        	textEditor.selectAndReveal(offset, 0);
		        }
		    } catch ( PartInitException e ) {
		        //TODO:Put your exception handler here if you wish to
		    }
		} else {
		    //TODO:Do something if the file does not exist
		}
	}
}
