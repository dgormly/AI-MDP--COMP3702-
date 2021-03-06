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


public class MySolver implements FundingAllocationAgent {
	
	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
    private List<Matrix> probabilities;
    private Integer[] initialRewardAction = {0, 0};
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
	}
	
	public void doOfflineComputation() {
		List<State> stateSpace = State.getAllStates(ventureManager.getMaxManufacturingFunds(), ventureManager.getNumVentures());
		List<Action> actionSpace = Action.getAllActions(ventureManager.getNumVentures(), ventureManager.getMaxAdditionalFunding(), ventureManager.getMaxManufacturingFunds());
		for (State s : stateSpace) {
			s.setValidActions(actionSpace,ventureManager.getMaxManufacturingFunds());
		}

		valueIteration(stateSpace);
//
//        System.out.println("Policy:");
//        stateSpace.forEach(e ->{
//            System.out.println(e + " -> " + e.getPolicy().toString() + " " + e.getIterationValue());
//        });
	}

	/**
	 * Called for a single ventures reward function
	 * @param ventureNumber the venture number (indexed from 0)
	 * @param initialFunding the initial funding before the action
	 * @param addedFunding the added funding to the venture
     * @return the reward function for the venture
     */
	protected double rewardFunction(int ventureNumber, int initialFunding, int addedFunding){

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
	protected double rewardFunction(State state, Action action){

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
		Integer[] manufacturingFundsArray = new Integer[manufacturingFunds.size()];

		for (int i = 0; i < manufacturingFunds.size(); i++) {
			manufacturingFundsArray[i] = manufacturingFunds.get(i);
		}

		List<Integer> additionalFunding = new ArrayList<>();
		State state = State.getState(manufacturingFundsArray);
        Action action = state.getPolicy();

        if (action == null){
            Integer[] a =  {0, 0};
            action = new Action(a);
        }

        for (int i = 0; i < action.ventureStates.length; i++){
            additionalFunding.add(action.ventureStates[i]);
        }

        return additionalFunding;


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
	}

	/**
	 * Gets the probability of transition from one state to the next
	 *
	 * @param currentState, the initial states of each venture
	 * @param action the actions
	 * @param futureState the future states after customer buys
	 */
	protected double transitionFunction(State currentState, Action action, State futureState){

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
	 *
	 * @param statesList
	 * 		State-space to iterate over
	 */
	public void valueIteration(List<State> statesList) {
		double discount = spec.getDiscountFactor();
		int maxFunding = ventureManager.getMaxManufacturingFunds();

		// Number of times to iterate TODO change this to check if converges).

		int count = statesList.size();
		while(count > 0) {
			// States to iterate over.
			for (State currentState: statesList) {
				// Check for convergance
				if (currentState.isConvergered()) {
					count--;
					continue;
				}

				double bestT = 0.0;
				// List of actions to apply.
				for (int a = 0; a < currentState.getAllActions(maxFunding).size(); a++) {
						Action action = currentState.getAllActions(maxFunding).get(a);
					double initialReward = rewardFunction(currentState, action);
					// Generate all possible future states from given action (There will be a more than one). This checks if action is valid.
					List<State> nextStates = currentState.getTransitionStates(action);
					double transition = 0;
					// Calculate expected future utility.
					for (State futureState : nextStates) {
						transition += transitionFunction(currentState, action, futureState) * futureState.getIterationValue();
						// Take the max utility found.
						bestT  =  initialReward + discount * transition > bestT ? initialReward + discount * transition : bestT;
					}
				}
				// Save utility value.
				currentState.setIterationValue(bestT);
			}
		}
	}






}
