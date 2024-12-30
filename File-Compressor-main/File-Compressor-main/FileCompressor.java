import java.util.*;
import java.io.*;

class FileCompressor {

    // Creating two maps for mapping the characters in the data with respective keys.
    HashMap<Character, String> encoder;
    HashMap<String, Character> decoder;
     
    // Creating Node class to implement Nodes
    private class Node implements Comparable<Node> {
        Character data;
        int cost; // to maintain the frequency count
        Node left;
        Node right;

        // When new node is initialized
        public Node(Character data, int cost) {
            this.data = data;
            this.cost = cost;
            this.left = null;
            this.right = null;
        }

        @Override
        // Overriding the compareTo method to compare the values
        public int compareTo(Node other) {
            return this.cost - other.cost; // compares the cost of the global variable to the newly created node's cost
        }     //comparing node with cost
    }

    public void HuffmanEncoder(String feeder) throws Exception {
        // Calculate the frequency of each character
        HashMap<Character, Integer> map = new HashMap<>();
        for (char i : feeder.toCharArray()) {
            map.put(i, map.getOrDefault(i, 0) + 1);
        }

        // Create a priority queue (min-heap) to build the Huffman tree
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            Node node = new Node(entry.getKey(), entry.getValue());
            minHeap.add(node);
        }

        // Build the Huffman tree
        while (minHeap.size() > 1) {
            Node first = minHeap.poll();
            Node second = minHeap.poll();
            Node newNode = new Node('\0', first.cost + second.cost);
            newNode.left = first;
            newNode.right = second;
            minHeap.add(newNode);
        }

        // Final tree root
        Node root = minHeap.poll();

        this.encoder = new HashMap<>();
        this.decoder = new HashMap<>();

        // Initialize encoder and decoder maps
        initEncoderDecoder(root, "");
    }

    private void initEncoderDecoder(Node node, String osf) {
        // osf-- output so far
        if (node == null) return;

        if (node.left == null && node.right == null) {
            this.encoder.put(node.data, osf);
            this.decoder.put(osf, node.data);
            return;
        }

        // Recursively calling the function to access all the nodes until it reaches the leaf node.
        initEncoderDecoder(node.left, osf + "0");
        initEncoderDecoder(node.right, osf + "1");
    }

    // To encode the data i.e., ENCRYPTION
    public String encode(String source) {
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            ans.append(encoder.get(source.charAt(i)));
        }
        return ans.toString();
    }

    // To decode the data i.e., DECRYPTION
    public String decode(String codedString) {
        StringBuilder ans = new StringBuilder();
        String key = "";
        for (int i = 0; i < codedString.length(); i++) {
            key += codedString.charAt(i);
            if (decoder.containsKey(key)) {
                ans.append(decoder.get(key));
                key = "";
            }
        }
        return ans.toString();
    }

    // Encode the data into a binary format
    public byte[] encodeToBinary(String source) {
        StringBuilder encodedString = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            encodedString.append(encoder.get(source.charAt(i)));
        }

        // Convert the encoded string to a byte array
        int length = encodedString.length();
        int byteLength = (length + 7) / 8;
        byte[] encodedBytes = new byte[byteLength];

        for (int i = 0; i < length; i++) {
            if (encodedString.charAt(i) == '1') {
                encodedBytes[i / 8] |= (1 << (7 - i % 8));
            }
        }

        return encodedBytes;
    }

    // Decode the data from a binary format
    public String decodeFromBinary(byte[] encodedBytes, int originalLength) {
        StringBuilder encodedString = new StringBuilder();

        for (int i = 0; i < originalLength; i++) {
            if ((encodedBytes[i / 8] & (1 << (7 - i % 8))) != 0) {
                encodedString.append('1');
            } else {
                encodedString.append('0');
            }
        }

        return decode(encodedString.toString());
    }

    public static void main(String[] args) throws Exception {
        FileCompressor compressor = new FileCompressor();

        // Input text needed to be encoded

        // case--0: Directly applying the input string as a test-case
        // String feeder = "this is an example for huffman encoding";

        // case--1: Reading data from console log
        // Scanner obj= new Scanner(System.in);
        // String feeder= obj.nextLine();
        // obj.close();

        // case--2: Reading data from text file
        String filepath = "C:\\Users\\91939\\Downloads\\File-Compressor-main\\File-Compressor-main\\in.txt";
        StringBuilder feeder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) { // Runs until the last word is appended into the feeder.
                feeder.append(line).append("\n");
            }
        }
        // Remove the last newline character added in the while loop.
        if (feeder.length() > 0) {
            feeder.setLength(feeder.length() - 1);
        }

        // Encode the data
        compressor.HuffmanEncoder(feeder.toString());
        byte[] encoded = compressor.encodeToBinary(feeder.toString());
        int originalLength = compressor.encode(feeder.toString()).length();

        System.out.println("Encoded data is now available in encoded.bin ");

        // Write the encoded bytes to a file
        String encodedFilePath = "encoded.bin"; // Specify the path to your encoded output file
        try (FileOutputStream fos = new FileOutputStream(encodedFilePath)) {
            fos.write(encoded);
        }

        // Decode the data
        byte[] encodedBytes;
        try (FileInputStream fis = new FileInputStream(encodedFilePath)) {
            encodedBytes = fis.readAllBytes();
        }
        String decoded = compressor.decodeFromBinary(encodedBytes, originalLength);
        System.out.println("Decoded data is now available in decoded.txt ");

        // Write the decoded string to a file
        String decodedFilePath = "decoded.txt"; // Specify the path to your decoded output file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(decodedFilePath))) {
            bw.write(decoded);
        }
    }
}
