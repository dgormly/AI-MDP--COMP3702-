package solver;

import problem.VentureManager;

import java.util.*;

public class Action extends State {

    private static List<Action> actionList;
    private static Map<String, Action> actionMap = new HashMap<>();
    private Integer[] state;


    public Action(Integer[] funding) {
        super(funding);
        state = funding;
        actionMap.put(Arrays.toString(funding), this);
    }

    public Action(ArrayList<Integer> funding) {
        super(funding);
    }

    /**
     * Returns an Action object assiciated with the given array
     *
     * This does not generate Action objects.
     *
     * @param action
     *      action array to retrieve object for
     * @return
     *      Action object associated with given array
     *      Null if the action is invalid.
     */
    public static Action getAction(Integer[] action) {
        return actionMap.get(Arrays.toString(action));
    }

    /**
     * Returns all actions. This function does not consider a specific state.
     *
     * Runtime efficiency: O(n)
     *
     * @param numVentures
     *      Number of ventures
     * @param maxAdditionalFunding
     *      The maximum amount of additional funding an individual venture can hold.
     * @param maxFunding
     *      The maximum amount of funding an individual venture can hold.
     * @return
     *      List of the action space.
     */
    public static List<Action> getAllActions(int numVentures, int maxAdditionalFunding, int maxFunding) {
        if (actionList != null) {
            return actionList;
        }

        actionList = new ArrayList<>();
        for (State state : State.getAllStates(maxFunding, numVentures)) {
            if (state.getFunding() <= maxAdditionalFunding) {
                actionList.add(new Action(state.ventureStates));
            }
        }

        return actionList;

    }


    /**
     * This function gets the next action to be checked by value iteration.
     * It calculates the most money that can be spent and generates actions which
     * spend this amount of money.
     *
     * It works by treating a action as a integer in base maxFunds+1.
     * This way we can add on max funds to each integer then convert back to an action
     * to maximise efficiency.
     *
     * For example if the base was 10, we would add 9 each time
     * yeilding 009, 018, 027, 036, ... etc
     * This trick works in all bases due to modulo arithmetic
     *
     * @param vm the venture manager to used
     * @param state the state we are currently iterating
     * @param action the action we just checked
     * @return
     */
    public static Action getNextAction(VentureManager vm, State state, Action action){
        int base = vm.getMaxManufacturingFunds()+1;
        //see getMaxFundingFor details
        int maxFunds = getMaxFunding(vm, state);

        //we haven't got an action yet lets make the first one with maxFunds used
        if (action == null){
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 1; i < vm.getNumVentures(); i++){
                list.add(0);
            }
            list.add(maxFunds);
            return new Action(list); //needs to be the first good action
        }
        //convert the action to a number for arithmetic
        int decimal = actionToDecimal(Arrays.asList(action.ventureStates), base);
        while (decimal < Math.pow(base, vm.getNumVentures())* vm.getMaxManufacturingFunds()){
            decimal += base-1;
            if (sumDigitsBaseN(decimal, base) == maxFunds){
                return decimalToAction(decimal, base);
            }
        }
        //we're done with valid actions
        return null;



    }

    /**
     * Sums the digits corresponding to a decimal number
     * @param number
     * @param base
     * @return
     */
    public static int sumDigitsBaseN(int number, int base){
        int sum = 0;
        int n = number;
        while(n > 0){
            sum += n%base;
            n = n/base;
        }
        return sum;
    }

    /**
     * Converts an action to a decimal
     * @param action
     * @param base
     * @return
     */
    public static int actionToDecimal(List<Integer> action, int base){
        int sum = 0;
        for(int i = 0; i < action.size(); i++){
            sum += Math.pow(base, action.size()-i)*action.get(i);
        }
        return sum;
    }

    public static Action decimalToAction(int number, int base){
        ArrayList<Integer> list = new ArrayList<>();
        int n = number;
        while(n > 0){
            list.add(0, n%base);
            n = n/base;
        }
        return new Action(list);
    }

    /**
     * The maximum funding that can be given is either the maxFunding given in
     * the spec or if our funding levels are  high it is the total amount
     * to reach maximum for each venture.
     *
     * @param vm
     * @param state
     * @return
     */
    public static int getMaxFunding(VentureManager vm, State state){
        int sum = 0;
        for (int i = 0; i < state.ventureStates.length; i++){
            sum += state.ventureStates[i];
        }
        return Math.min(vm.getMaxAdditionalFunding(), vm.getMaxManufacturingFunds()*vm.getNumVentures()-sum);
    }

    /**
     * TODO This can be used for filling in the output file once the iteration is working.
     * @return
     */
    @Override
    public String toString() {
        return Arrays.toString(state);
    }
}
