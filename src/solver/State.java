package solver;

import java.util.*;

public class State implements Comparable {

    private static List<State> allStates;
    private static Map<String, State> stateMap = new HashMap<>();

    protected Integer[] ventureStates;
    private int sum = 0;
    private static int numVentures;
    private List<Action> validActions;
    private List<State> transitionStates;

    private double iterationValue = 0.0;
    private boolean convergered = false;



    /**
     * Holds the state of all ventures.
     *
     * @param funding
     *      Initial state.
     */
    public State(Integer[] funding) {
        ventureStates = funding;

        for (Integer i : funding) {
            sum += i;
        }
        numVentures = funding.length;
        stateMap.put(Arrays.toString(funding), this);
    }

//    public State(ArrayList<Integer> funding){
//        ventureStates = (Integer[]) funding.toArray();
//    }


    /**
     * The total amount of funding across all ventures.
     *
     * @return
     *      The total sum.
     */
    public int getFunding() {
        return sum;
    }


    /**
     * Returns the state of a given venture.
     *
     * @param ventureNum
     *      Venture index to return starting from zero.
     * @return
     *      Venture state.
     */
    public int getVenture(int ventureNum) {
        return ventureStates[ventureNum];
    }

    /**
     * Returns all state permutations for a given number of ventures with a maximum funding.
     *
     * Runtime efficiency: O(n)
     *
     * @param maxFunding
     *      The maximum value allowed for each venture.
     * @param numVentures
     *      Number of ventures.
     * @return
     *      List of permutations.
     */
    public static List<State> getAllStates(int maxFunding, int numVentures) {

        List<Integer[]> tempList = new ArrayList<>();
        List<State> allStates = new ArrayList<>();

        // Get base
        for (int i = 0; i <= maxFunding; i++) {
            Integer[] temp = new Integer[numVentures];
            Arrays.fill(temp, 0);
            temp[0] = i;
            tempList.add(temp);
        }

        // increment there-after
        for (int i = 1; i < numVentures; i++) {
            sum(tempList, i, maxFunding);
        }

        // Convert to states.
        List<State> list = new ArrayList<>();
        for (Integer[] state : tempList) {
            State s = new State(state);
            list.add(s);
        }

        Collections.sort(list);
        for (State s : list) {
            if (s.getFunding() <= maxFunding) {
                allStates.add(s);
            }
        }
        return allStates;
    }


    /**
     * Don't touch this! Too hard to explain but is used to work out all permutations.
     *
     * @param list
     * @param ventureNum
     * @param maxFunding
     * @return
     */
    private static List<Integer[]> sum(List<Integer[]> list, int ventureNum, int maxFunding) {
        List<Integer[]> finalList = new ArrayList<>();
        // Get all values from given list, copy and increment the column.

        for (int i = 1; i <= maxFunding; i++) {
            for (Integer[] state : list) {
                Integer[] s = state.clone();
                s[ventureNum] = i;
                finalList.add(s);
            }
        }

        list.addAll(finalList);
        return list;
    }


    /**
     * Returns a list of all possible next states given a current state, action, and maximum funding.
     *
     *
     * @param currentState
     *      Current state to find the next state of.
     * @param action
     *      Action applied to the current state.
     * @param maxFunding
     *      The upperbound that each venture is allowed to have.
     * @return
     *      Next State given the action. If the action is invalid the function will return null.
     */
    public static List<State> getNextState(State currentState, Action action, int maxFunding) {
        Integer[] state = new Integer[currentState.ventureStates.length];
        List<State> nextStates = new ArrayList<>();

        for (int i = 0; i < currentState.ventureStates.length; i++) {
            int c = currentState.getVenture(i);
            int a = action.getVenture(i);

            if (c + a <= maxFunding) {
                state[i] = c + a;
            } else {
                return null;
            }
        }
        State aState = stateMap.get(Arrays.toString(state));
        nextStates.add(aState);

        for (State s : getAllStates(maxFunding, currentState.ventureStates.length)) {
            boolean valid = true;

            for (int y = 0; y < aState.ventureStates.length && valid == true; y++) {
                if (s.getFunding() > aState.getFunding()) {
                    valid = false;
                }

                if (s.getVenture(y) > aState.getVenture(y) + action.getVenture(y)) {
                    valid = false;
                }
            }

            if (valid && !nextStates.contains(s)) {
                nextStates.add(s);
            }
        }

        return nextStates;
    }


