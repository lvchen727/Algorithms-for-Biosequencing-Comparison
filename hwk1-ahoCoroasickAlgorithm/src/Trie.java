package ahoCoroasickAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The Trie class is for implementing Aho Corasick algorithm.
 * @author Chen
 *
 */

public class Trie {
	private State rootState = new State();
	private boolean failureStatesConstructed = false;
	

	/**
     * trie construction function
     * @param keyword
     */
    public void addPattern(String pattern, int patternLine, int s)
    {
        if (pattern == null || pattern.length() == 0)
        {
            return;
        }
        State currentState = this.rootState;
        for (Character character : pattern.toCharArray())
        {
            currentState = currentState.addState(character);
        }
        Emit e = new Emit(pattern, patternLine, s, (1-pattern.length()), 0);  
        currentState.addEmit(e);
        //System.out.println(currentState.emit());
    }
    
    /**
     * Aho Coroasick matching function
     * @param text
     * @return emit the output 
     */
    public Collection<Emit> parseText(String text)
    {	
    	checkForConstructedFailureStates();
        int position = 1;
        State currentState = this.rootState;
        List<Emit> collectedEmits = new ArrayList<Emit>();
        for (Character character : text.toCharArray())
        {
        	currentState = getState(currentState, character);
        	storeEmits(position, currentState, collectedEmits);
        	++position;
        }
        
        /**
         * sort the output by t, p and s
         */
        Comparator<Emit> comparator = new Comparator<Emit>() {
            public int compare(Emit c1, Emit c2) {
            	int c ;
            	c = c1.getStart() - c2.getStart();
            	if(c == 0)
            		c = c1.getPatternLines()-c2.getPatternLines();
            	if(c == 0)
            		c = c1.getStrandInfos() - c2.getStrandInfos();
                return c;
            }
        };
  
        Collections.sort(collectedEmits, comparator);
        return collectedEmits;
    }
    
    
    /**
     * Construction of goto function
     * @param currentState 
     * @param character 
     * @return next state
     */
    private static State getState(State currentState, Character character)
    {
        State newCurrentState = currentState.nextState(character); 
        while (newCurrentState == null) // if fail, go to 
        {
            currentState = currentState.failure();
            newCurrentState = currentState.nextState(character);
        }
        return newCurrentState;
    }
    
    private void checkForConstructedFailureStates()
    {
        if (!this.failureStatesConstructed)
        {
            constructFailureStates();
        }
    }

    /**
     * Construction of failure function
     */
    private void constructFailureStates()
    {
        Queue<State> queue = new LinkedBlockingDeque<State>();

        for (State depthOneState : this.rootState.getStates())
        {
            depthOneState.setFailure(this.rootState);
            queue.add(depthOneState);
        }
        this.failureStatesConstructed = true;

        while (!queue.isEmpty())
        {
            State currentState = queue.remove();
            for (Character transition : currentState.getTransitions())
            {
                State targetState = currentState.nextState(transition);
                queue.add(targetState);

                State traceFailureState = currentState.failure();
                while (traceFailureState.nextState(transition) == null)
                {
                    traceFailureState = traceFailureState.failure();
                }
                State newFailureState = traceFailureState.nextState(transition);
                targetState.setFailure(newFailureState);
                targetState.addEmit(newFailureState.emit());//update output function
            }
        }
    }

    /**
     * This function is to store the outputs.
     * @param currentState position in corpus file
     * @param currentState 
     * @param collectedEmits 
     */
    private static void storeEmits(int position, State currentState, List<Emit> collectedEmits)
    {
        Collection<Emit> emits = currentState.emit();
        if (emits != null && !emits.isEmpty())
        {
            for (Emit emit : emits)
            {
                collectedEmits.add(new Emit(emit.getPatterns(),emit.getPatternLines(),emit.getStrandInfos(),  (position - emit.getPatterns().length() + 1), position));
            }
        }
    }
    
}
