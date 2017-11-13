package solver;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StateTest {

    State state;

    @Before
    public void setupStates() {
        Integer[] input = {2, 2};
        state = new State(input);
        State.getAllStates(2,2);
    }


    @Test
    public void GetAllStatesTest() {
        List<State> validStates = state.getAllStates(2, 2);
        assertEquals("Expected array of size 9", 9, validStates.size());

        validStates = state.getAllStates(2, 1);
        assertEquals("Expected array of size 3", 3, validStates.size());

        validStates = state.getAllStates(1, 1);
        assertEquals("Expected array of size 2", 2, validStates.size());

        validStates = state.getAllStates(1, 2);
        assertEquals("Expected array of size 4", 4, validStates.size());

        validStates = state.getAllStates(1, 3);
        assertEquals("Expected array of size 8", 8, validStates.size());

        validStates = state.getAllStates(2, 3);
        assertEquals("Expected array of size 27", 27, validStates.size());

        validStates = state.getAllStates(6, 3);
        assertEquals("Expected array of size 343 (Gold)", 343, validStates.size());
    }


    @Test
    public void NextStateTest() {
        Integer[] s = {2, 2};
        Integer[] a1 = {1, 1};
        Integer[] a2 = {10, 10};

        State state = new State(s);
        Action action = new Action(a1);
        Action action2 = new Action(a2);

        List<State> nextStates = State.getNextState(state, action, 5);

        assertEquals("Expected next state to be {1, 1}", 1, State.getNextState(state, action, 15).size());
        assertEquals("Expected next state to be {1, 1}", 1, State.getNextState(state, action, 1).size());
        //assertEquals("Expected next state to be null", null, State.getNextState(state, action2, 1).size());

    }

    @Test
    public void getValidActionsTest() {
        Integer[] input = {0,0};
        state = new State(input);
        state.setValidActions(Action.getAllActions(2, 1, 2), 2);
        assertEquals("Expected transition states list to equal to 3", 3, state.getTransitionStates().size());

        Integer[] ss2 = {0, 1};
        Integer[] ss3 = {1, 0};
        State s1 = State.getState(input);
        State s2 = State.getState(ss2);
        State s3 = State.getState(ss3);


        assertEquals("Expected transition states list to contain {0, 0}", true, state.getTransitionStates().contains(s1));
        assertEquals("Expected transition states list to contain {1, 0}", true, state.getTransitionStates().contains(s2));
        assertEquals("Expected transition states list to contain {0, 1}", true, state.getTransitionStates().contains(s3));
    }


    @Test
    public void checkMapTest() {
        List<State> stateSpace = State.getAllStates(2, 2);
        assertEquals("Expected state space to be of size 9",9, stateSpace.size());

    }
}
