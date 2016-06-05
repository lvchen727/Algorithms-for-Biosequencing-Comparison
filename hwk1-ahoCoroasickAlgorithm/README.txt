This program implements Aho-Corasick algorithm by building Trie structures.
There are three crucial functions, as stated in the original paper:
1. goto function
2. fail function
3. output function(Here I used Emit class to store all the outputs)

Every base encountered is presented to a state object within the goto structure.
 If there is a matching state, that will be elevated to the new current state.
However, if there is no matching state, the algorithm will signal a fail and fall 
back to states with less depth and proceed from there, until it found a matching state, 
or it has reached the root state.
Whenever a state is reached that matches a pattern, it is emitted to an output 
set which can be read after the entire scan has completed.

Usage:

Setting up Trie
		Trie trie = new Trie();
		String p = "ATCG";
		trie.addPattern(p,5,1); // 5 is the number of pattern in the pattern file
								// 1 indicates its forwarding strand
		String recomP = reverseComplement(p);//reverse complementary pattern
		trie.addPattern(recomP,5,-1);// -1 indicates its reverse strand
		
Matching function		
	    Collection<Emit> emits = trie.parseText("ATCGCGAT");
	    System.out.println(emits);
	    

Test cases results
For each match found, the program emit a triple of integers "t,p,s":
t is the position at which the match starts in the corpus file. The first character is position 1.
p is the line number of the matching pattern the pattern file. The first pattern is line 1.
s is 1 if the match is on the forward strand of the corpus file, or −1 if it is on the reverse- complement strand.
The result are sorted first by t, then by p, and finally by s.

The five file attached here correspond to pat1.txt, pat2.txt and so forth.



Updates on the program:

1. The outputs are now in the format of " t p s" per line for each pattern

2. Update Emit Class(including adding Interval.java and Intervalable.java) to generate complete outputs.