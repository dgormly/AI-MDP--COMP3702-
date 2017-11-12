package solver;

import org.junit.Test;
import org.junit.Before;

import java.util.Iterator;
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

    @Test
    public void ActionSpaceSortedTest() {
        List<Action> actionSpace = Action.getAllActions(3, 6, 4);

        for (int i = 1; i < actionSpace.size(); i++) {
            Action prev = actionSpace.get(i -1);
            Action current = actionSpace.get(i);
            assertEquals("Expected value equal-to or less than previous", true, current.compareTo(prev) >= 0);
        }
    }
}
