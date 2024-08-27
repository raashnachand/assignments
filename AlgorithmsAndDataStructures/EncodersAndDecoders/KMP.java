/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public static int search(String pattern, String text) {
		// construct match table
		int patLength = pattern.length();
		int[] match = new int[patLength];
		match[0] = -1;
		match[1] = 0; 
		int j = 0;
		int pos = 2;
		while (pos < patLength){
			if (pattern.charAt(pos - 1) == pattern.charAt(j)) {
				match[pos] = j+1;
				pos++;
				j++;
			} else if(j>1){
				j = match[j];
			} else {
				match[pos] = 0;
				pos++;
			}
		}
		int a = 0; //start of current match in the text
		int b = 0; //pos of current character in string pattern
		int textLength = text.length();
		while(a + 1 < textLength){
			if (pattern.charAt(b) == text.charAt(a+b)) {
				b = b + 1;
				if (b==patLength){
					return a;
				}
			} else if (match[b] == -1){
				a = a + b + 1;
				b = 0;
			} else {
				a = a + b - match[b];
				b = match[b];
			}
		}
		return -1;
	}
}
