package acceptor;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.learnlib.api.Query;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.words.Word;

public class SetDataWordOracle implements DataWordOracle {

    public static final DataType INT_TYPE = 
            new DataType("int", Integer.class);    

    public static final ParameterizedSymbol ADD = 
            new InputSymbol("add", new DataType[]{INT_TYPE});
    
    public static final ParameterizedSymbol REMOVE = 
            new InputSymbol("remove", new DataType[]{INT_TYPE});

    
    public final ParameterizedSymbol[] getInputSymbols() {
        return new ParameterizedSymbol[] { ADD, REMOVE };
    }
        

    private int capacity;

    public SetDataWordOracle(int capacity) {
    	this.capacity = capacity;
    	
    }

	@Override
	public void processQueries(Collection<? extends Query<PSymbolInstance, Boolean>> queries) {
		for (Query<PSymbolInstance, Boolean> query : queries) {
			Word<PSymbolInstance> inputs = query.getInput();
			boolean accepts = acceptsInputs(inputs);
			query.answer(accepts);
		}
	}
	
	private boolean acceptsInputs(Word<PSymbolInstance> inputs) {
		Set<Integer> set = new HashSet<>();
		for (PSymbolInstance input : inputs) {
			if (input.getBaseSymbol().equals(ADD)) {
				Integer value = (Integer) input.getParameterValues()[0].getId();
				if (set.size() < capacity && !set.contains(value)) {
					set.add(value);
				} else {
					return false;
				}
			} else if (input.getBaseSymbol().equals(REMOVE)) {
				Integer value = (Integer) input.getParameterValues()[0].getId();
				if (set.contains(value)) {
					set.remove(value);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
