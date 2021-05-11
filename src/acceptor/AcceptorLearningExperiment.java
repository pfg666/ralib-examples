package acceptor;

import java.util.Collection;
import java.util.Map;

import de.learnlib.api.EquivalenceOracle;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.learning.Hypothesis;
import de.learnlib.ralib.learning.RaStar;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.oracles.SDTLogicOracle;
import de.learnlib.ralib.oracles.SimulatorOracle;
import de.learnlib.ralib.oracles.TreeOracleFactory;
import de.learnlib.ralib.oracles.io.IOOracle;
import de.learnlib.ralib.oracles.mto.MultiTheorySDTLogicOracle;
import de.learnlib.ralib.oracles.mto.MultiTheoryTreeOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.sul.BasicSULOracle;
import de.learnlib.ralib.sul.SimulatorSUL;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.classanalyzer.SpecialSymbols;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class AcceptorLearningExperiment {
	

	public AcceptorLearningExperiment() {
	}
	
	
	public static MultiTheoryTreeOracle createSimulatorMTO(RegisterAutomaton regAutomaton, Map<DataType, Theory> teachers, Constants consts, ConstraintSolver solver) {
	    DataWordOracle hypOracle = new SimulatorOracle(regAutomaton);
	    SimulatorSUL hypDataWordSimulation = new SimulatorSUL(regAutomaton, teachers, consts);
	    IOOracle hypTraceOracle = new BasicSULOracle(hypDataWordSimulation, SpecialSymbols.ERROR);  
	    return new MultiTheoryTreeOracle(hypOracle, hypTraceOracle,  teachers, consts, solver);
    }
	
	/**
	 * Launches a learning experiment using the oracles provided
	 * 
	 * @param membershipOracle 
	 * @param teachers         theories fully configured
	 * @param consts           constants fully defined
	 * @param solver           constraint solver to use
	 * @param actionSymbols    inputs and output symbols applied during the learning
	 *                         process
	 * @param equivalenceOracle 
	 */
	public void runAcceptorLearningExperiment(DataWordOracle membershipOracle, Map<DataType, Theory> teachers, Constants consts,
			ConstraintSolver solver, ParameterizedSymbol[] actionSymbols, IOEquivalenceOracle equivalenceOracle) {
		MultiTheoryTreeOracle mto = new MultiTheoryTreeOracle(membershipOracle, null,  teachers, consts, solver);
		SDTLogicOracle slo = new MultiTheorySDTLogicOracle(consts, solver);

		TreeOracleFactory hypFactory = (RegisterAutomaton hyp) -> createSimulatorMTO(hyp, teachers,
				consts, solver);
		
		RaStar rastar = new RaStar(mto, hypFactory, slo, consts, teachers, solver, actionSymbols);
		rastar.learn();
		int check = 0;
		while (true && check < 100) {
			check++;
			rastar.learn();
			Hypothesis hyp = rastar.getHypothesis();

			DefaultQuery<PSymbolInstance, Boolean> ce = equivalenceOracle.findCounterExample(hyp, null);

			if (ce == null) {
				break;
			}

			rastar.addCounterexample(ce);
		}
		
		RegisterAutomaton hyp = rastar.getHypothesis();
		

		System.out.println("LAST:------------------------------------------------");
		System.out.println("FINAL HYP: " + hyp);
	}
}
