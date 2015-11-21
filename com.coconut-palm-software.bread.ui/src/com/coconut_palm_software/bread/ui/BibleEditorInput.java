package com.coconut_palm_software.bread.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.optionmonad.Option;

public abstract class BibleEditorInput implements IEditorInput {

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	/**
	 * @return the HTML that renders this editor input in the Browser
	 */
	abstract String getHTML();
	
	abstract void locationChanging(LocationEvent event);
	
	protected void processReferenceLink(LocationEvent event) {
		try {
			String urlReference = URLDecoder.decode(event.location.replace("file:///", ""), "UTF-8");
			ReferenceParser parser = new ReferenceParser(urlReference);
			Option<IBibleReference> bibleReference = parser.reference;
			if (bibleReference.hasValue()) {
				event.doit = false;
				openChapter(bibleReference.get());
				return;
			}
		} catch (UnsupportedEncodingException e) {
		}
	}

	private void openChapter(IBibleReference ref) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			// FIXME: hard code Net Bible stuff for now.  Eventually need an editor input factory
			page.openEditor(new NetBibleEditorInput(ref), BibleEditor.ID, true);
		} catch (PartInitException e1) {
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to open Bible chapter", e1));
		}
	}
	
}
