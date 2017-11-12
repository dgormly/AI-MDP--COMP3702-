package solver;

import org.junit.Test;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Before
    public void setupActionClass() {

    }

    @Test
    public void getActionSpaceTest() {
        List<Action> actionList = Action.getAllActions(5, 1, 2);
        assertEquals("Expected 3 actions.", 6, actionList.size());
    }
}