    /**
     * Checks if a given action is valid for the state.
     *
     * @param action
     *      Action to check
     * @param maxFunding
     *      Upper-bound of manufacturing
     * @return
     *      True if action stays within the funding bounds.
     *      False otherwise
     */
    public boolean isValidAction(Action action, int maxFunding) {
        for (int i = 0; i < numVentures; i++) {
            if (ventureStates[i] + action.getVenture(i) > maxFunding) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns all valid actions in decending order. Only needs to compute once.
     *
     * @param maxAdditionalFunding
     *      Upper-bound of manufacturing
     * @return
     *      List of all valid actions for the given state
     *      Null if the list hot been set (An empty list can represend that all ventures are maxxed out)
     */
    public List<Action> getAllActions(int maxAdditionalFunding) {
        if (validActions == null) {
            return new ArrayList<>();
        }

        return validActions;
    }


    /**
     * Generates a list of valid actions in decending order for the given state.
     *
     * Will not check if funding space is less then action space.
     *
     * @param actionList
     * @param maxFunding
     *
     */
    public List<Action> setValidActions(List<Action> actionList, int maxFunding) {
        validActions = new ArrayList<>();
        transitionStates = new ArrayList<>();

        // Get additional funding space available.
        int fundingSpace = maxFunding - getFunding();

        // Iterate backwards through list (ascending order)
        for (int i = actionList.size() -1; i >= 0; i--) {
            Action a = actionList.get(i);

            if (a.getFunding() > fundingSpace) {
                break;
            }

            if (isValidAction(a, maxFunding)) {
                validActions.add(a);
                Integer[] nextState = ventureStates.clone();
                for (int s = 0; s < nextState.length; s++) {
                    nextState[s] += a.getVenture(s);
                }
                State state = stateMap.get(Arrays.toString(nextState));
                transitionStates.add(state);
            }
        }
        return validActions;
    }


    /**
     * Compare two states against each other using total funding.
     *
     * @param o
     *      object to compare against.
     * @return
     *      1 if greater than o
     *      0 if equal to 0
     *      -1 if less than o
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof State) {
            State s = (State) o;
            if (s.getFunding() > this.getFunding()) {
                return 1;
            } else if (s.getFunding() < this.getFunding()) {
                return -1;
            } else if (s.getFunding() == this.getFunding()) {
                return 0;
            }
        }
        return 1;
    }


    /**
     * Returns a list of all possible states this function count transition to.
     * @return
     *      List of states
     *      Null if the inventory is full.
     */
    public List<State> getTransitionStates(Action action) {
        List<State> list = new ArrayList<>();
        for (State s : transitionStates) {
            boolean valid = true;
            for (int i = 0; i < ventureStates.length; i++) {
                if (ventureStates[i] + action.ventureStates[i] > s.ventureStates[i]) {
                    valid = false;
                    break;
                }
                if (valid) {
                    list.add(s);
                }
            }
        }

        return list;
    }


    public List<State> getAllTransitionStates() {
        if (transitionStates == null) {
            return new ArrayList<>();
        }
        return transitionStates;
    }

    /**
     * Returns a State object for the given integer array.
     *
     * This does not create a state.
     *
     * @param state
     *      Object state to return
     * @return
     *      State associated with given array
     *      Null if it does not exist.
     */
    public static State getState(Integer[] state) {
        State s = stateMap.get(Arrays.toString(state));
        return s;
    }


    /**
     * Sets the iteration value and returns true if the value is converging.
     * TODO Write check for convergence
     *
     * @param iterationValue
     *      Value to update the iteration with.
     * @return
     *      True, the value has converged
     *      False, the value has not converged
     */
    public boolean setIterationValue(double iterationValue) {
        this.iterationValue = iterationValue;
        return false;
    }


    /**
     * returns the iteration value used in value iteration.
     * @return
     *      Decimal value
     */
    public double getIterationValue() {
        return iterationValue;
    }


    /**
     * Returns the best action to take for this state.
     *
     * @return
     *      Action to take
     */
    public Action getPolicy() {
        State best = null;

        for (State s : getAllTransitionStates()) {
            if (best == null) {
                best = s;
            }

            if (s.getIterationValue() > best.getIterationValue()) {
                best = s;
            }
        }

        if (best == null) {
            return null;
        }

        Integer[] state = best.ventureStates.clone();
        for (int i = 0; i < ventureStates.length; i++) {
            state[i] -= this.ventureStates[i];
        }

        return Action.getAction(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (sum != state.sum) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(ventureStates, state.ventureStates);
    }


    @Override
    public int hashCode() {
        int result = Arrays.toString(ventureStates).hashCode();
        result = 31 * result + sum;
        return result;
    }


    @Override
    public String toString() {
        return Arrays.toString(ventureStates);
    }
}
