package solver;

import java.util.*;

public class State {

    private Integer[] ventureStates;
    private int sum = 0;

    public State(Integer[] funding) {
        ventureStates = funding;

        for (Integer i : funding) {
            sum += i;
        }
    }

    public int getFunding() {
        return sum;
    }

    public int getVenture(int ventureNum) {
        return ventureStates[ventureNum];
    }

    /**
     * Returns all possible states for a given number of ventures and max funding.
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

        return list;
    }


    /**
     * Don't touch this!
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

}
