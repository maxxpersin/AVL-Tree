import java.io.*;
import java.util.*;


public class h4 {
    
    private AVLTree trees[];
    private int fieldLengths[][];
    
    private BufferedReader b;
    
    private String getline() throws IOException{
        String line;
        line = b.readLine();
        //skip comment lines in data file
        while (line != null && line.charAt(0) == '#') {
            line = b.readLine();
        }
        return line;
    }
    
    private void inserts(int t) throws IOException {
    //inserts into tree t
        String line = getline();
        String nums[] = line.split(",");
        int key;
        for (int i = 0; i < nums.length; i++) {
            key = new Integer(nums[i]);
            char fields[][] = new char[fieldLengths[t].length][];
            for (int j = 0; j < fieldLengths[t].length; j++) {
                //make char arrays of the correct lengths from the string representation of the key
                //this is an easy way to generate the other fields data
                fields[j] = Arrays.copyOf((new Integer(key)).toString().toCharArray(), fieldLengths[t][j]);
            }
            trees[t].insert(key, fields);
        }
    }
        
        private void prints() throws IOException {
        //print each tree
            for (int i = 0; i < trees.length; i++) {
                System.out.println("Print tree "+i);
                trees[i].print();
            }
        }
        
        private void finds(int t) throws IOException {
        //test find
            String line = getline();
            String nums[] = line.split(",");
            for (int i = 0; i < nums.length; i++) {
                int key = new Integer(nums[i]);
                LinkedList<String> fields = trees[t].find(key);
                if (fields == null) System.out.println(key+": Not found in tree "+t);
                else {
                    System.out.print(key+": "+fields.get(0));
                    for (int j = 1; j < fields.size(); j++) {
                        System.out.print(", "+fields.get(j));
                    }
                    System.out.println();
                }
            }
        }
        
        private void removes(int t) throws IOException {
            //test remove
            String line = getline();
            String nums[] = line.split(",");
            for (int i = 0; i < nums.length; i++) {
                int key = new Integer(nums[0]);
                trees[t].remove(key);
            }
        }
    
    public h4(String args[]) throws IOException{
        int i;
        b = new BufferedReader(new FileReader(args[0]));
        int numTrees = new Integer(getline());
        trees = new AVLTree[numTrees];
        fieldLengths = new int[numTrees][];
        for (i = 0; i < numTrees; i++) {
            String fLen[] = getline().split(",");
            fieldLengths[i] = new int[fLen.length];
            for (int j = 0; j < fLen.length; j++) {
                fieldLengths[i][j] = new Integer(fLen[j]);
            }
        }
        //create the trees
        for (i = 0; i < numTrees; i++) {
            trees[i] = new AVLTree("AVL"+i, fieldLengths[i]);
        }
        System.out.println("pass trees created");
        for (i = 0; i < numTrees; i++) {
            inserts(i);
        }
        System.out.println("pass first inserts");
        prints();
        for (i = 0; i < numTrees; i++) {
            inserts(i);
        }
        System.out.println("pass second inserts");
        prints();
        for (i = 0; i < numTrees; i++) {
            finds(i);
        }
        System.out.println("pass finds");

        for (i = 0; i < numTrees; i++) {
            removes(i);
        }
        
        System.out.println("pass removes");

        for (i = 0; i < numTrees; i++) {
            trees[i].close();
        }
        for (i = 0; i < numTrees; i++) {
            trees[i] = new AVLTree("AVL"+i);
        }
        System.out.println("pass reuse trees");

        prints();
        for (i = 0; i < numTrees; i++) {
            inserts(i);
        }
        System.out.println("pass third inserts");

        for (i = 0; i < numTrees; i++) {
            removes(i);
        }
        
        System.out.println("pass second removes");

        prints();
        
        for (i = 0; i < numTrees; i++) {
            trees[i].close();
        }
        
        //build a larger tree from random input
        Random keyStream = new Random();
        int keys[] = new int[2000];
        int bigFieldLens[] = {15};
        char bigFields[][] = new char[1][];
        AVLTree bigTree = new AVLTree("BigAVL", bigFieldLens);
        for (i = 0; i < 2000; i++) {
            keys[i] = keyStream.nextInt();
            bigFields[0] = Arrays.copyOf((new Integer(keys[i])).toString().toCharArray(), 15);
            bigTree.insert(keys[i], bigFields);
        }
        bigTree.close();
        bigTree = new AVLTree("BigAVL");
        for (i = 0; i < 1900; i++) {
            bigTree.remove(keys[i]);
        }
        bigTree.print();
        for (i = 1900; i < 2000; i++) {
            bigFields[0] = Arrays.copyOf((new Integer(keys[i])).toString().toCharArray(), 15);
            bigTree.insert(keys[i], bigFields);
        }
        for (i = 0; i < 1999; i++) {
            bigTree.remove(keys[i]);
        }
        bigTree.print();
        bigTree.close();
        
        System.out.println("passed random input");
        
    }
    
    public static void main(String args[]) throws IOException {
        new h4(args);
    }
        
}