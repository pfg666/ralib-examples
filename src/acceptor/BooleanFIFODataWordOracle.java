package acceptor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.learnlib.api.Query;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.words.Word;

public class BooleanFIFODataWordOracle implements DataWordOracle  {


    public static final DataType INT_TYPE = 
            new DataType("int", Integer.class);    

    public static final ParameterizedSymbol POLL = 
            new InputSymbol("poll", new DataType[]{INT_TYPE});
    
    public static final ParameterizedSymbol OFFER = 
            new InputSymbol("offer", new DataType[]{INT_TYPE});

	private int capacity;

    
    public final ParameterizedSymbol[] getInputSymbols() {
        return new ParameterizedSymbol[] { POLL, OFFER };
    }
        
    BooleanFIFODataWordOracle(int capacity) {
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
		List<Boolean> fifo = new LinkedList<>();
		for (PSymbolInstance input : inputs) {
			if (input.getBaseSymbol().equals(OFFER)) {
				Integer value = (Integer) input.getParameterValues()[0].getId();
				if (value > 1) {
					return false;
				}
				Boolean bValue = value.equals(1);
				
				if (fifo.size() < capacity) {
					fifo.add(bValue);
				} else {
					return false;
				}
			} else if (input.getBaseSymbol().equals(POLL)) {
				Integer value = (Integer) input.getParameterValues()[0].getId();
				if (value > 1) {
					return false;
				}
				Boolean bValue = value.equals(1);
				
				if (fifo.size() > 0 && fifo.get(fifo.size()-1).equals(bValue)) {
					fifo.remove(fifo.size()-1);
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
