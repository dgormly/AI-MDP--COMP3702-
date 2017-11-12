package solver;

import java.util.ArrayList;
import java.util.List;

public class Action extends State {

    private static List<Action> actionList;


    public Action(Integer[] funding) {
        super(funding);
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
}
