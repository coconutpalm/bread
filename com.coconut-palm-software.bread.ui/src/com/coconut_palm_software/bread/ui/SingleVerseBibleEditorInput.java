package com.coconut_palm_software.bread.ui;

import static com.coconut_palm_software.bread.optionmonad.Nulls.option;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.coconut_palm_software.bread.lib.Bible;
import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.optionmonad.Option;

public class SingleVerseBibleEditorInput extends BibleEditorInput {

	public IBibleReference reference;
	
	public Option<String> document;
	public Option<String> documentTitle;
	
	/**
	 * @param documentTitle The document title or null if none
	 * @param document The document or null if none
	 * @param reference
	 */
	public SingleVerseBibleEditorInput(String documentTitle, String document, IBibleReference reference) {
		this.documentTitle = option(documentTitle);
		this.document = option(document);
		if (reference == null) {
			throw new IllegalArgumentException("Null verse reference!");
		}
		this.reference = reference;
	}

	@Override
	public String getName() {
		if (documentTitle.hasValue()) {
			return documentTitle.get();
		}
		return Bible.books[reference.getBook()].name + " " + Integer.toString(reference.getChapter());
	}

	@Override
	String getHTML() {
		if (!document.hasValue())
			try {
				return reference.getHTML();
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		return document.get();
	}

	@Override
	void locationChanging(LocationEvent event) {
		processReferenceLink(event);
	}
}
