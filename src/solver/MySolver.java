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

public class MySolver implements FundingAllocationAgent {
	
	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
    private List<Matrix> probabilities;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
	}
	
	public void doOfflineComputation() {
	    // TODO Write your own code here.
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
