package com.coconut_palm_software.bread.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.ui.Activator;
import com.coconut_palm_software.bread.ui.BibleEditor;
import com.coconut_palm_software.bread.ui.NetBibleEditorInput;
import com.coconut_palm_software.bread.ui.SingleVerseBibleEditorInput;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RandomVerseHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public RandomVerseHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IBibleReference ref = null;
		try {
			ref = NetBibleReference.randomVerse();
			
			StringBuffer document = new StringBuffer();
			document.append("<html>\n");
			document.append("<body background=\"white\">\n");
			String verseText = "<h2>" + ref.toString() + "</h2>\n" 
			+ ref.getHTML() 
			+ " <a href=\""+ref.toString()+"\"><img src=\"file:///" + NetBibleReference.getShowContextImagePath() + "\"/></a>";
			document.append(verseText);
//			document.append("<h2>" + ref.toString() + "</h2>\n");
//			document.append("<p>#{" + ref.toString() + "}</p>\n");
			document.append("</body>\n");
			document.append("</html>");
			
			SingleVerseBibleEditorInput editorInput = new SingleVerseBibleEditorInput(ref.toString(), document.toString(), ref);
			page.openEditor(editorInput, BibleEditor.ID, true);
		} catch (Exception e) {
			String message = ref != null ? ref.toString() + ": " : "";
			message += e.getLocalizedMessage();
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to open Bible chapter", e));
			MessageDialog.openInformation(
					window.getShell(),
					"Open verse",
					"Unable to open verse: " + message);
		}
		return null;
	}
}
