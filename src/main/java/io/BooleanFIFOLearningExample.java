package io;

import java.util.LinkedHashMap;
import java.util.Map;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.data.SymbolicDataValue.Constant;
import de.learnlib.ralib.data.util.SymbolicDataValueGenerator;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class BooleanFIFOLearningExample {
	public static void main( String args [] ) {
		final Map<DataType, Theory> teachers = new LinkedHashMap<>();
		teachers.put(FIFOSUL.INT_TYPE, new IntegerEqualityTheory(FIFOSUL.INT_TYPE));

		ConstraintSolver solver = new SimpleConstraintSolver();
		Constants consts = new Constants();
		SymbolicDataValueGenerator.ConstantGenerator constsGen = new SymbolicDataValueGenerator.ConstantGenerator();
		Constant cTrue = constsGen.next(BooleanFIFOSUL.INT_TYPE);
		Constant cFalse = constsGen.next(BooleanFIFOSUL.INT_TYPE);
		consts.put(cFalse, DataValue.ZERO(BooleanFIFOSUL.INT_TYPE, Integer.class));
		consts.put(cTrue, DataValue.ONE(BooleanFIFOSUL.INT_TYPE, Integer.class));
		BooleanFIFOSUL sul = new BooleanFIFOSUL(2);
		ParameterizedSymbol[] actionSymbols = sul.getActionSymbols(); 
		
		IOLearningExperiment learningExperiment = new IOLearningExperiment();
		learningExperiment.runIOLearningExperiment(sul, teachers, consts, solver, actionSymbols, FIFOSUL.ERROR);
	}
}
