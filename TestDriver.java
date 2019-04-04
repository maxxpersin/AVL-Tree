import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Maxx Persin
 *
 */
public class TestDriver {
	static AVLTree tester;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] fieldLengths = { 5, 10 };
		char[][] data = new char[2][];

		for (int i = 0; i < 2; i++) {
			data[i] = new char[fieldLengths[i]];
		}
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = (char) ((i + j) + 95);
			}
		}
		try {
			File f = new File("test.bin");
			if (f.exists()) {
				System.out.println("exists");
				tester = new AVLTree("test.bin");
				tester.insert(1, data);
				LinkedList<String> l1 = tester.find(1);
				if (l1 != null) {
					for (int i = 0; i < l1.size(); i++) {
						System.out.println(l1.get(i));
					}
				}
			} else {
				tester = new AVLTree("test.bin", fieldLengths);
			}
			tester.insert(5, data);
			// tester.print();

			tester.insert(6, data);
			LinkedList<String> l = tester.find(6);
			if (l != null) {
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
			}
			l = tester.find(5);
			if (l != null) {
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
			}
			tester.insert(7, data);
			l = tester.find(7);
			if (l != null) {
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
			}
			tester.print();
			tester.insert(20, data);
			l = tester.find(20);
			if (l != null) {
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
			}
			tester.insert(15, data);
			l = tester.find(15);
			if (l != null) {
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
			}

			tester.print();
			tester.remove(7);
			tester.print();
			tester.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
