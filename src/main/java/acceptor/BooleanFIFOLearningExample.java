package acceptor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.data.SymbolicDataValue.Constant;
import de.learnlib.ralib.data.util.SymbolicDataValueGenerator;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class BooleanFIFOLearningExample {
	public static void main( String args [] ) {
		final Map<DataType, Theory> teachers = new LinkedHashMap<>();
		teachers.put(BooleanFIFODataWordOracle.INT_TYPE, new IntegerEqualityTheory(BooleanFIFODataWordOracle.INT_TYPE));

		ConstraintSolver solver = new SimpleConstraintSolver();
		Constants consts = new Constants();
//		SymbolicDataValueGenerator.ConstantGenerator constsGen = new SymbolicDataValueGenerator.ConstantGenerator();
//		Constant cTrue = constsGen.next(BooleanFIFODataWordOracle.INT_TYPE);
//		Constant cFalse = constsGen.next(BooleanFIFODataWordOracle.INT_TYPE);
//		consts.put(cFalse, DataValue.ZERO(BooleanFIFODataWordOracle.INT_TYPE, Integer.class));
//		consts.put(cTrue, DataValue.ONE(BooleanFIFODataWordOracle.INT_TYPE, Integer.class));
		
		BooleanFIFODataWordOracle membershipOracle = new BooleanFIFODataWordOracle(4);
		ParameterizedSymbol[] actionSymbols = membershipOracle.getInputSymbols(); 
		
		AcceptorLearningExperiment learningExperiment = new AcceptorLearningExperiment();
		Random random = new Random(1);
		
		IOEquivalenceOracle equivalenceOracle = new AcceptorBasicEquivalenceOracle(membershipOracle, teachers, consts, Arrays.asList(actionSymbols), solver, 1000, random);
		
		learningExperiment.runAcceptorLearningExperiment(membershipOracle, teachers, consts, solver, actionSymbols, equivalenceOracle);
	}
}
