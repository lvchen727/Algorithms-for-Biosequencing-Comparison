
import sys
inFile = sys.argv[1]

outFile1 =  sys.argv[2]
output1 = open(outFile1, "w")

outFile2 = sys.argv[3]
output2 = open(outFile2, "w")

sequence = ''
for i in range(250):
    with open(inFile, 'r') as fin:
    
        for line in fin:
            if line.startswith('>'):
                continue
            else:
                dna = line.strip();
                sequence += dna.replace("N","")


output1.write(sequence)
output2.write(sequence[::-1])



