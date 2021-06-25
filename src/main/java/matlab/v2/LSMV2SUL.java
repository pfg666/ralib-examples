//should be saved into FIFOSUL

package matlab.v2;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.mathworks.engine.MatlabEngine;
import com.mathworks.matlab.types.Struct;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.sul.DataWordSUL;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.OutputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import matlab.MatlabWrapper;

public class LSMV2SUL extends DataWordSUL {

	
	public static final DataType INT_TYPE = new DataType("int", Integer.class);
	public static final DataType LANE_CHANGE_REQUEST_TYPE = new DataType("lcr", Integer.class);
	
	public static final int NUM_PARAM = 9;
	
	public static final PSymbolInstance input(Object ... arguments) {
		DataValue [] dv = new DataValue[arguments.length];
		for (int i=0; i<arguments.length; i++) {
			dv[i] = new DataValue(INT_TYPE, arguments[i]);
		}
		
		return new PSymbolInstance(main_v2, dv);
	}

//    public static final DataType STRUCT_TYPE = 
//            new DataType("int[]", Integer.class);   
	/*
	 * //was not here before
	 * 
	 * public static final DataType STRING_TYPE = new DataType("string",
	 * String.class);
	 * 
	 * //finish here
	 */
	
	
	
	
	public static ParameterizedSymbol main_v2 = // public static final ParameterizedSymbol OFFER =
			new InputSymbol("main_v2", new DataType[] { INT_TYPE, INT_TYPE, INT_TYPE, INT_TYPE, 
					INT_TYPE, INT_TYPE, INT_TYPE, INT_TYPE, INT_TYPE });
	// new InputSymbol("offer", new DataType[]{INT_TYPE});
	

	private static Map<Integer, Integer> boundParams = new LinkedHashMap<>();

	private static final ParameterizedSymbol generateInputSymbol(Map<Integer, Integer> boundParam) {
		int numParam = NUM_PARAM - boundParam.size();
		DataType [] typeArr = new DataType[numParam];
		Arrays.fill(typeArr, INT_TYPE);
		return new InputSymbol("main_v2", typeArr);
	}
	
	public static final void bindInputParameters(Map<Integer, Integer> fixedParams) {
		LSMV2SUL.boundParams = fixedParams;
		main_v2 = generateInputSymbol(fixedParams); 
	}
	
	public final ParameterizedSymbol[] getInputSymbols() {
		return new ParameterizedSymbol[] { main_v2 };
	}

	public static final ParameterizedSymbol ERROR = new OutputSymbol("_io_err", new DataType[] {});

	public static final ParameterizedSymbol OUTPUT = new OutputSymbol("_out", new DataType[] { INT_TYPE });

	public static final ParameterizedSymbol OK = new OutputSymbol("_ok", new DataType[] {});

	public static final ParameterizedSymbol NOK = new OutputSymbol("_not_ok", new DataType[] {});

	public final ParameterizedSymbol[] getActionSymbols() {
		return new ParameterizedSymbol[] { main_v2, OUTPUT, OK, NOK, ERROR }; // could insert also OUTPUT
	}

	public static com.mathworks.engine.MatlabEngine matEng;
	
	

	// private FIFOExample fifo;
	// private List<Integer> fifo;
	// private int capacity;
	private List<Integer> stNew;
	private double state;
	private double direction;
	private double laneChangeRequest;
	private double b1;
	private double b2;
	private double laneChngReq;
	private double b4;
	private double b5;
	private double b6;
	
	private Integer [] getParameterValues(PSymbolInstance input) {
		Integer [] values = new Integer [NUM_PARAM];
		int inputParamIdx=0;
		for (Integer i=0; i<NUM_PARAM; i++ ) {
			if (boundParams.containsKey(i)) {
				values[i] = boundParams.get(i);
			} else {
				values[i] = (Integer) input.getParameterValues()[inputParamIdx++].getId();
			}
		}
		
		return values;
	}
	

	// here should go the definition of fifo as list of elements
	// private List<Integer> fifo;
	// private int varargin;

	public LSMV2SUL() throws ExecutionException, InterruptedException, TimeoutException {
		matEng = MatlabWrapper.getInstance().connect();
		MatlabEngine.startMatlab();
	}

