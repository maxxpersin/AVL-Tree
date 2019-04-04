
/**
 * 
 * @author Maxx Persin
 *
 */

import java.io.*;
import java.util.*;

public class AVLTree {
	/*
	 * Implements a ALV tree of ints (the keys) and fixed length character strings
	 * fields stored in a random access file. Duplicates keys are not allowed. There
	 * will be at least 1 character string field
	 */
	private RandomAccessFile f;
	private long root; // the address of the root node in the file
	private long free; // the address in the file of the first node in the free list
	private int numFields; // the number of fixed length character fields
	private int fieldLengths[]; // the length of each field, length of this is how many fields
	// each int in the array is the length of that field
	private LinkedList<Long> freeList;
	private Node rootNode;

	private class Node {
		private int key;
		private char fields[][]; // first length is number of fields
		private long left;
		private long right;
		private int height;

		private Node(long l, int d, long r, char fields[][]) { // constructor for a new node
			left = l;
			key = d;
			right = r;
			this.fields = fields;
			height = 0;
		}

		private Node(long addr) throws IOException { // constructor for a node that exists and is stored in the file
			// Node data = new Node(0, 0, 0, null);
			if (addr == 0) {
				left = 0;
				key = 0;
				right = 0;
				this.fields = null;
				height = 0;
			} else {
				f.seek(addr);
				key = f.readInt();
				fields = new char[numFields][];
				for (int i = 0; i < numFields; i++) {
					fields[i] = new char[fieldLengths[i]];
				}
				for (int i = 0; i < fields.length; i++) {
					for (int j = 0; j < fields[i].length; j++) {
						fields[i][j] = f.readChar();
					}
				}
				left = f.readLong();
				right = f.readLong();
				height = f.readInt();
			}
		}

		private void writeNode(long addr) throws IOException { // writes the node to the file at location addr
			if (addr != 0) {
				f.seek(addr);
				f.writeInt(key);
				for (int i = 0; i < fields.length; i++) {
					for (int j = 0; j < fields[i].length; j++) {
						f.writeChar(fields[i][j]);
					}
				}
				f.writeLong(left);
				f.writeLong(right);
				f.writeInt(height);
			}
		}
	}

