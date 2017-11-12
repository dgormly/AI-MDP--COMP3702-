package solver;

import java.util.*;

public class State {

    private Integer[] ventureStates;
    private List<Integer[]> allStateList = new ArrayList<>();


    public State(Integer[] funding) {

        ventureStates = funding;

    }


    public Set<Integer[]> getValidTransitionStates(int maxFunding, int numVentures) {
        Integer[] state = ventureStates;

        List<Integer[]> tempList = new ArrayList<>();

        /* Get all combinations. */
        for (int i = 0; i < numVentures; i++) {
            for (int y = 1; y < maxFunding; y++) {
                Integer[] temp = new Integer[numVentures];
                Arrays.fill(temp, 0);
                temp[i] = y;
                tempList.add(temp);
            }
        }

        allStateList.addAll(tempList);

        int listSize = allStateList.size();
        for (int v = 0; v < listSize; v++) {
            Integer[] s1 = allStateList.get(v);
            for (int i = 0; i < listSize; i++) {
                Integer[] s2 = allStateList.get(i);
                if (s1.equals(s2)) {
                    continue;
                }

                for (int y = 0; y < numVentures; y++) {
                    Integer[] t = s1.clone();
                    t[y] = s2[y];
                    if (!allStateList.contains(t)) {
                        allStateList.add(t);
                    }
                }
            }
        }


        return allStateList;
    }

}
