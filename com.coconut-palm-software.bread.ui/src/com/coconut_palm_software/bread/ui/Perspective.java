package com.coconut_palm_software.bread.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	private static final String PROJECT_EXPLORER =
		"org.eclipse.ui.navigator.ProjectExplorer";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.33f, editorArea);
		left.addView(View.ID);
		left.addView(PROJECT_EXPLORER);
//		layout.addView(IPageLayout.ID_RES_NAV, IPageLayout.LEFT, 0.33f, editorArea);
	}

}
