package io;
import java.util.LinkedHashMap;
import java.util.Map;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.ParameterizedSymbol;

/**
 * An example of how to learn when your SUL is reactive (on processing an input, responds with an output).
 * 
 * Your should replace the FIFOSUL by your adapter.
 */
public class FIFOLearningExample {
	
	public static void main( String args [] ) {
		final Map<DataType, Theory> teachers = new LinkedHashMap<>();
		teachers.put(FIFOSUL.INT_TYPE, new IntegerEqualityTheory(FIFOSUL.INT_TYPE));

		ConstraintSolver solver = new SimpleConstraintSolver();
		Constants consts = new Constants();
		FIFOSUL sul = new FIFOSUL(3);
		ParameterizedSymbol[] actionSymbols = sul.getActionSymbols(); 
		
		IOLearningExperiment learningExperiment = new IOLearningExperiment();
		learningExperiment.runIOLearningExperiment(sul, teachers, consts, solver, actionSymbols, FIFOSUL.ERROR);
	}
}
