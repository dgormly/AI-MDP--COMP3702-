package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import problem.Matrix;
import problem.ProblemSpec;
import problem.VentureManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MySolver implements FundingAllocationAgent {
	
	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
    private List<Matrix> probabilities;
    private double discountFactor;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
        discountFactor = spec.getDiscountFactor();
	}
	
	public void doOfflineComputation() {
		boolean solved = false;

		iterations = 0;
		while(!solved) {
			double maxUtilityChange = 0;

			for(State state = mdp.getStartState(); state!=null; state=mdp.getNextState()) {

				double utility = mdp.getUtility(state);
				double reward = rewardFunction(state);

				double maxCurrentUtility = -1e30;
				Action maxAction = null;

				// The following while loop computes \max_a\sum T(s,a,s')U(s')
				for(Action action=mdp.getStartAction(); action!=null;
					action=mdp.getNextAction()){

					Vector transition = mdp.getTransition(state, action);
					int size = transition.size();
					double nextUtil = 0;
					for(int i = 0; i < size; i++) {
						Transition t=(Transition)transition.get(i);
						double prob=t.probability;
						State sPrime=t.nextState;
						nextUtil += (prob * mdp.getUtility(sPrime));
					}

					if(nextUtil > maxCurrentUtility){
						maxCurrentUtility = nextUtil;
						maxAction = action;
					}
				}

				maxCurrentUtility = reward + discountFactor * maxCurrentUtility;
				mdp.setUtility(state, maxCurrentUtility);
				mdp.setAction(state, maxAction);

				double currentError = Math.abs(maxCurrentUtility - utility);
				if(currentError > maxUtilityChange) {
                    maxUtilityChange = currentError;
                }
			}

			iterations++;
			if(maxUtilityChange < epsilon * (1. - discountFactor)/discountFactor) {
                solved = true;
            }
		}
		return; //something
	}

	private double rewardFunction(int ventureNumber, int initialFunding, int addedFunding){

		//Note Venture Number is indexed from 0

		double profit = 0;
		double loss = 0;
		int currentAmount = initialFunding + addedFunding;
		List<Double> row = probabilities.get(ventureNumber).getRow(initialFunding+addedFunding);

		/*
		refactored code

		for (int i = 1; i < ventureManager.getMaxManufacturingFunds(); i++){
			profit += Math.min(i, initialFunding + addedFunding) * row.get(i);
		}
		for  (int i = initialFunding + addedFunding; i < ventureManager.getMaxManufacturingFunds(); i++){
			loss += (i-initialFunding-addedFunding) * row.get(i);
		}

		return 0.6*(spec.getSalePrices().get(ventureNumber)-addedFunding)*profit - 0.25*spec.getSalePrices().get(ventureNumber)*loss;
		*/

		for (int i = 1; i < ventureManager.getMaxManufacturingFunds(); i++){
			profit += Math.min(i, currentAmount) * row.get(i);
			if (i > currentAmount){
				loss += (i-currentAmount) * row.get(i);
			}
		}

		return 0.6*(spec.getSalePrices().get(ventureNumber)-addedFunding)*profit - 0.25*spec.getSalePrices().get(ventureNumber)*loss;
	}
	
	public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
														  int numFortnightsLeft) {
		// Example code that allocates an additional $10 000 to each venture.
		// TODO Replace this with your own code.

		List<Integer> additionalFunding = new ArrayList<Integer>();

		int totalManufacturingFunds = 0;
		for (int i : manufacturingFunds) {
			totalManufacturingFunds += i;
		}
		
		int totalAdditional = 0;
		for (int i = 0; i < ventureManager.getNumVentures(); i++) {
			if (totalManufacturingFunds >= ventureManager.getMaxManufacturingFunds() ||
			        totalAdditional >= ventureManager.getMaxAdditionalFunding()) {
				additionalFunding.add(0);
			} else {
				additionalFunding.add(1);
				totalAdditional ++;
				totalManufacturingFunds ++;
			}
		}

		return additionalFunding;
	}

}
