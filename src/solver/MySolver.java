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

	/**
	 * Called for a single ventures reward function
	 * @param ventureNumber the venture number (indexed from 0)
	 * @param initialFunding the initial funding before the action
	 * @param addedFunding the added funding to the venture
     * @return the reward function for the venture
     */
	private double rewardFunction(int ventureNumber, int initialFunding, int addedFunding){

		//Note Venture Number is indexed from 0

		double profit = 0;
		double loss = 0;

		//stored for efficiency
		int currentAmount = initialFunding + addedFunding;

		//we only care about the probs for our current amount of stock
		List<Double> row = probabilities.get(ventureNumber).getRow(initialFunding+addedFunding);

		//getting the expected value of each possible customer order
		//note 0 is not included as it is a net 0 profit/loss
		for (int i = 1; i < ventureManager.getMaxManufacturingFunds(); i++){
			// profit is based on the amount sold by the probability that it gets sold
			// if we dont have enough stock we just sell all the current stock
			profit += Math.min(i, currentAmount) * row.get(i);
			if (i > currentAmount){
				//we only lose money if we don't have enough product
				loss += (i-currentAmount) * row.get(i);
			}
		}

		return 0.6*(spec.getSalePrices().get(ventureNumber)-addedFunding)*profit - 0.25*spec.getSalePrices().get(ventureNumber)*loss;
	}

	/**
	 * Gets the reward function from the current state and the action performed
	 * @param states a list of the current states of each venture
	 * @param actions a list of the current actions of each venture
     * @return the total reward function R(s,a s')
     */
	private double rewardFunction(List<Integer> states, List<Integer> actions){

		//Note ventures are indexed from 0
		double totalReward = 0;

		for (int i = 0; i < ventureManager.getNumVentures(); i++){
			//reward is summed over all ventures
			totalReward += rewardFunction(i, states.get(i), actions.get(i));
		}

		return totalReward;
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
