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
        List<Integer[]> validStates = state.getValidTransitionStates(2, 2);
        assertEquals("Expected array of size 6", 6, validStates.size());
    }

}
