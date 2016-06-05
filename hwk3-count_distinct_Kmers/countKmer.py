###########################################################################
#
# This python program implements a 2BWT-based algorithm to find all distinct k-mers
# (substrings of length k) for a very large DNA sequence.
# The whole program largely referred the paper "Space-efficient clustering of metagenomic
# read sets" by Jarno Niklas Alanko.
#
# Author: Chen Lu
#
# Note : The code used  1-based indices, i.e., the string starts at position 1.
##########################################################################

Alphabet = ['A','T','G','C','$']

##########################################################################
# Given BWT string bw:
# it returns a parallel list of B-ranks rankall, rankall[c][i] gives # of c in the prefix bwt[1..i]
# it returns cc, a mapping from characters to # times the character appears in bwt.
# it returns C, C[i] is # of characters in bwt that have lexicographical rank strictly less than i.
##########################################################################

def rankAll(bwt):
    cc={}
    C = {}
    rankall={}
    for c in Alphabet:
        cc[c] = 0
        rankall[c] =[]
        rankall[c].append(0)
    for c in bwt :
        cc[c] = cc[c]+1
        for c in cc.iterkeys():
            rankall[c].append(cc[c])
    for base in Alphabet:
        C[base] = 0
        for c in Alphabet:
            if c < base:
                C[base] += cc[c]
    return rankall, cc, C

"""
#simple test case
seq = 'ATTATTAATTATATTATAATATA'
bwt =   'ATTTTTATTTA$AATATTTAAAAA'
r_bwt = 'ATTTT$TATTTATATATTAAAAAA'
"""

#######################################################################
# Load bwt, r_bwt
# compute rankall, cc, C for bwt and r_rankall, r_cc, r_C for r_bwt
#######################################################################

path1 = "/Users/Chen/Downloads/hu-chr1-bwts/hu-chr1.bwt.txt"
path2 = "/Users/Chen/Downloads/hu-chr1-bwts/hu-chr1-rev.bwt.txt"
rdFile1 = open(path1, "r")
bwt = rdFile1.read()
rdFile1.close()
rdFile2 = open(path2, "r")
r_bwt = rdFile2.read()
rdFile2.close()

N = len(bwt)
rankall, cc, C = rankAll(bwt)
r_rankall, r_cc, r_C = rankAll(r_bwt)
print "file loaded"


#######################################################################
# compute both the lexicographic and the colexicographic intervals of ca
# given the lexicographic and the colexicographic intervals of a
#
# extendLeft:  extend a to the left with the character c.
#
# extendRight: extend a to the right with the character c.
#
#######################################################################

def extendLeft (((i,j), (r_i,r_j)), c):
    ic = C[c] + rankall[c][i-1] + 1
    jc = C[c] + rankall[c][j]
    r_ic = r_i
    for base in Alphabet:
        if base < c:
            r_ic = r_ic + rankall[base][j] - rankall[base][i-1]
    r_jc = r_ic + (jc - ic)
    return (ic,jc), (r_ic, r_jc)

def extendRight ( ( (i,j), (r_i,r_j) ) , c):
    r_ic = C[c] + r_rankall[c][r_i - 1] + 1
    r_jc = C[c] + r_rankall[c][r_j]
    ic = i
    for base in Alphabet:
        if base < c:
            ic = ic + r_rankall[base][r_j] - r_rankall[base][r_i-1] 
            #print base, r_rankall[base][j] - r_rankall[base][i-1] , ic
    jc = ic + (r_jc - r_ic)
    return ( (ic,jc) , (r_ic,r_jc) )


def isValid (( (i,j), (r_i,r_j) )):
    return j >= i and r_j >= r_j

#######################################################################
# Check if the interval pairs right maximal:
# A substring of a string S is right-maximal if and only if there exist
# at least two distinct characters c and d such that both c and d are substrings of S.
#######################################################################
            
def isRightMaximal( ((i,j) , (r_i,r_j)) ):
    A = extendRight( ( (i,j) , (r_i,r_j)) , 'A' )
    C = extendRight( ( (i,j) , (r_i,r_j)) , 'C' )
    G = extendRight( ( (i,j) , (r_i,r_j)) , 'G' )
    T = extendRight( ( (i,j) , (r_i,r_j)) , 'T' )
    Dollar = extendRight( ( (i,j) , (r_i,r_j)) , '$')
    L = [isValid(A),isValid(T),isValid(C),isValid(G),isValid(Dollar)]
    return sum(L) >= 2

#print isRightMaximal(((21, 21), (4, 4)))


#######################################################################
# mark all distinct kmers
# two suffixes si and sj are in same class if and only if |si|>=k,|sj|>=k and
# si[1..k] = sj[1..k]. B[i] = 1 if and only if the suffix with rank i is the lexicographically
# least in its class, i.e., we mark the start of the interval of each class with 1.
# With all starts marked, we only need to sum them then minus k to get # of distinct k-mers
#######################################################################

def markKmer(k,n):
    stack = []
    stack.append( (( (1,n) , (1,n) ) , 0 ) )
    B = [0] * (n+1)
    count = 0
    while stack:
        (intervals, depth) = stack.pop()
        if depth < k:
            for c in Alphabet:
                ((i, j), (r_i, r_j))= extendRight( intervals , c )
                if j >= i:
                    B[i] = 1
        for c in Alphabet:
            intervals_left = extendLeft( intervals , c )
            if isRightMaximal(intervals_left) :
                stack.append( ( intervals_left , depth + 1 ) )
    return sum(B) - k

for k in range(15,41):
    print k, markKmer(k, N)




