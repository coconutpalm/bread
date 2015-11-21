package com.coconut_palm_software.bread.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.coconut_palm_software.bread.lib.Book;
import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.SearchEngine;
import com.coconut_palm_software.bread.optionmonad.Option;

public class View extends ViewPart {
	public static final String ID = "com.coconut_palm_software.bread.ui.view";
	private SearchEngine searchEngine = new SearchEngine();
	private Text openField;
	private List books;
	private List chapters;
	private List verses;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		
		openField = new Text(parent, SWT.BORDER);
		openField.setText("Type reference(s) or search for words");
		openField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		openField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == '\r') {
					openEditor(searchEngine.getCompleteSearchResults());
					return;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode != '\r' && e.character != 0) {
					searchEngine.updateSearch(openField.getText());
				}
			}
		});
		openField.addFocusListener(new FocusAdapter() {
			private boolean cleared = false;
			public void focusGained(FocusEvent e) {
				if (!cleared) {
					openField.setText("");
					cleared = true;
				} else {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							openField.selectAll();
						}
					});
				}
			}
		});
		
		// Set up the search results sashes/containers
		
		SashForm headerVerseSash = new SashForm(parent, SWT.VERTICAL);
		headerVerseSash.setLayout(new FillLayout());
		headerVerseSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		SashForm bookChapterSash = new SashForm(headerVerseSash, SWT.HORIZONTAL);
		bookChapterSash.setLayout(new FillLayout());
		
		Composite browseLeft = new Composite(bookChapterSash, SWT.NULL);
		browseLeft.setLayout(new GridLayout(1, false));
		Composite browseRight = new Composite(bookChapterSash, SWT.NULL);
		browseRight.setLayout(new GridLayout(1, false));
		
		bookChapterSash.setWeights(new int[] {40, 60});
		
		Composite browseBottom = new Composite(headerVerseSash, SWT.NULL);
		browseBottom.setLayout(new GridLayout(1, false));
		
		headerVerseSash.setWeights(new int[] {60, 40});
		
		// Now fill in the various containers
		
		new Label(browseLeft, SWT.NULL).setText("Book");
		new Label(browseRight, SWT.NULL).setText("Chapter");
		
		books = new List(browseLeft, SWT.BORDER | SWT.V_SCROLL);
		books.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		chapters = new List(browseRight, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		chapters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		books.addSelectionListener(new SelectionListener() {
			int oldSelection = -1;
			@Override
			public void widgetSelected(SelectionEvent e) {
				int newSelection = books.getSelectionIndex();
				if (newSelection != oldSelection) {
					searchEngine.bookSelected(newSelection);
					oldSelection = newSelection;
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int selectionIndex = books.getSelectionIndex();
				Option<BibleEditorInput> bookSearchResults = searchEngine.getBookSearchResults(selectionIndex);
				openEditor(bookSearchResults);
			}
		});
		
		chapters.addSelectionListener(new SelectionListener() {
			private int oldSelection;

			@Override
			public void widgetSelected(SelectionEvent e) {
				int newSelection = chapters.getSelectionIndex();
				if (newSelection != oldSelection) {
					searchEngine.chapterSelected(newSelection);
					oldSelection = newSelection;
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int selectionIndex = chapters.getSelectionIndex();
				Option<BibleEditorInput> chapterSearchResults = searchEngine.getChapterSearchResults(selectionIndex);
				openEditor(chapterSearchResults);
			}
		});

		
		new Label(browseBottom, SWT.NULL).setText("Verse");
		verses = new List(browseBottom, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		verses.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		verses.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int selectionIndex = verses.getSelectionIndex();
				Option<BibleEditorInput> verseSearchResults = searchEngine.getVerseSearchResults(selectionIndex);
				openEditor(verseSearchResults);
			}
		});
		
		// Data binding...
		processPendingSWTEvents();
		bindBooks(books);
		bindReferenceList(chapters, searchEngine.getChapterList());
		bindReferenceList(verses, searchEngine.getVerseList());
		
		searchEngine.updateSearch("");		// Start everything off...
	}

	private void processPendingSWTEvents() {
		long time = System.currentTimeMillis();
		Display display = Display.getDefault();
		while (System.currentTimeMillis() - time < 250) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void bindBooks(List books) {
		DataBindingContext dbc = new DataBindingContext(SWTObservables.getRealm(books.getDisplay()));
		dbc.bindList(SWTObservables.observeItems(books), 
				searchEngine.getBookList(), null, 
				new UpdateListStrategy() {
					@Override
					public Object convert(Object element) {
						Book book = (Book) element;
						return book.name;
					}
				});
	}

	private void bindReferenceList(List target, IObservableList model) {
		DataBindingContext dbc = new DataBindingContext(SWTObservables.getRealm(books.getDisplay()));
		dbc.bindList(SWTObservables.observeItems(target), 
				model, null, 
				updateReferenceStrategy);
	}
	
	UpdateListStrategy updateReferenceStrategy = new UpdateListStrategy() {
		@Override
		public Object convert(Object element) {
			IBibleReference reference = (IBibleReference) element;
			String firstLine="";
			try {
				firstLine = reference.getFirstLineText();
			} catch (Exception e) {
				//noop
			}
			return reference.getChapter() + 
				("".equals(firstLine) ? "" : ":" + reference.getVerse() + " " + firstLine);		
		}
	};
	
	private void openEditor(Option<BibleEditorInput> editorInput) {
		if (!editorInput.hasValue()) {
			return;
		}
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(editorInput.get(), BibleEditor.ID, true);
		} catch (PartInitException e1) {
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to open Bible chapter", e1));
		}
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		books.setFocus();
	}
}