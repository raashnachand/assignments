/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {
	/**
	 * This would be a good place to compute and store the tree.
	 */
	Node root;
	public HuffmanCoding(String text) {
		//Map to store character-frequency pairs; calculate frequency for each character
		Map<Character, Integer> frequencies = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }
		//Creating nodes and storing them in a priority queue
		PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            char character = entry.getKey();
            int frequency = entry.getValue();
            priorityQueue.offer(new Node(character, null, null, frequency));
        }

        //Build the Huffman tree by merging nodes from the priority queue
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node('\0', left, right, left.getFrequency() + right.getFrequency());
            priorityQueue.offer(parent);
        }
		root = priorityQueue.poll();
		assignCodes(root, "");
	}

	/**
	 * Helper method to assign codes to the Huffman tree. Adds 0
	 * to the code of the left node, and 1 to the code of the right
	 * node.
	 */
	public void assignCodes(Node node, String code){
		if(node == null){
			return;
		}
		node.setCode(code);
        assignCodes(node.getLeft(), code + "0");
        assignCodes(node.getRight(), code + "1");
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		//Map used to traverse the tree to retrieve character-code pairings
		Map<Character, String> codeMap = new HashMap<>();
		buildCodeMap(codeMap, root, "");
		StringBuilder encodedText = new StringBuilder();
		//Go through each character, retrieve its code and append to the string
        for (char c : text.toCharArray()) {
            encodedText.append(codeMap.get(c));
        }
        return encodedText.toString();
	}

	/**
	 * Tree traversal.
	 * This makes it easier to retrieve the code of a character.
	 */
	public void buildCodeMap(Map<Character, String> codeMap, Node node, String code) {
        if (node == null) {
            return;
        }
        if (node.getLeft() == null && node.getRight() == null) {
            codeMap.put(node.getChar(), code);
            return;
        }
        buildCodeMap(codeMap, node.getLeft(), code + "0");
        buildCodeMap(codeMap, node.getRight(), code + "1");
    }

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		StringBuilder decodedText = new StringBuilder();
        Node current = root;
		//decoding each bit in the encoded text
        for (char bit : encoded.toCharArray()) {
            if (bit == '0') {
                current = current.getLeft();
            } else if (bit == '1') {
                current = current.getRight();
            }
			//if we've reached the character we were looking for, return to the root to search for the next character
            if (current.getLeft() == null && current.getRight() == null) {
                decodedText.append(current.getChar());
                current = root;
            }
        }
        return decodedText.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}
}

/**
 * Node class - these store the character, the frequency of the character, 
 * and its left and right nodes.
 */
class Node implements Comparable<Node>{

	Character character;
	Node left;
	Node right;
	int frequency;
	String code;

	public Node(Character c, Node l, Node r, int freq){
		character = c;
		left = l;
		right = r;
		frequency = freq;
		code = "";
	}
	//getters
	public Character getChar(){return character;}
	public Node getLeft(){return left;}
	public Node getRight(){return right;}
	public int getFrequency(){return frequency;}
	public String getCode(){return code;}
	//setters
	public void setCode(String c){code = c;}
	/**
	 * Compare to method for the node priority queue.
	 * Ordered by lowest frequency first, then by first character alphabetically.
	 * @param other - node to compare with
	 * @return - priority of this node
	 */
	public int compareTo(Node other){
        if(this.frequency < other.frequency){return 1;}
        else if (this.frequency > other.frequency){return -1;}
        else {
            if(this.character < other.character){return 1;}
            else if(this.character > other.character){return -1;}
        }
        return 0;
    }
}
