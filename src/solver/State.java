package solver;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Integer[] ventureStates;
    private List<Integer[]> allStates;
    private int sum;

    public State(Integer[] funding) {

        for (int i = 0; i < funding.length; i++) {
            sum += funding[i];
        }
    }


    public List<Integer[]> getValidTransitionStates() {
        Integer[] state = ventureStates;

        /* Get all combinations. */
        for (int i = 0; i < ventureStates.length; i++) {
            for (int y = 1; y < ventureStates[i]; y++) {
                Integer[] tempState = state;
                tempState[i] -= y;
                allStates.add(tempState);
            }
        }
    }

}
