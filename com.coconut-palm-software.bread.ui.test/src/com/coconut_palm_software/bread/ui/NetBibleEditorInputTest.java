package com.coconut_palm_software.bread.ui;

import junit.framework.TestCase;

import com.coconut_palm_software.bread.lib.Bible;
import com.coconut_palm_software.bread.lib.Book;
import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.NetBibleReference;
import com.coconut_palm_software.bread.optionmonad.Option;
import com.coconut_palm_software.bread.ui.NetBibleEditorInput.BookParseResult;

public class NetBibleEditorInputTest extends TestCase {
	private NetBibleEditorInput testee;
	
	@Override
	protected void setUp() throws Exception {
		testee = new NetBibleEditorInput(new NetBibleReference());
	}
	
	/*
	 * Examples:
	 * file:///home/djo/bin/eclipse/3.5.2//netbible/gen5.htm
	 * file:///home/djo/bin/eclipse/3.5.2//netbible/1th5.htm
	 * file:///home/djo/bin/eclipse/3.5.2//netbible/gen3.htm#Ge%203:13
	 */
	
	public void testParseChapterReference_nullInputThrowsIllegalArgumentException() {
		try {
			testee.parseChapterReference(null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testParseChapterReference_nodotHTMExtension_throwsIllegalArgumentException() throws Exception {
		try {
			testee.parseChapterReference("/a/path/to/nowhere/Gen2");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			//success
		}
	}

	public void testParseChapterReference_nodotHTMExtensionButIncludesAnchor_throwsIllegalArgumentException() throws Exception {
		try {
			testee.parseChapterReference("/a/path/to/nowhere/Gen2#v1");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testParseChapterReference_validChapterReference_returnsSomeIBibleReference() throws Exception {
		Option<IBibleReference> result = testee.parseChapterReference("file://in/the/beginning/Gen2.htm#GodCreated");
		assertTrue(result.hasValue());
		assertEquals(new NetBibleReference("Genesis", 2, 1), result.get());
	}
	
	public void testParseChapterReference_invalidChapterReference_returnsNone() throws Exception {
		Option<IBibleReference> result = testee.parseChapterReference("file://needle/in/a/haystack2.htm#notfound");
		assertFalse(result.hasValue());
	}
	
	public void testParseBookFromLocation_abbreviationReturnsSomeBook() throws Exception {
		Option<BookParseResult> result = testee.parseBookFrom("Ge1");
		assertTrue(result.hasValue());
		assertEquals("Found book", 0, result.get().bookNumber);
		assertEquals("1", result.get().unparsedString);
	}
	
	public void testParseBookFromLocation_secondAbbreviationReturnsSomeBook() throws Exception {
		Option<BookParseResult> result = testee.parseBookFrom("Ex1");
		assertTrue(result.hasValue());
		assertEquals("Found book", 1, result.get().bookNumber);
		assertEquals("1", result.get().unparsedString);
	}
	
	public void testParseBookFromLocation_fileRoot_ReturnsSomeBook() throws Exception {
		Option<BookParseResult> result = testee.parseBookFrom("Gen1");
		assertTrue(result.hasValue());
		assertEquals("Found book", 0, result.get().bookNumber);
		assertEquals("1", result.get().unparsedString);
	}
	
	public void testParseBookFromLocation_bookName_ReturnsSomeBook() throws Exception {
		Option<BookParseResult> result = testee.parseBookFrom("Genesis1");
		assertTrue(result.hasValue());
		assertEquals("Found book", 0, result.get().bookNumber);
		assertEquals("1", result.get().unparsedString);
	}
	
	public void testParseBookFromLocation_invalidName_ReturnsNothing() throws Exception {
		Option<BookParseResult> result = testee.parseBookFrom("nowhere");
		assertFalse(result.hasValue());
	}
	
	public void testStripBookName_emptyStringThrowsIArgExcp() throws Exception {
		try {
			testee.stripBookName("");
			fail("Should have thrown IllegalArgExc");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testStripBookName_nullStringThrowsIArgExcp() throws Exception {
		try {
			testee.stripBookName(null);
			fail("Should have thrown IllegalArgExc");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	public void testStripBookName_onlyChapterNumber_isIdentityFunction() throws Exception {
		String expected = "777";
		String result = testee.stripBookName(expected);
		assertEquals(expected, result);
	}
	
	public void testStripBookName_stripsOneCharacter() throws Exception {
		String result = testee.stripBookName("g7");
		assertEquals("7", result);
	}
	
	public void testStripBookName_stripsMultipleCharacters() throws Exception {
		String result = testee.stripBookName("Gen77");
		assertEquals("77", result);
	}
	
	public void testStripBookName_noChapterDigits_returnsEmptyString() throws Exception {
		String result = testee.stripBookName("Genesis");
		assertEquals("", result);
	}
	
	public void testRemovePathPartsFrom_noPath_isIdentityFunction() throws Exception {
		final String input = "foo";
		String result = testee.removePathPartsFrom(input);
		assertEquals(input, result);
	}
	
	public void testRemovePathPartsFrom_singleSegment_success() throws Exception {
		String result = testee.removePathPartsFrom("/bonk");
		assertEquals("bonk", result);
	}
	
	public void testRemovePathPartsFrom_multipleSegments_success() throws Exception {
		String result = testee.removePathPartsFrom("/bonk/bang/ouch");
		assertEquals("ouch", result);
	}
}
