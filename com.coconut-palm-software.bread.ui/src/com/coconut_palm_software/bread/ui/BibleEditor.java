package com.coconut_palm_software.bread.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BibleEditor extends EditorPart {

	public static final String ID = "com.coconut_palm_software.bread.ui.Bible";
	private Browser browser;
	private BibleEditorInput editorInput;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// NOOP
	}

	@Override
	public void doSaveAs() {
		// NOOP
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		editorInput = (BibleEditorInput)input;
		setPartName(editorInput.getName());
	}

	@Override
	public boolean isDirty() {
		// The Bible is never dirty ;-)
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		browser = new Browser(parent, SWT.NULL);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setText(editorInput.getHTML());
		browser.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
//				mouseDown(e);
			}

			@Override
			public void mouseDown(MouseEvent e) {
//				browser.setFocus();
			}

			@Override
			public void mouseUp(MouseEvent e) {
//				mouseDown(e);
			}
		});
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				editorInput.locationChanging(event);
				setPartName(editorInput.getName());
			}
		});
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

}