	@Override
	public void pre() {
		try {
			// matEng.startMatlab();
//    		System.out.println("start PRE() function");
//        	System.out.println("initialize countReset(1)");
			String matlabResource = LSMV2SUL.class.getResource("/matlab").getFile();
			matEng.eval(
					"cd '" + matlabResource + "'");
//			System.out.println("cd matlab_files/mainv2_simplified"); 
			matEng.eval("[state, direction, laneChangeRequest, b1, b2] = initialize");
//        	System.out.println("initialize");
			this.state = matEng.getVariable("state");
//        	System.out.println(this.state);
			this.direction = matEng.getVariable("direction");
//        	System.out.println(this.direction);
			this.laneChangeRequest = matEng.getVariable("laneChangeRequest");
//        	System.out.println(this.laneChangeRequest);
			this.b1 = matEng.getVariable("b1");
//        	System.out.println(this.b1);
			this.b2 = matEng.getVariable("b2");
//        	System.out.println("b2");
			// fifo = new FIFOExample(capacity);
			// here is defined the fifo vector

			/*
			 * int laneChngReq = laneChngReq; int b4 = b4; int b5 = b5; int b6 = b6; return
			 * wUpdate(laneChngReq,b4,b5,b6); // it is not wUpdate , but i don't think is
			 * even needed
			 */
			
			countResets(1);
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception");
			System.out.println(e.getMessage());
		} catch (ExecutionException e) {
			System.out.println("Execution Exception");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void post() {
		try {
//    	System.out.println("start POST() function");
			// fifo = null;
			// delete the function
			matEng.eval("clear");
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception");
			System.out.println(e.getMessage());
			throw new IllegalStateException("Interrupted Exception");
		} catch (ExecutionException e) {
			System.out.println("Cause of the exception: " + e.getCause());
			System.out.println(e.getMessage());
			System.out.println("Execution Exception");
//        	System.out.println(e.getMessage());
			throw new IllegalStateException("Execution Exception");
		}
	}

	private PSymbolInstance createOutputSymbol(Object x) {

		if (x instanceof Exception) {
			return new PSymbolInstance(ERROR);
		} else if (x == null) {
			return new PSymbolInstance(NOK);
		} else if ((int) x == 2) { // DON*T THINK IS CASED PROPERLY
			return new PSymbolInstance(NOK);
		} else {
			assert (null != x);
			return new PSymbolInstance(OUTPUT, new DataValue(INT_TYPE, x));
		}

	}

	@Override
	public PSymbolInstance step(PSymbolInstance i) {
		try {
//    			System.out.println("start STEP() function");
			countInputs(1);
//    			System.out.println("countInputs(1)");

			Struct st = new Struct("state", this.state, "direction", this.direction, "laneChangeRequest",
					this.laneChangeRequest, "b1", this.b1, "b2", this.b2, "laneChngReq", laneChngReq, "b4", b4, "b5",
					b5, "b6", b6);
//    			System.out.println("Struct st");
//    			System.out.println(st);

			Integer[] vals = getParameterValues(i);
			StringBuilder evalBuilder = new StringBuilder().append("st = main_v2(");
			if (vals.length > 0) {
				int idx;
				for (idx=0; idx< vals.length-1; idx++) {
					evalBuilder.append(vals[idx]).append(", ");
				}
				evalBuilder.append(vals[idx]);
			}
			evalBuilder.append(")");
			
			matEng.eval(evalBuilder.toString());
//    			System.out.println("computed main_v2");

			st = matEng.getVariable("st");
//    			System.out.println(st);

			this.state = (double) st.get("state");
//    			System.out.println("this.state");
//    			System.out.println(this.state);
			this.direction = (double) st.get("direction");
//            	System.out.println("this.direction");
//            	System.out.println(this.direction);
			this.laneChangeRequest = (double) st.get("laneChangeRequest");
//            	System.out.println("this.laneChangeRequest");
//            	System.out.println(this.laneChangeRequest);
			this.b1 = (double) st.get("b1");
//            	System.out.println("this.b1");
//            	System.out.println(this.b1);
			this.b2 = (double) st.get("b2");
//            	System.out.println("this.b2");
//            	System.out.println(this.b2);

			// double x = this.state;
			int x = (int) this.state; // check git how to not use a int/double
			return createOutputSymbol(x);

			/*
			 * 
			 * if (i.getBaseSymbol().equals(PUSH)) { //
			 * System.out.println("start PUSH() function"); //
			 * System.out.println("entered in if cycle"); //Object x = fifo.offer((Integer)
			 * i.getParameterValues()[0].getId()); // cosa fa questa linea ????
			 * 
			 * //matEng.feval(1,push, use string ,(Integer)
			 * i.getParameterValues()[0].getId()); // System.out.
			 * println("below shoud have the eval with push(sys and integer value)");
			 * 
			 * // boolean x = matEng.feval("push",(Integer)
			 * i.getParameterValues()[0].getId()); // matEng.eval("out = push(" + (Integer)
			 * i.getParameterValues()[0].getId() + ")");
			 * matEng.eval("outt = push("+(Integer) i.getParameterValues()[0].getId()+")");
			 * // System.out.println("have done push"); //
			 * System.out.println("get variable out"); boolean x =
			 * matEng.getVariable("outt"); //or true or false should be a boolean //
			 * System.out.println("x"); //output true if is possible to push and false if is
			 * not possible
			 * 
			 * return createOutputSymbol(x); } else if (i.getBaseSymbol().equals(POP)) { //
			 * System.out.println("start POP() function"); //Object x = fifo.poll();
			 * 
			 * matEng.eval("first_element = pop()"); // receive as output the queue, but we
			 * only need the name to be saved // System.out.println("pop eval"); Integer x =
			 * matEng.getVariable("first_element"); // System.out.println("x"); //
			 * System.out.println(x);
			 * 
			 * //int x = matEng.feval("pop", "sys"); // System.out.println("x"); //
			 * matEng.eval("first_element = pop(sys)"); // int x =
			 * matEng.getVariable("first_element"); //maybe cast another time to integer
			 * according to line 83
			 * 
			 * return createOutputSymbol(x);
			 * 
			 * }
			 * 
			 * else { throw new
			 * IllegalStateException("i must be instance of poll or offer"); }
			 */
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception");
			System.out.println(e.getMessage());
			throw new IllegalStateException("Interrupted Exception");
		} catch (ExecutionException e) {
			System.out.println("Cause of the exception: " + e.getCause());
			System.out.println(e.getMessage());
			System.out.println("Execution Exception");
//        	System.out.println(e.getMessage());
			throw new IllegalStateException("Execution Exception");
		}

	}
}
