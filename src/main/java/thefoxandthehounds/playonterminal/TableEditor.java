package thefoxandthehounds.playonterminal;

import thefoxandthehounds.businesslogic.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;
import static java.lang.System.in;

public class TableEditor {
    private static final Logger logger = Logger.getLogger(TableEditor.class.getName());
    private final GameIO gameIO;
    private GameState gameState;
    private Scanner scanner;
    private Table table;

    public TableEditor(GameIO gameIO, GameState gameState) {
        this.gameIO = gameIO;
        this.gameState = gameState;
    }

    public void editTable() {
        EditMode mode = gameIO.getEditingMode();
        switch (mode) {
            case EMPTY:
                editingFromEmptyTable();
                break;
            case STARTER:
                editingFromStartingPosition();
                break;
            case EXIT:
                System.exit(0);
        }
    }

    private void editingFromEmptyTable() {
        scanner = new Scanner(in);
        table = Table.getEmptyTable(gameState.getSize());

        // FIX: Sync the new table to the GameState immediately
        gameState.setTable(table);

        System.out.println("Input row and column coordinates of the fox indexed starting with 0: ");
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        int row = Integer.parseInt(lineScanner.next());
        int col = Integer.parseInt(lineScanner.next());
        table.addFox(new Fox(row, col));

        for (int i = 1; i <= gameState.getSize() / 2; i++) {
            System.out.println("Input the " + i + ". hounds row and column positions indexed starting with 0: ");
            line = scanner.nextLine();
            lineScanner = new Scanner(line);
            row = Integer.parseInt(lineScanner.next());
            col = Integer.parseInt(lineScanner.next());
            table.addHound(new Hound(row, col));
        }
    }

    private void editingFromStartingPosition() {
        scanner = new Scanner(in);
        boolean exit = false;
        Character c;
        Hound activeHound = null;
        table = Table.getStarterTable(gameState.getSize());

        // FIX: Sync the new table to the GameState immediately
        gameState.setTable(table);

        System.out.println("The starting table: " + table);
        c = 'a';

        while (Arrays.asList('a','d','q','e','x').contains(Character.toLowerCase(c))) {
            System.out.println("Give a step of the fox: a-southwest/d-southeast/q-northwest/e-northeast, p - out from editing");
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': table.doMove(new Move(table.getFox(), Direction.SOUTHWEST)); break;
                case 'd': table.doMove(new Move(table.getFox(), Direction.SOUTHEAST)); break;
                case 'q': table.doMove(new Move(table.getFox(), Direction.NORTHWEST)); break;
                case 'e': table.doMove(new Move(table.getFox(), Direction.NORTHEAST)); break;
                case 'p': exit = true; break;
                default: break;
            }
            if (exit) return;
            System.out.println("The table is now: " + table);

            boolean isHoundFound = false;
            while (!isHoundFound) {
                System.out.println("Which hound is to move? A row and a column should be given: ");
                int y = scanner.nextInt();
                int x = scanner.nextInt();
                activeHound = table.getHound(y, x);
                if (activeHound == null) {
                    System.out.println("There is no hound.");
                } else isHoundFound = true;
            }

            System.out.println("What is the direction of the move?: a(southwest)/d(southeast), p - out from editing");
            scanner.nextLine();
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': table.doMove(new Move(activeHound, Direction.SOUTHWEST)); break;
                case 'd': table.doMove(new Move(activeHound, Direction.SOUTHEAST)); break;
                case 'p': exit = true; break;
                default: break;
            }
            if (exit) return;
            System.out.println("The table is edited by moves: " + table);
        }
    }
}