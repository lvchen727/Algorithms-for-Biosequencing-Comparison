package ahoCoroasickAlgorithm;


/**
 * The Emit class is built to output the final results.
 * @author Chen
 *
 */
public class Emit extends Interval implements Intervalable{
	private int patternLines;
	private int strandInfos;
	private String patterns = null;

	public Emit(String patterns, int patternLines, int strandInfos, int start, int end) {
		super(start,end);
		this.patternLines = patternLines;
		this.strandInfos = strandInfos;
		this.patterns = patterns;
	}
	
	public int getPatternLines() {
		return patternLines;
	}
	
	public int getStrandInfos() {
		return strandInfos;
	}
	
	public String getPatterns() {
		return patterns;
	}
	

	public void setPatternLines(int patternLines) {
		this.patternLines = patternLines;
	}
	
	public void setStrandInfos(int strandInfos) {
		this.strandInfos = strandInfos;
	}
	
	public void setPatterns(String patterns) {
		this.patterns = patterns;
	}
	
	
	
	public int getStart() {
		return super.getStart();
	}

	public int getEnd() {
		return super.getEnd();
	}



	@Override
	public String toString() {
		return super.getStart() + " "
				+ patternLines + " " + strandInfos;
	}
	
	
}
