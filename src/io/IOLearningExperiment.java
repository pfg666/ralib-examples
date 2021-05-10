package io;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import de.learnlib.oracles.DefaultQuery;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.equivalence.HypVerifier;
import de.learnlib.ralib.equivalence.IOCounterExampleLoopRemover;
import de.learnlib.ralib.equivalence.IOCounterExamplePrefixFinder;
import de.learnlib.ralib.equivalence.IOCounterExamplePrefixReplacer;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.equivalence.IORandomWalk;
import de.learnlib.ralib.learning.Hypothesis;
import de.learnlib.ralib.learning.RaStar;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.oracles.SimulatorOracle;
import de.learnlib.ralib.oracles.TreeOracleFactory;
import de.learnlib.ralib.oracles.io.BasicIOCacheOracle;
import de.learnlib.ralib.oracles.io.IOFilter;
import de.learnlib.ralib.oracles.io.IOOracle;
import de.learnlib.ralib.oracles.mto.MultiTheorySDTLogicOracle;
import de.learnlib.ralib.oracles.mto.MultiTheoryTreeOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.sul.BasicSULOracle;
import de.learnlib.ralib.sul.DataWordSUL;
import de.learnlib.ralib.sul.SimulatorSUL;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.classanalyzer.SpecialSymbols;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class IOLearningExperiment {

	private Random random;
	private long maxRuns = 1000; // maximum number of runs
	private double resetProbability = 0.1; // reset probability
	private double freshProbability = 0.5; // prob. of choosing a fresh data value
	private int maxDepth = 100; // max depth
	
	public IOLearningExperiment() {
		random = new Random();
	}
	
	
	
	public IOEquivalenceOracle createEquivalenceOracle(Map<DataType, Theory> teachers, Constants consts, IOOracle target, ParameterizedSymbol[] inputSymbols) {
		IORandomWalk randWalk = new IORandomWalk(random, target, false, // do not
				// draw
				// symbols
				// uniformly
				resetProbability, // reset probability
				freshProbability, // prob. of choosing a fresh data value
				maxRuns, // number of runs
				maxDepth, // max depth
				consts,
				false, // reset runs
				teachers, inputSymbols);
		return randWalk;
	}
	
	 /**
     * Creates an MTO with support for equalities, incl. over sum constants, inequalities and deterministically chosen fresh output values.
     */
    public static MultiTheoryTreeOracle createBasicMTO(
            DataWordSUL sul, ParameterizedSymbol error,  
            Map<DataType, Theory> teachers, Constants consts, 
            ConstraintSolver solver, ParameterizedSymbol ... inputs) {
        
        IOOracle ioOracle = new BasicSULOracle(sul, error);
        BasicIOCacheOracle ioCache = new BasicIOCacheOracle(ioOracle);
        IOFilter ioFilter = new IOFilter(ioCache, inputs);
        
        MultiTheoryTreeOracle mto =  new MultiTheoryTreeOracle(
                ioFilter, ioCache, teachers, consts, solver);
        
        return mto;
    }
    
	
	/**
     * Creates an MTO from an RA by simulating it.
     */
    public static MultiTheoryTreeOracle createSimulatorMTO(RegisterAutomaton regAutomaton, Map<DataType, Theory> teachers, Constants consts, ConstraintSolver solver) {
	    DataWordOracle hypOracle = new SimulatorOracle(regAutomaton);
	    SimulatorSUL hypDataWordSimulation = new SimulatorSUL(regAutomaton, teachers, consts);
	    IOOracle hypTraceOracle = new BasicSULOracle(hypDataWordSimulation, SpecialSymbols.ERROR);  
	    return new MultiTheoryTreeOracle(hypOracle, hypTraceOracle,  teachers, consts, solver);
    }

	/**
	 * Launches a learning experiment on the DataWordSUL
	 * 
	 * @param sul              the system to be tested
	 * @param teachers         theories fully configured
	 * @param consts           constants fully defined
	 * @param solver           constraint solver to use
	 * @param actionSymbols    inputs and output symbols applied during the learning
	 *                         process
	 * @param errorSymbol      special error symbol
	 * @param equOracleBuilder
	 */
	public void runIOLearningExperiment(DataWordSUL sul, Map<DataType, Theory> teachers, Constants consts,
			ConstraintSolver solver, ParameterizedSymbol[] actionSymbols, ParameterizedSymbol errorSymbol) {
		ParameterizedSymbol[] inputSymbols = Arrays.stream(actionSymbols).filter(s -> (s instanceof InputSymbol))
				.toArray(ParameterizedSymbol[]::new);

		MultiTheoryTreeOracle mto = createBasicMTO(sul, errorSymbol, teachers, consts, solver, inputSymbols);

		MultiTheorySDTLogicOracle mlo = new MultiTheorySDTLogicOracle(consts, solver);

		TreeOracleFactory hypFactory = (RegisterAutomaton hyp) -> createSimulatorMTO(hyp, teachers, consts, solver);

		// the oracle used for CE optimization
		IOOracle ioOracle = new BasicSULOracle(sul, errorSymbol);

		RaStar rastar = new RaStar(mto, hypFactory, mlo, consts, true, teachers, solver, actionSymbols);

		IOEquivalenceOracle equOracle = createEquivalenceOracle(teachers, consts, ioOracle, inputSymbols); 

		HypVerifier hypVerifier = HypVerifier.getVerifier(true, teachers, consts);

		IOCounterExampleLoopRemover loops = new IOCounterExampleLoopRemover(ioOracle, hypVerifier);
		IOCounterExamplePrefixReplacer asrep = new IOCounterExamplePrefixReplacer(ioOracle, hypVerifier);
		IOCounterExamplePrefixFinder pref = new IOCounterExamplePrefixFinder(ioOracle, hypVerifier);
		DefaultQuery<PSymbolInstance, Boolean> ce = null;

		int check = 0;
		while (true && check < 100) {
			check++;
			rastar.learn();
			// hypothesis
			Hypothesis hyp = rastar.getHypothesis();

			ce = equOracle.findCounterExample(hyp, null);

			if (ce == null) {
				break;
			}
			
			// shrinking counterexamples
			ce = loops.optimizeCE(ce.getInput(), hyp);
			ce = asrep.optimizeCE(ce.getInput(), hyp);
			ce = pref.optimizeCE(ce.getInput(), hyp);
			rastar.addCounterexample(ce);
		}

		RegisterAutomaton hyp = rastar.getHypothesis();

		System.out.println("LAST:------------------------------------------------");
		System.out.println("FINAL HYP: " + hyp);
		System.out.println("Resets: " + sul.getResets());
		System.out.println("Inputs: " + sul.getInputs());
	}
	
	
}
