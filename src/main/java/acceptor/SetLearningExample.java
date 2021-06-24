package acceptor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.ParameterizedSymbol;

/**
 * An example of how to learn when your SUL is accepts/rejects data words and is implemented as a DataWordOracle.
 * Note: we had to implement the Equivalence oracle ourselves for this case.
 * 
 * You should replace the SetDataWordOracle by your adapter. 
 * You may need a smarter/more effective EquivalenceOracle.
 */
public class SetLearningExample {
	public static void main( String args [] ) {
		final Map<DataType, Theory> teachers = new LinkedHashMap<>();
		teachers.put(SetDataWordOracle.INT_TYPE, new IntegerEqualityTheory(SetDataWordOracle.INT_TYPE));

		ConstraintSolver solver = new SimpleConstraintSolver();
		Constants consts = new Constants();
		SetDataWordOracle membershipOracle = new SetDataWordOracle(3);
		ParameterizedSymbol[] actionSymbols = membershipOracle.getInputSymbols(); 
		
		AcceptorLearningExperiment learningExperiment = new AcceptorLearningExperiment();
		Random random = new Random(0);
		
		IOEquivalenceOracle equivalenceOracle = new AcceptorBasicEquivalenceOracle(membershipOracle, teachers, consts, Arrays.asList(actionSymbols), solver, 1000, random);
		
		learningExperiment.runAcceptorLearningExperiment(membershipOracle, teachers, consts, solver, actionSymbols, equivalenceOracle);
	}
	
}
