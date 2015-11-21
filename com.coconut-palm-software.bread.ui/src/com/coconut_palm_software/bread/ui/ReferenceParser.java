package com.coconut_palm_software.bread.ui;

import com.coconut_palm_software.bread.lib.Bible;
import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.optionmonad.Option;

import static com.coconut_palm_software.bread.optionmonad.None.none;
import static com.coconut_palm_software.bread.optionmonad.Some.some;

public class ReferenceParser {

	public final String referenceString;
	public final Option<IBibleReference> reference;
	public Option<String> bookName = none();
	public Option<Integer> chapter = none();
	public Option<Integer> verse = none();

	public ReferenceParser(String referenceString) {
		this.referenceString = referenceString;
		this.reference = parse(referenceString);
	}
	
	public String getReferenceString() {
		return referenceString;
	}

    private Option<IBibleReference> parse(String reference) {
    	reference = reference.trim();
    	int spacePos = reference.lastIndexOf(' ');
    	int colonPos = reference.indexOf(':');

    	// No space or colon
    	if (spacePos < 0 && colonPos < 0) {
    		int book = Bible.ref2int(reference);
    		if (checkBookRange(book)) {
    			this.bookName = some(Bible.books[book].name.substring(0, reference.length()));
    			return some((IBibleReference)new NetBibleReference(book, 1, 1));
    		} else {
    			return none();
    		}
    	}
    	// There is at least one space, not no colon
    	if (colonPos < 0) {
    		String bookName = reference.substring(0, spacePos);
    		int chapter;
    		try {
    			chapter = Integer.parseInt(reference.substring(spacePos).trim());
    		} catch (NumberFormatException e) {
    			return none();
    		}
    		int book = Bible.ref2int(bookName);
    		if (checkBookRange(book) && checkChapterRange(book, chapter)) {
    			this.bookName = some(Bible.books[book].name.substring(0, bookName.length()));
    			this.chapter = some(chapter);
    			return some((IBibleReference)new NetBibleReference(book, chapter, 1));
    		} else {
    			return none();
    		}
    	}
    	// A colon, but no space
    	if (spacePos < 0) {
    		return none();
    	}
    	// Both a space and a colon
    	String bookName = reference.substring(0, spacePos);
		int book = Bible.ref2int(bookName);
		if (book < 0) {
			return none();
		}
		int chapter;
		try {
			chapter = Integer.parseInt(reference.substring(spacePos+1, colonPos).trim());
		} catch (NumberFormatException e) {
			return none();
		}
		int verse;
		try {
			verse = Integer.parseInt(reference.substring(colonPos+1).trim());
		} catch (NumberFormatException e) {
			if (checkBookRange(book) && checkChapterRange(book, chapter)) {
				this.bookName = some(Bible.books[book].name.substring(0, bookName.length()));
				this.chapter = some(chapter);
				return some((IBibleReference)new NetBibleReference(book, chapter, 1));
			} else {
				return none();
			}
		}
		if (checkBookRange(book) && checkChapterRange(book, chapter) && checkVerseRange(book, chapter, verse)) {
			this.bookName = some(Bible.books[book].name.substring(0, bookName.length()));
			this.chapter = some(chapter);
			this.verse = some(verse);
			return some((IBibleReference)new NetBibleReference(book, chapter, verse));
		} else {
			return none();
		}
    }

	private static boolean checkVerseRange(int book, int chapter, int verse) {
		if (verse < 1) {
			return false;
		}
		NetBibleReference reference = new NetBibleReference(book, chapter, 1);
		if (verse > reference.getNumberOfVersesInChapter()) {
			return false;
		}
		return true;
	}

	private static boolean checkChapterRange(int book, int chapter) {
		return chapter > 0 && chapter <= Bible.books[book].numChaps;
	}

	private static boolean checkBookRange(int book) {
		return book >= 0;
	}

}
