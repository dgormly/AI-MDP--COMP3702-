package problem;

import org.junit.Before;
import org.junit.Test;
import solver.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StateTest {

    State state;

    @Before
    public void setupStates() {
        Integer[] input = {2, 2};
        state = new State(input);
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
        assertEquals("Expected array of size 343", 343, validStates.size());
    }

}
