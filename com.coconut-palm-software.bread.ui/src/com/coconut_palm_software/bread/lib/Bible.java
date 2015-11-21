package com.coconut_palm_software.bread.lib;


/**
 * Class Bible.  Stores data about the books of the Bible.
 * 
 * @author djo
 */
public class Bible {
    /**
     * Method ref2int.  Converts a string book name abbreviation to an integer suitable for indexing the Books array
     * 
     * @param abbrev The abbreviation (any prefix string will work) for the book name
     * @return The integer corresponding to the book's position in the Protestant canon (0-based)
     */
    public static int ref2int(String abbrev) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].isAbbrev(abbrev)) return i;
        }
        return -1;
    }
    
    public static Book[] books = new Book[] {
        new Book("Genesis", "Ge", "gen", 0, 50),
        new Book("Exodus", "Ex", "exo", 0, 40),
        new Book("Leviticus", "Le", "lev", 0, 27),
        new Book("Numbers", "Nu", "num", 0, 36),
        new Book("Deuteronomy", "De", "deu", 0, 34),
        new Book("Joshua", "Jos", "jos", 0, 24),
        new Book("Judges", "Jdg", "jdg", 0, 21),
        new Book("Ruth", "Ru", "rut", 0, 4),
        new Book("1Samuel", "1Sa", "1sa", 0, 31),
        new Book("2Samuel", "2Sa", "2sa", 0, 24),
        new Book("1Kings", "1Ki", "1ki", 0, 22),
        new Book("2Kings", "2Ki", "2ki", 0, 25),
        new Book("1Chronicles", "1Ch", "1ch", 0, 29),
        new Book("2Chronicles", "2Ch", "2ch", 0, 36),
        new Book("Ezra", "Ezr", "ezr", 0, 10),
        new Book("Nehemiah", "Ne", "neh", 0, 13),
        new Book("Esther", "Es", "est", 0, 10),
        new Book("Job", "Job", "job", 0, 42),
        new Book("Psalms", "Ps", "psa", 0, 150),
        new Book("Proverbs", "Pr", "pro", 0, 31),
        new Book("Ecclesiastes", "Ec", "ecc", 0, 12),
        new Book("Song of Solomon", "So", "sos", 0, 8),
        new Book("Isaiah", "Is", "isa", 0, 66),
        new Book("Jeremiah", "Je", "jer", 0, 52),
        new Book("Lamentations", "La", "lam", 0, 5),
        new Book("Ezekiel", "Eze", "eze", 0, 48),
        new Book("Daniel", "Da", "dan", 0, 12),
        new Book("Hosea", "Ho", "hos", 0, 14),
        new Book("Joel", "Joe", "joe", 0, 3),
        new Book("Amos", "Am", "amo", 0, 9),
        new Book("Obadiah", "Ob", "oba", 0, 1),
        new Book("Jonah", "Jon", "jon", 0, 4),
        new Book("Micah", "Mic", "mic", 0, 7),
        new Book("Nahum", "Na", "nah", 0, 3),
        new Book("Habakkuk", "Hab", "hab", 0, 3),
        new Book("Zephaniah", "Zep", "zep", 0, 3),
        new Book("Haggai", "Hag", "hag", 0, 2),
        new Book("Zechariah", "Zec", "zec", 0, 14),
        new Book("Malachi", "Mal", "mal", 0, 4),
        new Book("Matthew", "Mt", "mat", 0, 28),
        new Book("Mark", "Mk", "mar", 0, 16),
        new Book("Luke", "Lk", "luk", 0, 24),
        new Book("John", "Jn", "joh", 0, 21),
        new Book("Acts", "Ac", "act", 0, 28),
        new Book("Romans", "Ro", "rom", 0, 16),
        new Book("1Corinthians", "1Co", "1co", 0, 16),
        new Book("2Corinthians", "2Co", "2co", 0, 13),
        new Book("Galatians", "Ga", "gal", 0, 6),
        new Book("Ephesians", "Eph", "eph", 0, 6),
        new Book("Philippians", "Php", "phi", 0, 4),
        new Book("Colossians", "Col", "col", 0, 4),
        new Book("1Thessalonians", "1Th", "1th", 0, 5),
        new Book("2Thessalonians", "2Th", "2th", 0, 3),
        new Book("1Timothy", "1Ti", "1ti", 0, 6),
        new Book("2Timothy", "2Ti", "2ti", 0, 4),
        new Book("Titus", "Tit", "tit", 0, 3),
        new Book("Philemon", "Phm", "phm", 0, 1),
        new Book("Hebrews", "Heb", "heb", 0, 13),
        new Book("James", "Jam", "jam", 0, 5),
        new Book("1Peter", "1Pe", "1pe", 0, 5),
        new Book("2Peter", "2Pe", "2pe", 0, 3),
        new Book("1John", "1Jn", "1jo", 0, 5),
        new Book("2John", "2Jn", "2jo", 0, 1),
        new Book("3John", "3Jn", "3jo", 0, 1),
        new Book("Jude", "Jud", "jud", 0, 1),
        new Book("Revelation", "Rev", "rev", 0, 22),
    };
    
    
    // Initialize the chapOffset and abbrev fields
    static {
        for (int i=1; i < books.length; ++i) {
            books[i].chapOffset = books[i-1].chapOffset + books[i-1].numChaps;
        }
// **Save in case we ever need this code again**
//        String fileRoot = "/" + Reference.getBasePath();
//        try {
//	        for (int bookNum=0; bookNum < books.length; ++bookNum) {
//	        	String chapterHtmlFile = fileRoot + Reference.getChapterHtmlFile(new Reference(bookNum, 1, 1));
//				BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(chapterHtmlFile)));
//				try {
//		        	String line = null;
//					while ((line = is.readLine()) != null) {
//						String anchorTag = "<a name=\"";
//						int positionOfAnchorTag = line.indexOf(anchorTag);
//						if (positionOfAnchorTag >= 0) {
//							String beginningOfAbbrev = line.substring(positionOfAnchorTag + anchorTag.length());
//							int endOfAbbrev = beginningOfAbbrev.indexOf(' ');
//							if (endOfAbbrev < 0) {
//								throw new IllegalStateException("Unable to parse abbreviation");
//							}
//							books[bookNum].abbrev = beginningOfAbbrev.substring(0, endOfAbbrev);
//							break;
//						}
//					}
//				} finally {
//					is.close();
//				}
//	        }
//        } catch (IOException e) {
//			Activator.getDefault().getLog().log(
//					new Status(Status.ERROR, Activator.PLUGIN_ID,
//							"Unable to compute book abbreviations", e));
//        }
    }
}
