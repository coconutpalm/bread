package com.coconut_palm_software.bread.lib;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.coconut_palm_software.bread.ui.ReferenceParser;

import junit.framework.TestCase;

public class NetBibleReferenceTest extends TestCase {
	private static final String VERSE1 = "1Tim 6:2";
	private static final String EDITOR_COMMENT1 = "Summary of Timothy’s Duties";
	private static final String VERSE2 = "3John 1:4";
	private static final String EDITOR_COMMENT2 = "The Charge to Gaius";
	private static final String VERSE3 = "Heb 5:10";
	private static final String EDITOR_COMMENT3 = "The Need to Move on to Maturity";
	
	@Override
	protected void setUp() throws Exception {
		// NOTE: Change this to wherever you've go this unpacked on your system
		NetBibleReference.basePath = "/home/djo/bin/bread8/linux.gtk.x86/bread/netbible/";
	}

	public void testReference_stripsEditorcomments1() throws Exception {
		assertVerseDoesNotContain(VERSE1, EDITOR_COMMENT1);
	}

	public void testReference_stripsEditorcomments2() throws Exception {
		assertVerseDoesNotContain(VERSE2, EDITOR_COMMENT2);
	}

	public void testReference_stripsEditorcomments3() throws Exception {
		assertVerseDoesNotContain(VERSE3, EDITOR_COMMENT3);
	}

	private void assertVerseDoesNotContain(String verse, String comment) throws FileNotFoundException,
			IOException {
		IBibleReference testee = parseOrThrowException(verse);
		
		String result = testee.getText();
		
		assertFalse(verse, result.contains(comment));
	}

	private NetBibleReference parseOrThrowException(String verse) {
		ReferenceParser parser = new ReferenceParser(verse);
		NetBibleReference testee = (NetBibleReference) parser.reference
			.getOrThrow(new IllegalArgumentException("Unable to parse verse: " + verse));
		return testee;
	}
	
	//----------------------------------------------------------------------------------------
	
	public void testReference_garbageBook() throws Exception {
		try {
			parseOrThrowException("moo 5:8");
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			//success
		}
	}

	public void testReference_parsesBook() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
	}

	public void testReference_parsesBookWithChapter() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar 1");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
		assertEquals(1, testee.getChapter());
	}

	public void testReference_parsesBookWithHighChapterNumber() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar 16");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
		assertEquals(16, testee.getChapter());
	}
	
	public void testReference_rangeChecksChapterNumber_Low() throws Exception {
		try {
			parseOrThrowException("mar 0");
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			//success
		}
	}

	public void testReference_rangeChecksChapterNumber_High() throws Exception {
		try {
			parseOrThrowException("mar 17");
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			//success
		}
	}

	public void testReference_parsesBookWithChapterAndTrailingColon() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar 1:");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
		assertEquals(1, testee.getChapter());
	}
	
	public void testReference_parsesBookWithChapterAndVerse() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar 1:5");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
		assertEquals(1, testee.getChapter());
		assertEquals(5, testee.getVerse());
	}
	
	public void testReference_parsesBookWithChapterAndVerse_rangeChecksVerse_high_success() throws Exception {
		NetBibleReference testee = parseOrThrowException("mar 1:45");
		
		assertTrue(Bible.books[testee.getBook()].name.equals("Mark"));
		assertEquals(1, testee.getChapter());
		assertEquals(45, testee.getVerse());
	}
	
	public void testReference_parsesBookWithChapterAndVerse_rangeChecksVerse_high_fail() throws Exception {
		try {
			parseOrThrowException("mar 1:46");
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testReference_parsesBookWithChapterAndVerse_rangeChecksVerse_low() throws Exception {
		try {
			parseOrThrowException("mar 1:0");
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testReference_parsesPsa119_113() throws Exception {
		NetBibleReference testee = parseOrThrowException("psa 119:113");
		String firstLineText = testee.getFirstLineText();
		assertEquals("I hate", firstLineText.substring(0, 6));
	}
	
	//----------------------------------------------------------------------------------------

	public void testReference_parsesVerseFromSubsequentFile() throws Exception {
		IBibleReference testee = parseOrThrowException("1Jo 1:7");
		assertTrue(testee.getText().contains("But if we walk in the light"));
	}

	//----------------------------------------------------------------------------------------

	public void testReference_removesHebrewLettersFromAcrosticChapters() throws Exception {
		IBibleReference testee = parseOrThrowException("Ps 119:8");
		assertFalse(testee.getText().contains("Bet"));
	}
}

