package com.coconut_palm_software.bread.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import com.coconut_palm_software.bread.optionmonad.Option;

public interface IBibleReference extends Serializable {

	int getBook();
	
	int getChapter();
	
	int getVerse();
	
	int getNumberOfVersesInChapter();

	Option<IBibleReference> prevBookReference();

	Option<IBibleReference> nextBookReference();

	Option<IBibleReference> prevChapterReference();

	Option<IBibleReference> nextChapterReference();

	Option<IBibleReference> prevVerseReference();

	/**
	 * Note: This may be EXPENSIVE!
	 */
	Option<IBibleReference> nextVerseReference();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	String toString();

	/**
	 * Returns this reference as <a name="reference"/> anchor
	 * @return The HTML anchor string
	 */
	String toHTMLAnchor();

	/**
	 * @return the reference as a URL-encoded #reference
	 */
	String toURLAnchor();

	/**
	 * @return the contents of the reference as formatted text.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	String getText() throws FileNotFoundException,
			IOException;
	
	/**
	 * @return The first line of the verse
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	String getFirstLineText() throws FileNotFoundException, IOException;

	/**
	 * @return the contents of the reference as a fragment of HTML-formatted text.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	String getHTML() throws FileNotFoundException,
			IOException;

}