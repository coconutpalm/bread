package com.coconut_palm_software.bread.lib;

import static com.coconut_palm_software.bread.optionmonad.None.none;
import static com.coconut_palm_software.bread.optionmonad.Some.some;

import java.util.LinkedList;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import com.coconut_palm_software.bread.optionmonad.Option;
import com.coconut_palm_software.bread.ui.BibleEditorInput;
import com.coconut_palm_software.bread.ui.NetBibleEditorInput;
import com.coconut_palm_software.bread.ui.ReferenceParser;

public class SearchEngine extends Realm {
	ReferenceParser parser = new ReferenceParser("");

	private WritableList bookList = new WritableList(this, new LinkedList<Book>(), Book.class);
	private WritableList chapterList = new WritableList(this, new LinkedList<IBibleReference>(), IBibleReference.class);
	private WritableList verseList = new WritableList(this, new LinkedList<IBibleReference>(), IBibleReference.class);
	private WritableList extraList = new WritableList(this, new LinkedList<Object>(), Object.class);

	BackgroundWorker bookRefresher = new BackgroundWorker();
	BackgroundWorker chapterRefresher = new BackgroundWorker();
	BackgroundWorker verseRefresher = new BackgroundWorker();
	BackgroundWorker extraRefresher = new BackgroundWorker();
	
	BackgroundWorkQueue readQueue = new BackgroundWorkQueue();
	
	public synchronized void updateSearch(String text) {
		parser = new ReferenceParser(text);
		reloadBookList();
		reloadChapterListIfSearchChanged();
		reloadVerseListIfSearchChanged();
	}

	int lastChapterSearchResult = -1;
	private void reloadChapterListIfSearchChanged() {
		int newChapter = parser.chapter.getOrReturn(-1);
		if (newChapter != lastChapterSearchResult) {
			lastChapterSearchResult = newChapter;
			reloadChapterList(0);
		}
	}
	
	int lastVerseSearchResult = -1;
	private void reloadVerseListIfSearchChanged() {
		int newVerse = parser.verse.getOrReturn(-1);
		if (newVerse != lastVerseSearchResult) {
			lastVerseSearchResult = newVerse;
			reloadVerseList(0);
		}
	}
	
	public synchronized void bookSelected(int selection) {
		reloadChapterList(selection);
	}
	
	public synchronized void chapterSelected(int selection) {
		reloadVerseList(selection);
	}
	
	public IObservableList getBookList() {
		return bookList;
	}
	
	public IObservableList getChapterList() {
		return chapterList;
	}
	
	public IObservableList getVerseList() {
		return verseList;
	}
	
	public IObservableList getExtraList() {
		return extraList;
	}

	private Book lastBook = null;
	
	private void reloadBookList() {
		bookRefresher.submit(new Runnable() {
			@Override
			public void run() {
				String prefix = parser.bookName.getOrReturn("");
				bookList.clear();
				boolean gotFirst = false;
				for (int bookNum = 0; bookNum < Bible.books.length; bookNum++) {
					Book book = Bible.books[bookNum];
					if (book.name.startsWith(prefix)) {
						bookList.add(book);
						if (!gotFirst) {
							reloadChapterListIfFilterChanged();
							gotFirst = true;
						}
					}
				}
			}

			private void reloadChapterListIfFilterChanged() {
				Book currentBook = (Book) bookList.get(0);
				if (!currentBook.equals(lastBook))
				{
					lastBook = currentBook;
					reloadChapterList(0);
				}
			}});
	}
	
	private IBibleReference lastChapter = null;

