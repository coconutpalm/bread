package com.coconut_palm_software.bread.lib;

import static com.coconut_palm_software.bread.optionmonad.None.none;
import static com.coconut_palm_software.bread.optionmonad.Some.some;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Platform;

import com.coconut_palm_software.bread.optionmonad.Option;

/**
 * Class Reference.  Contains book/chapter/verse data and methods to retrieve
 * next/previous Reference and the referent of the reference. :D
 * 
 * @author djo
 */
public class NetBibleReference implements IBibleReference {
	private static final long serialVersionUID = -4199074073212390721L;
	private int book;
	private int chapter;
	private int verse;
    
    public int getBook() {
		return book;
	}

	public int getChapter() {
		return chapter;
	}

	public int getVerse() {
		return verse;
	}

    public NetBibleReference(int book, int chapter, int verse) {
        this.book = book;
        if (this.book == -1)
            throw new RuntimeException("Invalid book");
        this.chapter = chapter;
        this.verse = verse;
    }

    public NetBibleReference(String book, int chapter, int verse) throws InvalidReferenceException {
        this.book = Bible.ref2int(book);
        if (this.book == -1)
            throw new InvalidReferenceException("Invalid book");
        this.chapter = chapter;
        this.verse = verse;
    }
    
    public NetBibleReference(IBibleReference toCopy) {
    	this.book = toCopy.getBook();
    	this.chapter = toCopy.getChapter();
    	this.verse = toCopy.getVerse();
    }
    
    public NetBibleReference() {
    	this(0, 1, 1);
	}
    
    public static NetBibleReference randomChapter() {
    	int book = (int)(Math.random() * Bible.books.length);
    	int chapter = (int)(Math.random() * Bible.books[book].numChaps) + 1;
    	return new NetBibleReference(book, chapter, 1);
    }
    
