package thefoxandthehounds.playonterminal;
import thefoxandthehounds.businesslogic.*;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import static java.lang.System.in;
import static thefoxandthehounds.playonterminal.PlayOnTerminal.logger;

public class GameIO {
    private Scanner scanner;

    public GameIO() {
        this.scanner = new Scanner(in);
    }



    public int readTableSize() {
        int size = -1;
        while (!(size <= 12 && size >= 4 && size % 2 == 0)) {
            System.out.print("Give the size of the table as an even integer between 4 and 12: ");
            try {
                size = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("Not integer is given as size");
            }
        }
        return size;
    }

    public boolean chooseSide() {
        while (true) {
            System.out.print("\nWould you lead fox (f) or hounds (h) or will you exit (x)?: ");
            char choice = scanner.nextLine().toLowerCase().charAt(0);
            switch (choice) {
                case 'f': return true;
                case 'h': return false;
                case 'x': System.exit(0);
            }
        }
    }

    private boolean readYesNo() {
        while (true) {
            System.out.print("Give 'y' or 'n' as answer: \n ");
            char answer = scanner.nextLine().toLowerCase().charAt(0);
            if (answer == 'y') return true;
            if (answer == 'n') return false;
        }
    }
    public EditMode getEditingMode() {
        System.out.println("Editing happens now from empty table.\n" +
                "How do you wish the editing? From empty table (e)\n" +
                "or from the starter table by steps(s) " + "or with a random table? (r)\n" + "x -exit(x)" +
                "random does not work yet");

        while (true) {
            System.out.println("e/s/x: ");
            char input = scanner.nextLine().toLowerCase().charAt(0);
            switch (input) {
                case 'e': return EditMode.EMPTY;
                case 's': return EditMode.STARTER;
                case 'x': return EditMode.EXIT;
            }
        }
    }

    public int[] getFoxPosition() {
        System.out.println("Input row and column coordinates of the fox indexed starting with 0: ");
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        int row = Integer.parseInt(lineScanner.next());
        int col = Integer.parseInt(lineScanner.next());
        lineScanner.close();
        return new int[]{row, col};
    }

    public int[] getHoundPosition(int houndNumber) {
        System.out.println("Input the " + houndNumber + ". hounds row and column positions indexed starting with 0: ");
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        int row = Integer.parseInt(lineScanner.next());
        int col = Integer.parseInt(lineScanner.next());
        lineScanner.close();
        return new int[]{row, col};
    }

    public int[] getHoundPosition() {
        System.out.println("Which hound is to move? A row and a column should be given: (exit: -1)");
        int row = scanner.nextInt();
        if (row == -1) return null;
        int col = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return new int[]{row, col};
    }

    public Direction getFoxMove() {
        System.out.println("Give a step of the fox: a-southwest/d-southeast/q-northwest/e-northeast, p - out from editing");
        char c = scanner.nextLine().toLowerCase().charAt(0);
        return charToDirection(c);
    }

    public Direction getHoundMove() {
        System.out.println("What is the direction of the move?: a(southwest)/d(southeast), p - out from editing");
        char c = scanner.nextLine().toLowerCase().charAt(0);
        return charToDirection(c);
    }

    public void showError(String message) {
        System.out.println(message);
    }

    private Direction charToDirection(char c) {
        switch (c) {
            case 'a': return Direction.SOUTHWEST;
            case 'd': return Direction.SOUTHEAST;
            case 'q': return Direction.NORTHWEST;
            case 'e': return Direction.NORTHEAST;
            case 'p': return null;
            default: return null;
        }
    }

    private  GameIO gameIO;


}