	protected void reloadChapterList(final int selection) {
		chapterRefresher.submit(new Runnable() {
			@Override
			public void run() {
				Book book = (Book) bookList.get(selection);
				int bookNum = Bible.ref2int(book.name);
				
				String prefix = "";
				if (parser.chapter.hasValue()) {
					prefix = parser.chapter.get().toString();
				}
				chapterList.clear();
				boolean gotFirst = false;
				for (Integer chapter = 1; chapter <= book.numChaps; ++chapter) {
					if (!chapter.toString().startsWith(prefix)) {
						continue;
					}
					chapterList.add(new NetBibleReference(bookNum, chapter, 1));
					if (!gotFirst) {
						reloadVerseListIfFilterChanged();
						gotFirst = true;
					}
				}
			}

			private void reloadVerseListIfFilterChanged() {
				IBibleReference newChapter = (IBibleReference) chapterList.get(0);
				if (lastChapter == null
						|| newChapter.getBook() != lastChapter.getBook() 
						|| newChapter.getChapter() != lastChapter.getChapter())
				{
					lastChapter = newChapter;
					reloadVerseList(0);
				}
			}});
	}
	
	protected void reloadVerseList(final int selection) {
		verseRefresher.submit(new Runnable() {
			@Override
			public void run() {
				IBibleReference ref = (IBibleReference) chapterList.get(selection);
				
				String prefix = "";
				if (parser.verse.hasValue()) {
					prefix = parser.verse.get().toString();
				}
				verseList.clear();
				for (Integer verse = 1; verse <= ref.getNumberOfVersesInChapter(); ++verse) {
					if (!verse.toString().startsWith(prefix)) {
						continue;
					}
					verseList.add(new NetBibleReference(ref.getBook(), ref.getChapter(), verse));
				}
			}});
	}
	
	public synchronized Option<BibleEditorInput> getCompleteSearchResults() {
		Option<IBibleReference> ref = parser.reference;
		if (ref.hasValue()) {
			BibleEditorInput result = new NetBibleEditorInput(ref.get());
			return some(result);
		}
		return none();
	}
	
	public synchronized Option<BibleEditorInput> getBookSearchResults(final int selection) {
		class GetSearchResults implements Runnable {
			Option<BibleEditorInput> searchResults = none();
			
			@Override
			public void run() {
				if (selection < 0 || selection >= bookList.size()) {
					searchResults = none();
					return;
				}
				Book book = (Book) bookList.get(selection);
				int bookNum = Bible.ref2int(book.name);
				IBibleReference ref = new NetBibleReference(bookNum, 1, 0);
				BibleEditorInput result = new NetBibleEditorInput(ref);
				searchResults = some(result);
			}
		}
		GetSearchResults resultsGetter = new GetSearchResults();
		syncExec(resultsGetter);
		return resultsGetter.searchResults;
	}
	
	public synchronized Option<BibleEditorInput> getChapterSearchResults(final int selection) {
		class GetSearchResults implements Runnable {
			Option<BibleEditorInput> searchResults = none();

			@Override
			public void run() {
				if (selection < 0 || selection >= chapterList.size()) {
					searchResults = none();
					return;
				}
				IBibleReference chapter = (IBibleReference) chapterList.get(selection);
				BibleEditorInput result = new NetBibleEditorInput(chapter);
				searchResults = some(result);
			}
		}
		GetSearchResults resultsGetter = new GetSearchResults();
		syncExec(resultsGetter);
		return resultsGetter.searchResults;
	}
	
	public synchronized Option<BibleEditorInput> getVerseSearchResults(final int selection) {
		class GetSearchResults implements Runnable {
			Option<BibleEditorInput> searchResults = none();

			@Override
			public void run() {
				if (selection < 0 || selection >= verseList.size()) {
					searchResults = none();
					return;
				}
				IBibleReference verse = (IBibleReference) verseList.get(selection);
				BibleEditorInput result = new NetBibleEditorInput(verse);
				searchResults = some(result);
			}
		}
		GetSearchResults resultsGetter = new GetSearchResults();
		syncExec(resultsGetter);
		return resultsGetter.searchResults;
	}
	
	public synchronized Option<BibleEditorInput> getExtraSearchResults(int selection) {
		return none();
	}

	@Override
	public boolean isCurrent() {
		return readQueue.isInWorkerThread()
			|| bookRefresher.isInWorkerThread()
			|| chapterRefresher.isInWorkerThread()
			|| verseRefresher.isInWorkerThread()
			|| extraRefresher.isInWorkerThread();
	}
	
	@Override
	public void asyncExec(final Runnable runnable) {
		readQueue.submit(runnable);
	}

}
