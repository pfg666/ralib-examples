package main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.data.SymbolicDataValue.Constant;
import de.learnlib.ralib.data.util.SymbolicDataValueGenerator;
import de.learnlib.ralib.data.util.SymbolicDataValueGenerator.ConstantGenerator;
import de.learnlib.ralib.oracles.io.IOOracle;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.sul.CanonizingSULOracle;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import io.IOLearningExperiment;
import matlab.v2.LSMV2SUL;
import net.automatalib.words.Word;

public class Main {
	
	private static Random rand = new Random(0);

	public static void main(String[] args) throws IllegalArgumentException, IllegalStateException, InterruptedException, ExecutionException, TimeoutException {
		bindParameters();
		//testRandomly();
		runLearningExperiment(
				3 // test length bound
				);
	}
	
	
	// experiment with which parameters to fix such that learning is still successful.
	// so far it seems that 4 parameters is the upper limit if we don't use typing
	/**
	 * bind parameters of the SUL input to concrete values
	 */
	public static void bindParameters() {
		LinkedHashMap<Integer, Integer> boundParam = new LinkedHashMap<Integer,Integer>();
		//boundParam.put(0, 0); // state
		//boundParam.put(1, 0); // direction
		//boundParam.put(2, 0); // lcr
		boundParam.put(3, 0); // b1
		boundParam.put(4, 0); // b2
		//boundParam.put(5, 0); // lcr
		boundParam.put(6, 0); // b4
		boundParam.put(7, 0); // b5
		boundParam.put(8, 0); // b6
		LSMV2SUL.bindInputParameters(boundParam);
	}
	
	public static void runLearningExperiment(int testLengthBound) throws ExecutionException, InterruptedException, TimeoutException {
		Constants constants = new Constants();
		ConstantGenerator cGen = new SymbolicDataValueGenerator.ConstantGenerator();
		Constant c0 = cGen.next(LSMV2SUL.INT_TYPE);
		Constant c1 = cGen.next(LSMV2SUL.INT_TYPE);
		Constant c2 = cGen.next(LSMV2SUL.INT_TYPE);
		constants.put(c0, new DataValue(LSMV2SUL.INT_TYPE, 0));
		constants.put(c1, new DataValue(LSMV2SUL.INT_TYPE, 1));
		constants.put(c2, new DataValue(LSMV2SUL.INT_TYPE, 2));
		IOLearningExperiment experiment = new IOLearningExperiment();
		LSMV2SUL sul = new LSMV2SUL();
		IntegerEqualityTheory lCRTheory = new IntegerEqualityTheory(LSMV2SUL.LANE_CHANGE_REQUEST_TYPE);
		IntegerEqualityTheory otherTheory = new IntegerEqualityTheory(LSMV2SUL.INT_TYPE);
		
		
		Map<DataType, Theory> theories = new LinkedHashMap<>();
		theories.put(LSMV2SUL.INT_TYPE, otherTheory);
		theories.put(LSMV2SUL.LANE_CHANGE_REQUEST_TYPE, lCRTheory);
		SimpleConstraintSolver solver = new SimpleConstraintSolver();
		ParameterizedSymbol[] actionSymbols = sul.getActionSymbols();
		experiment.setMaxDepth(testLengthBound);
		experiment.runIOLearningExperiment(sul, theories, constants, solver, actionSymbols, LSMV2SUL.ERROR);
	}

	public static void testRandomly() throws ExecutionException, InterruptedException, TimeoutException {
		LSMV2SUL sul = new LSMV2SUL();
		IOOracle sulOracle = new CanonizingSULOracle(sul, LSMV2SUL.ERROR, t -> t);
		
		for (int i=0; i<1000; i ++) {
			Word<PSymbolInstance> output = sulOracle.trace(Word.fromSymbols(
					randInput(3),
					randInput(3),
					randInput(3)
					));
		}
	}
	
	
	public static PSymbolInstance randInput(int valBound) {
		Object [] vals = new Object[LSMV2SUL.main_v2.getArity()];
		for (int i=0; i<vals.length; i++) {
			vals[i] = rand.nextInt(valBound);
		}
		return LSMV2SUL.input(vals);
	}
}
