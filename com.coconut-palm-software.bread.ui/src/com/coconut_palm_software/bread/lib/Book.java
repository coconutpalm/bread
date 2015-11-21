package com.coconut_palm_software.bread.lib;

/**
 * Class Book.  A listing of all books in the (Protestant) BibleModule
 * 
 * @author djo
 */
public class Book {

	public Book(String name, String abbrev, String fileRoot, int chapOffset, int numChaps) {
		this.name = name;                   // Name of the book
		this.abbrev = abbrev;               // Short name abbreviation (used in HTML anchors)
		this.fileRoot = fileRoot;			// Abbreviated (NET Bible) name (used for file names)
		this.chapOffset = chapOffset;       // Offset of the first chapter of this book in the Chapters file
		this.numChaps = numChaps;
	}
	
	public final String name;
	public final String fileRoot;
	public final int numChaps;
	public String abbrev;			// Computed at runtime
	public int chapOffset;			// Computed at runtime
    
    public boolean isAbbrev(String abbrev) {
    	if (abbrev.equalsIgnoreCase(this.abbrev)) {
    		return true;
    	}
        if (name.toUpperCase().startsWith(abbrev.toUpperCase())) { 
            return true;
        }
        return false;
    }
	
}
