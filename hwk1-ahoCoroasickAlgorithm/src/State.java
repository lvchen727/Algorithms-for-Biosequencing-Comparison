package ahoCoroasickAlgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The State class was used in the trie construction.
 * @author Chen
 *
 */

public class State {

	protected final int depth;
	protected final State rootState;
	private State failure = null;
	private Set<Emit> emits = null;
	private Map<Character, State> success = new TreeMap<Character, State>();
	
	public State()
	{
		this(0);
	}
	
	public State(int depth)
	{
		this.depth = depth;
	    this.rootState = depth == 0 ? this : null;
	}
	   
	public int getDepth()
	{
		return this.depth;
	}

	public void addEmit(Emit emit)
    {
        if (this.emits == null)
        {
            this.emits = new TreeSet<Emit>();
        }
        this.emits.add(emit);     
    }

	public void addEmit(Collection<Emit> emits)
    {
        for (Emit emit : emits)
        {
            addEmit(emit);
        }
    }
	
	public Collection<Emit> emit()
    {
       return this.emits == null ? Collections.<Emit>emptyList() : this.emits;
    }
	
    public State failure()
    {
        return this.failure;
    }

    public void setFailure(State failState)
    {
        this.failure = failState;
    }

    private State nextState(Character character, boolean ignoreRootState)
    {
        State nextState = this.success.get(character);
        if (!ignoreRootState && nextState == null && this.rootState != null)
        {
            nextState = this.rootState;
        }
        return nextState;
    }

    public State nextState(Character character)
    {
        return nextState(character, false);
    }

    public State nextStateIgnoreRootState(Character character)
    {
        return nextState(character, true);
    }

    public State addState(Character character)
    {
        State nextState = nextStateIgnoreRootState(character);
        if (nextState == null)
        {
            nextState = new State(this.depth + 1);
            this.success.put(character, nextState);
        }
        return nextState;
    }

    public Collection<State> getStates()
    {
        return this.success.values();
    }

    public Collection<Character> getTransitions()
    {
        return this.success.keySet();
    }
}
