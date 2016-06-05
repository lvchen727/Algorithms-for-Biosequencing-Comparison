package ahoCoroasickAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * This is the main function for testing the Aho Corasick Algorithm.
 * @author Chen
 *
 */
public class Test {
	public static void main(String[] args) throws IOException{
		
/*
 * Below is a small test for testing the correctness of the code.	
 */
//		Trie trie = new Trie();
//		
//		String p1 = "TCG";
//		trie.addPattern(p1, 2, 1);
//		String rP1 = reverseComplement(p1);
//		
//		trie.addPattern(rP1, 2, 1);
//		String p2 = "ATCG";
//		trie.addPattern(p2,1,1);
//		
//		String rP2 = reverseComplement(p2);
//		trie.addPattern(rP2,1,1);
//	
//		
//		
//	    Collection<Emit> emits = trie.parseText("ATCGCGAT");
//	    System.out.println(emits);
		
//		Trie trie = new Trie();
//		trie.addPattern("HE",1,1);
//		trie.addPattern("SHE",2,1);
//		trie.addPattern("THEY",3,1);
//		trie.addPattern("THEM",4,1);
//	    Collection<Emit> emits = trie.parseText("THEMTHEY");
//	    System.out.println(emits);

		/**
		 * Here shows the code for computing pat1.txt
		 */
		Trie trie1 = new Trie();
		File pat1Path = new File("/Users/Chen/Documents/CS courses/CS131/workspace/Aho-Corasick Algorithm/src/ahoCoroasickAlgorithm/hwk1-test-cases/pat5.txt");
		BufferedReader pat1 = new BufferedReader(new InputStreamReader(new FileInputStream(pat1Path)));
		int i1 = 1;
		String p1 ;
		String recomP1;
		while((p1 = pat1.readLine()) != null){
			trie1.addPattern(p1,i1,1);
			recomP1 = reverseComplement(p1);
			trie1.addPattern(recomP1,i1,-1);
			i1++;
		}

	   try {
		   
		   RandomAccessFile aFile = new RandomAccessFile("/Users/Chen/Documents/CS courses/CS131/workspace/Aho-Corasick Algorithm/src/ahoCoroasickAlgorithm/hwk1-test-cases/hu-chr1.txt", "r");
		   FileChannel inChannel = aFile.getChannel();
		   MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
		   buffer.load();
		   CharBuffer charBuffer = StandardCharsets.US_ASCII.decode(buffer);
		   String read = charBuffer.toString();
		   
		   int count = 0;
		   Collection<Emit> results = trie1.parseText(read);
		   for (Emit e:results){
			   System.out.println(e.toString());
			   count ++;
		   }
		   System.out.println(count);
		   buffer.clear(); // do something with the data and clear or compact it.
		   inChannel.close();
		   aFile.close();
        
	   } catch (IOException ioe) {
		   ioe.printStackTrace();
	   }
	}

	/**
	 * This function is to map DNA to its reverse-complementary strand.
	 * @param dna
	 * @return reverse complementary strand of dna
	 */
	public static String reverseComplement(String dna){		
		String reComp = "";
		for(int i = dna.length() - 1; i >= 0; i--){
			if(dna.charAt(i) == 'A')
				reComp += 'T';
			if(dna.charAt(i) == 'T')
				reComp += 'A';
			if(dna.charAt(i) == 'C')
				reComp += 'G';
			if(dna.charAt(i) == 'G')
				reComp += 'C';
		}
		return reComp;
	}

}
