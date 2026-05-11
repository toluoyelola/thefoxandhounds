import org.junit.runners.Parameterized;
import thefoxandthehounds.businesslogic.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class TableTest{

    private Table table;

    // The size of the board for the current test iteration
    private final int boardSize;

    // Constructor required for parameterized tests
    public TableTest() {
        this.boardSize = 8;//default if no parameter is given
    }
    @Parameterized.Parameters(name = "Board size: {0}x{0}")
    public static Collection<Object[]> validBoardSizes() {
        return Arrays.asList(new Object[][] {
                {4}, {6}, {8}, {10}, {12}  // Valid board sizes
        });
    }


    @Before
    public void setUp() {
        table = Table.getStarterTable(boardSize);
    }

    @Test
    public void testStarterTable() {
        // Verify the board was created with the correct size
        //assertEquals("Board size should match requested size",
               // boardSize, table.getTableSize());

        // Verify fox starts in the correct position (bottom-left corner)
        Fox fox = table.getFox();
        assertEquals("Fox should start in bottom row",
                boardSize - 1, fox.getRow());
        assertEquals("Fox should start in leftmost column",
                0, fox.getCol());

        // Verify correct number of hounds (always half the board size)
        assertEquals("Number of hounds should be board size / 2",
                boardSize / 2, table.hounds.size());

        // Check each hound's starting position
        for (int i = 0; i < table.hounds.size(); i++) {
            Hound hound = table.hounds.get(i);
            assertEquals("Hound should start in top row",
                    0, hound.getRow());
            assertEquals("Hound should be on odd-numbered column",
                    2 * i + 1, hound.getCol());
        }
    }

    @Test
    public void testFoxMovement() {
        // Place fox in middle of board for maximum movement options
        table = Table.getEmptyTable(boardSize);
        int middlePosition = boardSize / 2;
        Fox fox = new Fox(middlePosition, middlePosition);
        table.addFox(fox);

        // Test all possible fox movements
        Direction[] allDirections = Direction.values();
        for (Direction direction : allDirections) {
            // Calculate expected new position
            int expectedRow = middlePosition + direction.getRowStep();
            int expectedCol = middlePosition + direction.getColStep();

            // Only test moves that stay on the board
            if (expectedRow >= 0 && expectedRow < boardSize &&
                    expectedCol >= 0 && expectedCol < boardSize) {

                table.doMove(new Move(fox, direction));

                assertEquals("Fox row position should update correctly",
                        expectedRow, fox.getRow());
                assertEquals("Fox column position should update correctly",
                        expectedCol, fox.getCol());

                // Reset fox position for next test
                fox.setRow(middlePosition);
                fox.setCol(middlePosition);
            }
        }
    }

    @Test
    public void testHoundVictory() {
        table = Table.getEmptyTable(boardSize);

        // Place fox in middle of board
        int middleRow = boardSize / 2;
        int middleCol = boardSize / 2;
        Fox fox = new Fox(middleRow, middleRow);
        table.addFox(fox);

        // Surround fox with hounds to trap it to see if hound wins
        table.addHound(new Hound(middleRow - 1, middleCol - 1));
        table.addHound(new Hound(middleRow - 1, middleCol + 1));
        table.addHound(new Hound(middleRow + 1, middleCol - 1));
        table.addHound(new Hound(middleRow + 1, middleCol + 1));

        assertTrue("Hounds should win when fox is completely trapped",
                table.winHounds());
    }
}
