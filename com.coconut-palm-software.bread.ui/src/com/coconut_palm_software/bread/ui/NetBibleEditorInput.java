package com.coconut_palm_software.bread.ui;

import static com.coconut_palm_software.bread.optionmonad.Some.some;
import static com.coconut_palm_software.bread.optionmonad.None.none;

import org.eclipse.swt.browser.LocationEvent;

import com.coconut_palm_software.bread.lib.Bible;
import com.coconut_palm_software.bread.lib.Book;
import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.optionmonad.Option;

public class NetBibleEditorInput extends BibleEditorInput {

	public NetBibleReference reference;
	
	public NetBibleEditorInput(NetBibleReference reference) {
		this.reference = reference;
	}

	public NetBibleEditorInput(IBibleReference ref) {
		this.reference = new NetBibleReference(ref.getBook(), ref.getChapter(), ref.getVerse());
	}

	@Override
	public String getName() {
		return Bible.books[reference.getBook()].name + " " + Integer.toString(reference.getChapter());
	}

	@Override
	String getHTML() {
		final String basePath = NetBibleReference.getBasePath();
		
		final String htmlStart = "<HTML>\n" + 
		"\n" + 
		"<FRAMESET ROWS=\"*,75\">\n" + 
		"\n" + 
		"   <FRAME SRC=\""+ basePath;
		
		final String htmlEnd = "\" NAME=\"text_pane\" MARGINWIDTH=\"10\" MARGINHEIGHT=\"20\" SCROLLING=\"auto\" frameborder=\"1\">\n" + 
		"\n" + 
		"   <FRAME SRC=\"" + basePath + "noteintr.htm\" NAME=\"note_pane\" MARGINWIDTH=\"10\" MARGINHEIGHT=\"5\" SCROLLING=\"auto\" frameborder=\"1\">\n" + 
		"\n" + 
		"</FRAMESET>\n" + 
		"\n" + 
		"</HTML>\n" + 
		"";

		String chapterHtmlFile = reference.getChapterHtmlFile();
		String verseAnchor = reference.getVerse() <= 1 ? "" : reference.toURLAnchor();
		String htmlDocument = htmlStart + chapterHtmlFile + verseAnchor + htmlEnd;
		return htmlDocument;
	}

	@Override
	void locationChanging(LocationEvent event) {
		// If it's a reference to a note, we're done
		if (event.location.contains("_notes.htm")) return;

		// If we processed a reference link, we're done
		processReferenceLink(event);
		if (!event.doit) return;
		
		// Update the current reference reference from the URL we're opening
		Option<IBibleReference> nextChapter = parseChapterReference(event.location);
		if (nextChapter.hasValue()) {
			reference = (NetBibleReference) nextChapter.get();
		}
	}

	Option<IBibleReference> parseChapterReference(String location) {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null");
		}
		if (!location.contains(".htm")) {
			throw new IllegalArgumentException("Must be a .htm file");
		}
		location = location.replaceFirst("\\.htm.*$", "");
		location = removePathPartsFrom(location);
		Option<BookParseResult> bookParseResultOption = parseBookFrom(location);
		if (!bookParseResultOption.hasValue()) {
			return none();
		}
		BookParseResult bookParseResult = bookParseResultOption.get();
		int chapter = computeChapterNumber(bookParseResult);
		NetBibleReference result = new NetBibleReference(bookParseResult.bookNumber, chapter, 1);
		return some((IBibleReference)result);
	}

	private int computeChapterNumber(BookParseResult bookParseResult) {
		String unparsedString = bookParseResult.unparsedString;
		int chapter = "".equals(unparsedString) ? 1 : Integer.parseInt(unparsedString);
		return chapter;
	}

	static class BookParseResult {
		public int bookNumber;
		public String unparsedString;
	}
	
	BookParseResult bookParseResult(int bookNumber, String unparsedString) {
		BookParseResult result = new BookParseResult();
		result.bookNumber = bookNumber;
		result.unparsedString = unparsedString;
		return result;
	}
	
	Option<BookParseResult> parseBookFrom(String location) {
		int position=0;
		for (Book book : Bible.books) {
			if (location.startsWith(book.name)) {
				return some(bookParseResult(position, stripBookName(location)));
			}
			if (location.startsWith(book.fileRoot)) {
				return some(bookParseResult(position, stripBookName(location)));
			}
			if (location.startsWith(book.abbrev)) {
				return some(bookParseResult(position, stripBookName(location)));
			}
			++position;
		}
		return none();
	}

	String stripBookName(String chapterReference) {
		if ("".equals(chapterReference) || null == chapterReference) {
			throw new IllegalArgumentException("Empty string not allowed!");
		}
		
		boolean foundDigit = false;
		int digitPosition=0;
		int length = chapterReference.length();
		while (!foundDigit && digitPosition < length) {
			if (Character.isDigit(chapterReference.charAt(digitPosition))) {
				return chapterReference.substring(digitPosition);
			}
			++digitPosition;
		}
		return "";
	}

	String removePathPartsFrom(String location) {
		return location.replaceFirst("^.*/", "");
	}

}
