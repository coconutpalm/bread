package com.coconut_palm_software.bread.ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;

public interface IVerseSelector {
	List getBook();
	List getChapter();
	Button getOkButton();
	Button getCancelButton();
}