	public AVLTree(String fname, int fieldLengths[]) throws IOException {
		// creates a new empty AVL tree stored in the file fname
		// the number of character string fields is fieldLengths.length
		// fieldLengths contains the length of each field
		this.fieldLengths = fieldLengths;
		numFields = fieldLengths.length;
		try {
			File file = new File(fname);
			f = new RandomAccessFile(file, "rw");
			f.writeLong(0);
			f.writeLong(0);
			f.writeInt(fieldLengths.length);
			for (int i = 0; i < fieldLengths.length; i++) {
				f.writeInt(fieldLengths[i]);
			}
			freeList = new LinkedList<Long>();
			freeList.add(f.length());
			free = freeList.getFirst();
			freeList.removeFirst();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AVLTree(String fname) throws IOException { // reuse an existing tree store in the file fname
		f = new RandomAccessFile(fname, "rw");
		root = f.readLong();
		free = f.readLong();
		freeList = new LinkedList<Long>();
		freeList.add(free);
		numFields = f.readInt();
		fieldLengths = new int[numFields];
		for (int i = 0; i < fieldLengths.length; i++) {
			fieldLengths[i] = f.readInt();
		}
		rootNode = new Node(root);
	}

	public void insert(int k, char fields[][]) throws IOException {
		// PRE: the number and lengths of the fields matches the expected number and
		// lengths
		// insert k and the fields into the tree
		// if k is in the tree do nothing
//		if (foundInTree(k)) {
//			 System.out.println(k + " was in tree");
//			return;
//		} else {
			// System.out.println(root + " was root2");
			root = insert(root, k, fields);
			rootNode = new Node(root);
			rootNode.writeNode(root);
			f.seek(0);
			f.writeLong(root);
			// writeRoot();
		//}
	}

	private void writeRoot() throws IOException {
		f.seek(0);
		f.writeLong(root);
	}

	private long insert(long r, int k, char[][] fields) throws IOException {
		Node n;
		// System.out.println("Now inserting " + k);
		if (r == 0) {
			n = new Node(0, k, 0, fields);
			long addr = free;
			n.writeNode(addr);
			if (!freeList.isEmpty()) {
				free = freeList.removeFirst();
			} else {
				free = getFree();
			}
			return addr;
		}
		n = new Node(r);
		// System.out.println(n.key + " is n's key");
		if (k < n.key) {
			n.left = insert(n.left, k, fields);
		} else if (k > n.key) {
			n.right = insert(n.right, k, fields);
		}
		Node left = new Node(n.left);
		Node right = new Node(n.right);
		if (n.left != 0 && n.right != 0) {
			n.height = Math.max(left.height, right.height) + 1;
			// System.out.println(n.height + " is n's height before balance");
		} else if (n.left == 0 && n.right != 0) {
			n.height++;
			// System.out.println(n.height + " is n's height before balance");
		} else if (n.left != 0 && n.right == 0) {
			n.height++;
			// System.out.println(n.height + " is n's height before balance");
		} else {
			n.height = 0;
			// System.out.println(n.height + " is n's height before balance");
		}
		n.writeNode(r);
		// r = balance(r);
		// n.height = Math.max(left.height, right.height) + 1;
		// n.writeNode(r);

		// System.out.println(r + " was r");
		return balance(r);
		// return r;
	}

	private long balance(long r) throws IOException {
		Node n;
		if (r == 0) {
			return r;
		}

		n = new Node(r);
		// System.out.println(n.left + " " + n.key + " " + n.right);
		if (n.left == 0 && n.right == 0) {
			return r;
		}
		Node left = new Node(n.left);
		Node right = new Node(n.right);
		// System.out.println(left.height);
		// System.out.println(right.height);
		int leftHeight, rightHeight;
		leftHeight = left.height;
		rightHeight = right.height;
		if (leftHeight == 0 && rightHeight != 0) {
			rightHeight++;
		} else if (rightHeight == 0 && leftHeight != 0) {
			leftHeight++;
		}
		if (leftHeight - rightHeight > 1) {
			Node leftRight = new Node(left.right);
			Node leftLeft = new Node(left.left);
			if (leftLeft.height >= leftRight.height) {
				r = rotateLeft(r);
			} else {
				r = doubleRotateLeft(r);
			}
		} else if (rightHeight - leftHeight > 1) {
			Node rightRight = new Node(right.right);
			Node rightLeft = new Node(right.left);
			if (rightRight.height >= rightLeft.height) {
				r = rotateRight(r);
			} else {
				r = doubleRotateRight(r);
			}
		}
		n = new Node(r);
		n.height = Math.max(left.height, right.height) + 1;
		// System.out.println(n.height + " was n's height after balance");
		n.writeNode(r);
		// System.out.println(root + " root after balance");
		return r;
	}

	private long rotateLeft(long r) throws IOException {
		Node n = new Node(r); // k2
		long retAddr = n.left;
		Node left = new Node(n.left); // k1 = k2.left
		n.left = left.right; // k2.left = k1.right
		left.right = r; // k1.right = k2
		Node nLeft = new Node(n.left);
		Node nRight = new Node(n.right);
		Node lLeft = new Node(left.left);
		if (nLeft.fields == null && nRight.fields == null) {
			n.height = 0;
		} else {
			n.height = Math.max(nLeft.height, nRight.height) + 1;
		}
		if (lLeft.fields == null && n.fields == null) {
			left.height = 0;
		} else {
			left.height = Math.max(lLeft.height, n.height) + 1;
		}
		n.writeNode(r);
		left.writeNode(retAddr);
		// System.out.println(left.key + "YEEEEEEEEE left");
		// System.out.println(n.key + "root key");
		return retAddr;
	}

	private long rotateRight(long r) throws IOException {
		Node n = new Node(r);
		long retAddr = n.right;
		// System.out.println(retAddr + " supposed return address for balance");
		Node right = new Node(n.right);
		n.right = right.left;
		right.left = r;
		Node nLeft = new Node(n.left);
		Node nRight = new Node(n.right);
		Node rRight = new Node(right.right);
		if (nLeft.fields == null && nRight.fields == null) {
			n.height = 0;
		} else {
			n.height = Math.max(nLeft.height, nRight.height) + 1;
		}
		if (rRight.fields == null && n.fields == null) {
			right.height = 0;
		} else {
			right.height = Math.max(rRight.height, n.height) + 1;
		}
		n.writeNode(r);
		right.writeNode(retAddr);
		// System.out.println(right.key + "YEEEEEEEEEE right");
		return retAddr;
	}

	private long doubleRotateLeft(long r) throws IOException {
		Node n = new Node(r);
		n.left = rotateRight(n.left);
		n.writeNode(r);
		return rotateLeft(r);
	}

	private long doubleRotateRight(long r) throws IOException {
		Node n = new Node(r);
		// System.out.println("right double rotation ran with r being " + n.key);
		n.right = rotateLeft(n.left);
		n.writeNode(r);
		return rotateRight(r);
	}

	private boolean foundInTree(int k) {
		Node data = new Node(0, k, 0, null);
		return foundInTree(data, root);
	}

	private boolean foundInTree(Node d, long n) {
		try {
			if (n == 0) {
				// System.out.println("n was 0");
				return false;
			}
			Node cur = new Node(n);
			if (cur.key > d.key) {
				return foundInTree(d, cur.left);
			} else if (cur.key < d.key) {
				return foundInTree(d, cur.right);
			} else {
				// System.out.println("true");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void print() throws IOException { // Print the contents of the nodes in the tree is ascending order of the
												// key
		print(root);
		System.out.println();
	}

	private void print(long r) throws IOException {
		if (r == 0) {
			return;
		}
		Node n = new Node(r);
		print(n.left);
		System.out.println(r + " " + n.key + " " + n.left + " " + n.right + " " + n.height);
		print(n.right);
	}

	public LinkedList<String> find(int k) throws IOException {
		// if k is in the tree return a linked list of the fields associated with k
		// otherwise return null
		// The strings in ths list must NOT include the padding (i.e the null chars)
		Node data = new Node(0, k, 0, null);
		return find(data, root);
	}

	private LinkedList<String> find(Node d, long n) {
		// System.out.println(n + " cur node start");
		try {
			if (n == 0) {
				return null;
			}
			Node cur = new Node(n);
			if (cur.key > d.key) {
				// System.out.println(cur.key + " " + d.key);
				return find(d, cur.left);
			} else if (cur.key < d.key) {
				return find(d, cur.right);
			} else {
				f.seek(n);
				LinkedList<String> data = new LinkedList<String>();
				data.add(cur.key + "");
				String payload = "";
				for (int i = 0; i < fieldLengths.length; i++) {
					for (int j = 0; j < fieldLengths[i]; j++) {
						payload += f.readChar();
					}
					data.add(payload);
					payload = "";
				}
				data.add(cur.left + "");
				data.add(cur.right + "");
				data.add(cur.height + "");
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private long getFree() throws IOException {
		// If free list is empty, return end of file address
		// else return top object in free list
		if (freeList.isEmpty()) {
			return f.length();
		} else {
			long r = freeList.getFirst();
			freeList.removeFirst();
			return r;
		}
	}

	public void remove(int k) throws IOException {
		// if k is in the tree removed the node with key k from the tree
		// otherwise do nothing
//		if (!foundInTree(k)) {
//			return;
//		} else {
			root = remove(root, k);
		//}
	}

	private long remove(long r, int k) throws IOException {
		if (r == 0) {
			return r;
		}
		Node n = new Node(r);
		if (n.key > k) {
			n.left = remove(n.left, k);
		} else if (n.key < k) {
			n.right = remove(n.right, k);
		} else if (n.left == 0 && n.right == 0) {
			n = (n.left != 0) ? new Node(n.left) : new Node(n.right);
			n.writeNode(r);
		} else {
			System.out.println("else remove ran");
			Node removed = findMin(n.right);
			n.key = removed.key;
			n.fields = removed.fields;
			//removePointer(r, removed);
			//Node right = new Node(n.right);
			//n.right = remove(n.right, k);
			n.writeNode(r);
			
		}

		return balance(r);
	}

	private Node findMin(long r) throws IOException {
		// PRE: r != 0
		Node n = new Node(r);
		if (n.right == 0) {
			freeList.add(r);
			return n;
		} else {
			return findMin(n.right);
		}
	}

	public void close() throws IOException {
		f.seek(0);
		f.writeLong(root);
		f.writeLong(getFree());
		f.writeInt(fieldLengths.length);
		for (int i = 0; i < fieldLengths.length; i++) {
			f.writeInt(fieldLengths[i]);
		}
		f.close();
	}

}
