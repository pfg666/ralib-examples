package io;

import de.learnlib.api.SULException;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.sul.DataWordSUL;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.OutputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class BooleanFIFOSUL extends DataWordSUL {

    public static final DataType INT_TYPE = 
            new DataType("int", Integer.class);    

    public static final ParameterizedSymbol POLL = 
            new InputSymbol("poll", new DataType[]{});
    
    public static final ParameterizedSymbol OFFER = 
            new InputSymbol("offer", new DataType[]{INT_TYPE});

    
    public final ParameterizedSymbol[] getInputSymbols() {
        return new ParameterizedSymbol[] { POLL, OFFER };
    }
        
    public static final ParameterizedSymbol ERROR = 
            new OutputSymbol("_io_err", new DataType[]{});

    public static final ParameterizedSymbol OUTPUT = 
            new OutputSymbol("_out", new DataType[]{INT_TYPE});
    
    public static final ParameterizedSymbol OK = 
            new OutputSymbol("_ok", new DataType[]{});
        
    public static final ParameterizedSymbol NOK = 
            new OutputSymbol("_not_ok", new DataType[]{});

    public final ParameterizedSymbol[] getActionSymbols() {
        return new ParameterizedSymbol[] { POLL, OFFER, OUTPUT, OK, NOK, ERROR };
    }


    private BooleanFIFOExample fifo;
    private int capacity;

    
    public BooleanFIFOSUL(int capacity) {
    	this.capacity = capacity;
    }

    @Override
    public void pre() {
        countResets(1);
        fifo = new BooleanFIFOExample(capacity);
    }

    @Override
    public void post() {
        fifo = null;
    }

    private PSymbolInstance createOutputSymbol(Boolean x) {
    	return new PSymbolInstance(OUTPUT, new DataValue(INT_TYPE, x ? 1 : 0));
    }

    @Override
    public PSymbolInstance step(PSymbolInstance i) throws SULException {
        countInputs(1);
        if (i.getBaseSymbol().equals(OFFER)) {
        	Integer value = (Integer) i.getParameterValues()[0].getId();
        	if (value > 1) {
        		return new PSymbolInstance(NOK);
        	}
        	Boolean bValue = value == 0 ? Boolean.FALSE : Boolean.TRUE; 
            boolean x = fifo.offer(bValue);
            if (x) {
            	return new PSymbolInstance(OK);
            } else {
            	return new PSymbolInstance(NOK);
            }
        } else if (i.getBaseSymbol().equals(POLL)) {
            Boolean x = fifo.poll();
            if (x == null) {
            	return new PSymbolInstance(NOK);
            } else {
            	return createOutputSymbol(x);
            }
        } else {
            throw new IllegalStateException("i must be instance of poll or offer");
        }
    }
}