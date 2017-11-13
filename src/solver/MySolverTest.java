package solver;

import org.junit.Before;
import org.junit.Test;
import problem.ProblemSpec;
import problem.VentureManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MySolverTest {
//    These are my first 2 iterations, starting with the rewards we have in common
//            0:0 v -1.12500 v' -0.73695 best action 2:1
//            0:1 v -0.31000 v' -0.41969 best action 2:0
//            0:2 v -0.02000 v' -0.37441 best action 1:0
//            0:3 v 0.21000 v' -0.14441 best action 0:0
//            1:0 v 0.50500 v' 0.89305 best action 1:1
//            1:1 v 1.32000 v' 1.21031 best action 1:0
//            1:2 v 1.61000 v' 1.12591 best action 0:0
//            2:0 v 1.08500 v' 1.47305 best action 0:1
//            2:1 v 1.90000 v' 1.79031 best action 0:0
//            3:0 v 1.54500 v' 1.93305 best action 0:0
//            0:0 v -0.73695 v' -0.35860 best action 3:0
//            0:1 v -0.41969 v' -0.08987 best action 2:0
//            0:2 v -0.37441 v' -0.41864 best action 1:0
//            0:3 v -0.14441 v' -0.22507 best action 0:0
//            1:0 v 0.89305 v' 1.27140 best action 2:0
//            1:1 v 1.21031 v' 1.54013 best action 1:0
//            1:2 v 1.12591 v' 1.21136 best action 0:0
//            2:0 v 1.47305 v' 1.85140 best action 1:0
//            2:1 v 1.79031 v' 2.12013 best action 0:0
//            3:0 v 1.93305 v' 2.31140 best action 0:0

    MySolver solver;
    ProblemSpec spec;
    VentureManager ventureManager;

    @Before
    public void setup() throws IOException {
        spec = new ProblemSpec();
        spec.loadInputFile("testcases/bronze1.txt");
        ventureManager = new VentureManager("bronze");
        solver = new MySolver(spec);


    }

    @Test
    public void rewardFunctionTest() {
        Integer[] s = {0, 0};
        Integer[] a = {1, 1};
        State state = new State(s);
        Action action = new Action(a);

        double initialReward = solver.rewardFunction(state, action);
        assertEquals("Expected", 0, initialReward, 0.0000);
    }

    @Test
    public void valueFunctionTest() {
        Integer[] initialRewardAction = {0, 0};
        Integer[] s1 = {3, 2};
        State state = new State(s1);

        // Setup
        List<State> stateSpace = State.getAllStates(ventureManager.getMaxManufacturingFunds(), ventureManager.getNumVentures());
        List<Action> actionSpace = Action.getAllActions(ventureManager.getNumVentures(), ventureManager.getMaxAdditionalFunding(), ventureManager.getMaxManufacturingFunds());
        for (State s : stateSpace) {
            s.setValidActions(actionSpace,ventureManager.getMaxManufacturingFunds());
            s.setIterationValue(solver.rewardFunction(s, new Action(initialRewardAction)));
        }

        double bestT = 0.0;

        int actionNumber = state.getAllActions(3).size();
        assertEquals("Expected only two valid actions.", 2, actionNumber);

        for (int a = 0; a < actionNumber; a++) {
            Action action = state.getAllActions(3).get(a);

            if (a == 1) {
                assertEquals("Expected action {0, 0}", Arrays.toString(initialRewardAction), Arrays.toString(action.ventureStates));
            }

            double initialReward = solver.rewardFunction(state, action);
            // Generate all possible future states from given action (There will be a more than one). This checks if action is valid.
            List<State> nextStates = state.getTransitionStates(action);
            double transition = 0;
            // Calculate expected future utility.
            for (State futureState : nextStates) {
                transition += solver.transitionFunction(state, action, futureState) * futureState.getIterationValue();
                // Take the max utility found.
                bestT  =  initialReward + 0.975 * transition > bestT ? initialReward + 0.975 * transition : bestT;
            }
        }
        // Save utility value.

    }
}
