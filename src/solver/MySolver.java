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
import java.util.*;

import static java.util.stream.Collectors.toMap;

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
		List<State> state = State.getAllStates(ventureManager.getMaxManufacturingFunds(), ventureManager.getNumVentures());

		valueIteration(10, state);

        System.out.println("Policy:");
        state.forEach(e ->{
            System.out.println(e);
        });
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
	 * @param state a State object containing all venture states
	 * @param action a list of the current actions of each venture
     * @return the total reward function R(s,a s')
     */
	private double rewardFunction(State state, Action action){

		//Note ventures are indexed from 0
		double totalReward = 0;

		for (int i = 0; i < ventureManager.getNumVentures(); i++){
			//reward is summed over all ventures
			totalReward += rewardFunction(i, state.getVenture(i), action.getVenture(i));
		}

		return totalReward;
	}
	
	public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
														  int numFortnightsLeft) {
		// Example code that allocates an additional $10 000 to each venture.
		// TODO Replace this with your own code.

		List<Integer> additionalFunding = new ArrayList<Integer>();


		/*
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
		*/
		return null;
	}

	/**
	 * Gets the probability of transition from one state to the next
	 * @param currentState, the initial states of each venture
	 * @param action the actions
	 * @param futureState the future states after customer buys
	 */
	private double transitionFunction(State currentState, Action action, State futureState){

		//start with 1 as the multiplicative identity
		int probability = 1;

		//multiples each probability
		for (int i = 0; i < ventureManager.getNumVentures(); i++){
			probability *= spec.getTransitions().get(i).get(currentState.getVenture(i) + action.getVenture(i), futureState.getVenture(i));
		}

		return probability;
	}


	/**
	 * Iterates over all states for a set number of loops.
	 * @param numIterations
	 * 		Number of times to iterater over the state space.
	 * @param statesList
	 * 		State-space to iterate over
	 */
	public void valueIteration(int numIterations, List<State> statesList) {
		double discount = spec.getDiscountFactor();

		// Number of times to iterate TODO change this to check if converges).
		for (int i = 0; i < numIterations; i++) {

			// States to iterate over.
			for (State currentState: statesList) {
				double bestT = 0.0;

				// List of actions to apply.

				for (Action action : currentState.getAllActions(ventureManager.getMaxAdditionalFunding())) {
					double initialReward = rewardFunction(currentState, action);
					// Generate all possible future states from given action (There will be a more than one). This checks if action is valid.
					List<State> nextStates = State.getNextState(currentState, action, ventureManager.getMaxManufacturingFunds());
					double transition = 0;

					// Calculate expected future utility.
					for (State futureState : nextStates) {
						transition += transitionFunction(currentState, action, futureState) * futureState.getIterationValue();
					}
					// Take the max utility found.
					bestT  =  transition > bestT ? initialReward + discount * transition : bestT;
				}
				// Save utility value.
				currentState.setIterationValue(bestT);
			}
		}
	}






}
