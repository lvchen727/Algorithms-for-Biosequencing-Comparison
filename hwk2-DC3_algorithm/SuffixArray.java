package suffix_array;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SuffixArray {
	
    public static String buildBWT(String str)
    {        
    	str = str + "$";
    	int n = str.length( );
        
        int [ ] s = new int[ n + 3 ];
        int [ ] a = new int[ n + 3 ];
       
        for( int i = 0; i < n; i++ )
            s[ i ] =map(str.charAt( i ));
        
        final int alphabetSize = 4;
        
        buildSuffixArray( s, a, n, alphabetSize);
        s=null; 

        int[] A = new int[n];    
        for( int i = 0; i < n; i++ )
        	A[ i ] = a[ i ];

        a = null;
        
        char[] BWT = new char [A.length];
    	for ( int i = 0; i < A.length; i++  ){
    		if(A[i] != 0){
    			BWT[i] = str.charAt(A[i]-1);
    		}else{
    			BWT[i] = '$';
    		}
    	}
    	
        A =null;
    	String bwt = new String(BWT);
    	BWT = null; 
        return bwt;
    }
    
    
    private static int map(char c){
    	switch (c){
    		case 'A':
    			return 1;
    		case 'C':
    			return 2;
    		case 'G': 
    			return 3;
    		case 'T':
    			return 4;
    		default:
    			return 0;
    	
    	}
    }

    
    /**
     * s: rank
     * A: suffix array
     * n: length of input string
     * K: alphabet size
     */
	 public static void buildSuffixArray( int [ ] s, int [ ] A, int n, int K )
	 {	
		 int n0 = ( n + 2 ) / 3;
	     int n1 = ( n + 1 ) / 3;
	     int n2 = n / 3;
	     int n12 = n0 + n2;

	     int [ ] s12  = new int[ n12 + 3 ];
	     s12[n12]= s12[n12+1]= s12[n12+2]=0; 
	     
	     int [ ] A12 = new int[ n12 + 3 ];
	     A12[n12]=A12[n12+1]=A12[n12+2]=0;
	     
	     int [ ] s0   = new int[ n0 ];
	     int [ ] A0  = new int[ n0 ];
	        
	     // generate positions in s for items in s12
	     // the "+(n0-n1)" adds a dummy mod 1 suffix if n%3 == 1
	     // at that point, the size of s12 is n12
	     for( int i = 0, j = 0; i < n + (n0-n1); i++ ){
	    	 if( i % 3 != 0 ){
	    		 s12[j] = i;
	    		 j++;
	    	 }
	     }
        
	     radixPass( s12 , A12, s, 2, n12, K );
	     radixPass( A12, s12 , s, 1, n12, K );  
	     radixPass( s12 , A12, s, 0, n12, K );
	           
	     // find lexicographic names of triples
	     int name = 0;
	     int c0 = -1, c1 = -1, c2 = -1;
	      
	     for( int i = 0; i < n12; i++ )
	     {
	    	 if( s[ A12[ i ] ] != c0 || s[ A12[ i ] + 1 ] != c1 || s[ A12[ i ] + 2 ] != c2 )
	         { 
	    		 name++;
	             c0 = s[ A12[ i ] ];
	             c1 = s[ A12[ i ] + 1 ];
	             c2 = s[ A12[ i ] + 2 ];
	         }
	          
	    	 if( A12[ i ] % 3 == 1 )
	    		 s12[ A12[ i ] / 3 ]      = name;//left half
	         else
	             s12[ A12[ i ] / 3 + n0 ] = name;//right half 
	     }
	        
	        
	     if( name == n12 ) // all names are unique
	    	 for( int i = 0; i < n12; i++ )
	    		 A12[ s12[i] - 1 ] = i; 
	     else
	     {
	    	 buildSuffixArray( s12, A12, n12, name );
	         // store unique names in s12 using the suffix array 
	    	 for( int i = 0; i < n12; i++ )
	    		 s12[ A12[ i ] ] = i + 1;
	     }
	 
	     for( int i = 0, j = 0; i < n12; i++ )
	    	 if( A12[ i ] < n0 )
	    		 s0[ j++ ] = 3 * A12[ i ];
	        
	     radixPass( s0, A0, s,0, n0, K );
	        
	     int p = 0, k = 0;
	     int t = (n0-n1);
	     while( t != n12 && p != n0 )
	     {
	    	 int i = getI( A12, t, n0 ); // pos of current offset 12 suffix
	    	 int j = A0[ p ];           // pos of current offset 0  suffix
	            
	    	 if (A12[t] < n0 ? 
	    			 	leq(s[i],       s12[A12[t] + n0], s[j],       s12[j/3]) :
	                    leq(s[i],s[i+1],s12[A12[t]-n0+1], s[j],s[j+1],s12[j/3+n0]))
	    	 {// suffix from A12 is smaller 
	    		 A[ k++ ] = i;
	    		 t++;
	    	 }else{ 
	    		 A[ k++ ] = j;
	    		 p++;
	    	 }  
	     } 
	        
	     while( p < n0 )
	    	 A[ k++ ] = A0[ p++ ];
	     while( t < n12 )
	    	 A[ k++ ] = getI( A12, t++, n0 ); 
	        
	     s12 = null;
	     A12 = null;
	     s0 = null;
	     A0 = null;
	 }
	
	
	// stably sort source[0..n-1] to destination[0..n-1] with keys in 0..K from s
    // uses counting radix sort
    private static void radixPass(int[] source, int[] destination, int[] s, int offset, int n, int K) 
    { 
      int[] bin = new int[K+1]; 
      
      // initialize counter array
      for (int i = 0;  i <= K;  i++)
    	  bin[i] = 0; 
      
      // count occurrences
      for (int i = 0;  i < n;  i++) 
    	  bin[s[source[i] + offset]]++;    
      
      //give each bin its name
      for (int i = 0, sum = 0;  i <= K;  i++) { 
         int temp = bin[i];  
         bin[i] = sum;  
         sum += temp;
      }
      
      //sort it to the array
      for (int i = 0;  i < n;  i++){
    	  destination[bin[s[source[i] + offset]]] = source[i];
    	  bin[s[source[i]+ offset]]++;
      }
      
      bin = null;
    }
    
    
    private static int getI( int [ ] A12, int t, int n0 )
    {
    	return A12[t] < n0 ? A12[t] * 3 + 1 : (A12[t] - n0) * 3 + 2;
    }
    
    // lexic. order for pairs
    private static boolean leq( int a1, int a2, int b1, int b2 )
    {	return a1 < b1 || a1 == b1 && a2 <= b2; }
    
    // lexic. order for triples
    private static boolean leq( int a1, int a2, int a3, int b1, int b2, int b3 )
    { 	return a1 < b1 || a1 == b1 && leq( a2, a3,b2, b3 ); }
    
    
    public static void main(String[] args) throws IOException {	
    	BufferedWriter output = null;
		File file = new File( "/Users/Chen/Documents/BWT.txt");
		output = new BufferedWriter(new FileWriter(file));
		try {
			   RandomAccessFile aFile = new RandomAccessFile("/Users/Chen/Documents/CS courses/CS131/workspace/Aho-Corasick Algorithm/src/ahoCoroasickAlgorithm/hwk1-test-cases/hu-chr1.txt", "r");
			   FileChannel inChannel = aFile.getChannel();
			   MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
	   
			   buffer.load();
			   CharBuffer charBuffer = StandardCharsets.US_ASCII.decode(buffer);
			   String read = charBuffer.toString();
			   
			   long startTime = System.nanoTime();   
			   output.write(buildBWT(read));
			   long endTime = System.nanoTime();
			   long duration = (endTime - startTime)/1000000000;
			   
			   System.out.println(duration);
			   buffer.clear(); 
			   inChannel.close();
			   aFile.close();
	        
		   } catch (IOException ioe) {
			   ioe.printStackTrace();
		   }finally {
	            if ( output != null ) output.close();
	       }

    }

}