    public static IBibleReference randomVerse() throws IOException {
    	NetBibleReference randomVerse = randomChapter();
    	int numberOfVersesInChapter = randomVerse.getNumberOfVersesInChapter();
    	int verse = (int)(Math.random() * numberOfVersesInChapter) + 1;
    	randomVerse.verse = verse;
		return randomVerse;
    }
    
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#getNumberOfVersesInChapter()
	 */
	public int getNumberOfVersesInChapter() {
		File chapterFile = getFullyQualifiedChapterPathAndFile();
		try {
			if (notLastChapterInBook()) {
				NetBibleReference nextChapterRef = (NetBibleReference) nextChapterReference()
						.getOrThrow(
								new IllegalStateException(
										"Not last chapter in book, but cannot get next chapter?"));
				File nextChapterFile = nextChapterRef
						.getFullyQualifiedChapterPathAndFile();
				return countVersesInChapterFile(chapterFile)
						+ countVersesInChapterFile(nextChapterFile);
			} else {
				return countVersesInChapterFile(chapterFile);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#prevBookReference()
	 */
	public Option<IBibleReference> prevBookReference() {
		if (book > 0) {
			return some((IBibleReference)new NetBibleReference(book-1, 1, 1));
		}
		return none();
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#nextBookReference()
	 */
	public Option<IBibleReference> nextBookReference() {
		if (notLastBookInBible()) {
			return some((IBibleReference)new NetBibleReference(book+1, 1, 1));
		}
		return none();
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#prevChapterReference()
	 */
	public Option<IBibleReference> prevChapterReference() {
		if (chapter > 1) {
			return some((IBibleReference)new NetBibleReference(book, chapter-1, 1));
		} else if (book > 0) {
			int previousBook = book-1;
			return some((IBibleReference)new NetBibleReference(previousBook, Bible.books[previousBook].numChaps, 1));
		}
		return none();
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#nextChapterReference()
	 */
	public Option<IBibleReference> nextChapterReference() {
		if (notLastChapterInBook()) {
			IBibleReference nextChapterRef = new NetBibleReference(book, chapter+1, 1);
			return some(nextChapterRef);
		} else {
			if (notLastBookInBible()) {
				return some((IBibleReference)new NetBibleReference(book+1, 1, 1));
			} else {
				return none();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#prevVerseReference()
	 */
	public Option<IBibleReference> prevVerseReference() {
		if (verse > 1) {
			return some((IBibleReference)new NetBibleReference(book, chapter, verse-1));
		} 
		Option<IBibleReference> maybePrevChapter = prevChapterReference();
		if (!maybePrevChapter.hasValue()) {
			// we got None back (ie: 'this' is already Gen 1:1), return the None object
			return maybePrevChapter;
		}
		NetBibleReference result = (NetBibleReference) maybePrevChapter.get();
		result.verse = result.getNumberOfVersesInChapter();
		return some((IBibleReference)result);
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#nextVerseReference()
	 */
	public Option<IBibleReference> nextVerseReference() {
		if (notLastVerseInChapter()) {
			return some((IBibleReference)new NetBibleReference(book, chapter, verse+1));
		} else if (notLastChapterInBook()) {
			return some((IBibleReference)new NetBibleReference(book, chapter+1, 1));
		} else if (notLastBookInBible()) {
			return some((IBibleReference)new NetBibleReference(book+1, 1, 1));
		}
		return none();
	}

	private boolean notLastBookInBible() {
		return book < Bible.books.length;
	}

	private boolean notLastChapterInBook() {
		return chapter < Bible.books[book].numChaps;
	}

	private boolean notLastVerseInChapter() {
		return verse < getNumberOfVersesInChapter();
	}

	private int countVersesInChapterFile(File chapterFile) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(chapterFile)));
		String verseAnchorRegex = computeVerseAnchorRegexUpToChapter();
		int result=0;
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				result += countNumberOfVersesStartingOnLine(verseAnchorRegex, line);
			}
		} finally {
			br.close();
		}
		return result;
	}

	private static int countNumberOfVersesStartingOnLine(
			String verseAnchorRegex, String line) {
		line += " "; // If anchor is at the end, the following line will fail
		return line.split(verseAnchorRegex).length-1;
	}
	
	static String basePath = null;

	public static String getBasePath() {
		if (basePath == null) {
			basePath = Installation.getBasePath() + "/netbible/";
		}
		return basePath;
	}

	public static String getShowContextImagePath() {
		return Platform.getInstallLocation().getURL().toExternalForm().substring(6) + "/images/show_context.gif";
	}

	public String getChapterHtmlFile() {
		return Bible.books[book].fileRoot + Integer.toString(chapter) + ".htm";
	}
	
	private File getFullyQualifiedChapterPathAndFile() {
		return new File("/" + getBasePath() + getChapterHtmlFile());
	}

	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof NetBibleReference) {
            NetBibleReference other = (NetBibleReference)o;
            if (book == other.book && chapter == other.chapter && verse == other.verse)
                return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return book * chapter * verse;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#toString()
	 */
    public String toString() {
        return Bible.books[book].name + " " + chapter + ":" + verse;
    }
    
    /* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#toHTMLAnchor()
	 */
    public String toHTMLAnchor() {
    	return "<a name=\"" + Bible.books[book].abbrev + " " + chapter + ":" + verse + "\"/>";
    }
    
    /* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#toURLAnchor()
	 */
    public String toURLAnchor() {
    	return "#" + Bible.books[book].abbrev + "%20" + chapter + ":" + verse;
    }
    
    /* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#getVerseAsText()
	 */
    public String getText() throws FileNotFoundException, IOException {
    	if (verse < 1) {
    		throw new IllegalArgumentException("No verse specified, only a chapter");
    	}
		File chapterFile = getFullyQualifiedChapterPathAndFile();
		String result = getVerseFromChapterFile(chapterFile);
		if (result != null) {
			return result;
		}
		NetBibleReference nextChapterRef = (NetBibleReference) nextChapterReference().getOrThrow(new IllegalStateException("No chapter after: [" + this + "] ?"));
		File nextChapterFile = nextChapterRef.getFullyQualifiedChapterPathAndFile();
		result = getVerseFromChapterFile(nextChapterFile);
		if (result != null) {
			return result;
		}
		System.out.println(getBasePath());
		throw new IllegalStateException("Unable to read verse for: " + this);
    }

	private String getVerseFromChapterFile(File chapterFile) throws FileNotFoundException, IOException {
		String verseAnchorRegex = computeVerseAnchorRegex();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(chapterFile)));
		String result=null;
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (result == null) {
					// Look for the beginning of the verse
					String[] parts = line.split(verseAnchorRegex);
					if (parts.length > 1) {
						result = parts[1];
						parts = result.split("<a name=");
						if (parts.length > 1) {
							result = parts[0];
							break;
						}
					}
				} else {
					if (line.contains("Next Chapter")) {
						// we hit the end of the chapter; we're done
						break;
					}
					// Look for the start of the next verse
					String[] parts = line.split("<a name=");
					if (parts.length > 1) {
						result += parts[0];
						break;
					} else {
						result += line;
					}
				}
			}
		} finally {
			br.close();
		}
		
		// If we didn't find it, say so.
		if (result == null) {
			return null;
		}
		
		// Semi-intelligently convert HTML to formatted text
		result = result.replaceAll("<p class=\"bodytext\">", "\n"); // An extra newline for paragraph breaks; the rest are poetry breaks
		result = result.replaceAll("<p class=\"paragraphtitle\">.*</p>", "");
		result = result.replaceAll("<p class=\"lamhebrew\">.*</p>", "");
		result = result.replaceAll("</p>", "\n");
		result = result.replaceAll("</P>", "\n");
		result = result.replaceAll("<span class=\"versenum\">[0-9]+:[0-9]+</span>", "");
		result = result.replaceAll("^ +", "");
		result = result.replaceAll("<SUP>[0-9]+</SUP>", "");
		result = result.replaceAll("\\<.*?>","");
		result = HTMLEntities.unhtmlentities(result);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#getFirstLineText()
	 */
	@Override
	public String getFirstLineText() throws FileNotFoundException, IOException {
		String verseText = getText().trim();
		int firstNewline = verseText.indexOf('\n');
		if (firstNewline >= 0) {
			verseText = verseText.substring(0, firstNewline);
		}
		return verseText;
	}

    /* (non-Javadoc)
	 * @see com.coconut_palm_software.bread.lib.IBibleReference#getVerseAsHTML()
	 */
    public String getHTML() throws FileNotFoundException, IOException {
    	// FIXME: for now, we hackishly convert the text back to HTML
    	String result = getText().trim();
    	result = result.trim().replaceAll("\n", "<br/>\n");
    	return result;
    }

	private String computeVerseAnchorRegex() {
		return "<a name=\"" + Bible.books[book].abbrev + "\\ " + chapter + ":" + verse + "\" *> *</a>";
	}

	private String computeVerseAnchorRegexUpToChapter() {
		return "<a name=\"" + Bible.books[book].abbrev + "\\ " + chapter;
	}

}

