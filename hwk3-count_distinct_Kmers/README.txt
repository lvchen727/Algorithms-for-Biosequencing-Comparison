This python program implements a 2BWT-based algorithm to find all distinct k-mers (substrings of length k) for a very large DNA sequence. The whole program largely referred the paper “Space-efficient clustering of metagenomic read sets” by Jarno Niklas Alanko.

The original program was written and tested in ipython notebook, as shown in the folder. For reading simplicity, the python version code was uploaded too.

There are four crucial functions, as stated in the paper:

1. rankAll : returns rankall gives # of c in the prefix bwt[1..i]), 
	cc gives # times the character appears in bwt
	C gives # of c in bwt that have lexicographical rank strictly less than i.

2. extendRight/extendLeft: compute both the lexicographic and the colexicographic intervals of ca given the lexicographic and the colexicographic intervals of a

3. isRightMaximal:Check if the interval pairs right maximal, we use this function to determine if we push the interval in the stack in the function markKmer.

4. markKmers: mark all distinct kmers using a List B. B[i] = 1 if and only if the suffix with rank i is the lexicographically least in its class.


The simple test case I used to check my program was from the paper:

seq = “ATTATTAATTATATTATAATATA”
bwt =  “ATTTTTATTTA$AATATTTAAAAA”
r_bwt = “ATTTT$TATTTATATATTAAAAAA”

The output was checked manually and looked fine.
k Nk
1 2
2 4
3 6
4 10
5 15
6 17
7 17
8 16
9 15
10 14
11 13
12 12
13 11
14 10
15 9
16 8
17 7
18 6
19 5
20 4
21 3
22 2
23 1

The program intially runs really slow given bwt and rev_bwt of hu-chr1.
However, after applying the trick from Problem 2(b) - always pushing the
largest bi-interval I onto the stack first by modifying markKmer function 
a little bit, the program starts working well. It generated outputs, but probably
need one week to generate all the outputs.
