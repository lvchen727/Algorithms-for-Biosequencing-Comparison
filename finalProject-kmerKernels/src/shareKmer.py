###########################################################################
#
# This python program implements a 2BWT-based algorithm to find all shared k-mers between two sequences
#  as well as the distinct k-mers for each sequence.
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
    return rankall, C

"""
#simple test case
seq = 'ATTATTAATTATATTATAATATA'
bwt =   'ATTTTTATTTA$AATATTTAAAAA'
r_bwt = 'ATTTT$TATTTATATATTAAAAAA'
"""

#######################################################################
# Load bwt, r_bwt for both sequences
# compute rankall, cc, C for bwt and r_rankall, r_cc, r_C for r_bwt
#######################################################################
import sys
inFile1 = sys.argv[1]
inFile2 = sys.argv[2]

rdFile1 = open(inFile1, "r")
bwt1 = rdFile1.read()
rdFile1.close()
rdFile2 = open(inFile2, "r")
r_bwt1 = rdFile2.read()
rdFile2.close()

N1 = len(bwt1)
rankall1, C1 = rankAll(bwt1)
r_rankall1, r_C1 = rankAll(r_bwt1)

inFile3 = sys.argv[3]
inFile4 = sys.argv[4]

rdFile3 = open(inFile3, "r")
bwt2 = rdFile3.read()
rdFile3.close()
rdFile4 = open(inFile4, "r")
r_bwt2 = rdFile4.read()
rdFile4.close()

N2 = len(bwt2)
rankall2, C2 = rankAll(bwt2)
r_rankall2, r_C2 = rankAll(r_bwt2)
print "file loaded"


#######################################################################
# compute both the lexicographic and the colexicographic intervals of ca
# given the lexicographic and the colexicographic intervals of a
#
# extendLeft:  extend a to the left with the character c.
#
#######################################################################

def extendLeft (((i,j), (r_i,r_j)), c, rankall, C):
    ic = C[c] + rankall[c][i-1] + 1
    jc = C[c] + rankall[c][j]
    r_ic = r_i
    for base in Alphabet:
        if base < c:
            r_ic = r_ic + rankall[base][j] - rankall[base][i-1]
    r_jc = r_ic + (jc - ic)
    return (ic,jc), (r_ic, r_jc)

def isNotValid(( (i,j), (r_i,r_j) )):
    return j < i or r_j < r_j



#######################################################################
# Count common k-mers by go through 2bwt using two parallel stacks for
#   for each sequence.
#
#######################################################################

import operator

def sharedKmer(k,n1,n2, rankall1, C1, rankall2, C2):
    stack1 = []
    stack1.append( (( (1,n1) , (1,n1) ) , 0 ) )
    stack2 = []
    stack2.append( (( (1,n2) , (1,n2) ) , 0 ) )
    
    count = 0
    while stack1 or stack2:                                 
        (I1, d) = stack1.pop()
        (I2, d) = stack2.pop()
        
        if (d == k):
            count = count + 1
            continue
    
        for c in Alphabet:
            if c == '$':
                continue
            I1_left = extendLeft( I1 , c, rankall1, C1 )
            I2_left = extendLeft( I2 , c, rankall2, C2 )
            
            if isNotValid(I1_left) or isNotValid(I2_left):
                continue
            stack1.append( ( I1_left , d + 1 ) )
            stack2.append( ( I2_left , d + 1 ) )
                
    return count

#######################################################################
# ns: number of shared k-mers
# k1: number of distinct k-mers for seq1
# k2: number of distnct k-mers for seq2
# distance: genetic distance calculated from ns, k1, k2 using AAF paper function
#######################################################################

import math
K = int(sys.argv[5])
print sys.argv[1], sys.argv[3], "K=",K

ns = sharedKmer(K, N1, N2, rankall1, C1, rankall2, C2)
k1 = sharedKmer(K, N1, N1, rankall1, C1, rankall1, C1)
k2 = sharedKmer(K, N2, N2, rankall2, C2, rankall2, C2)
distance = (-1)* math.log(float(ns)/min(k1,k2))/K
print ns, k1, k2
print distance






