package thefoxandthehounds.playonterminal;

import thefoxandthehounds.businesslogic.*;
import java.util.Arrays;
import java.util.Scanner;
import static java.lang.System.in;

public class GamePlay {
    private GameIO gameIO;
    private GameState gameState;
    private Scanner scanner;

    public GamePlay(GameIO gameIO, GameState gameState) {
        this.gameIO = gameIO;
        this.gameState = gameState;
        this.scanner = new Scanner(in);
    }

    public void play() {
        if (gameState.getHumanPlaysWithFox()) {
            playWithFox();
        } else {
            playWithHounds();
        }
    }

    private void playWithFox() {
        if (gameState.getFoxIsOnMove() == null) gameState.setFoxIsOnMove(true);
        if (!gameState.getFoxIsOnMove()) {
            if (gameState.getTable().winFox()) {System.out.println("You won"); return;}
            if (gameState.getTable().winHounds()) {System.out.println("You loose"); return; }
            gameState.getTable().doARandomHoundMove();
            gameState.setFoxIsOnMove(true);
            System.out.println("The table is now: " + gameState.getTable());
        }

        char c = ' ';
        while (true) {
            if (gameState.getTable().winFox()) {System.out.println("You won"); return;}
            if (gameState.getTable().winHounds()) {System.out.println("You loose"); return; }
            System.out.print("\nYour move: a/d/q/e, exit: x ");
            String s = scanner.nextLine();
            c = Character.toLowerCase(s.charAt(0));
            while(!Arrays.asList('a','d','q','e','x').contains(c)) {
                System.out.println("Invalid move! Please enter a/d/q/e for movement or x to exit");
                s = scanner.nextLine();
                c = Character.toLowerCase(s.charAt(0));
            }

            if (c == 'x') return;
            gameState.getTable().doMove(new Move(gameState.getTable().getFox(), directionByChar(c)));
            gameState.setFoxIsOnMove(false);
            System.out.println("The table is now: " + gameState.getTable());
            gameState.getTable().doARandomHoundMove();
            gameState.setFoxIsOnMove(true);
            System.out.println("The table is now: " + gameState.getTable());
        }
    }

    private void playWithHounds() {
        if (gameState.getFoxIsOnMove() == null) gameState.setFoxIsOnMove(false);
        if (gameState.getFoxIsOnMove()) {
            if (gameState.getTable().winFox()) {System.out.println("You loose"); return;}
            if (gameState.getTable().winHounds()) {System.out.println("You won"); return; }
            gameState.getTable().doARandomFoxMove();
            gameState.setFoxIsOnMove(false);
            System.out.println("The table is now: " + gameState.getTable());
        }

        char c = ' ';
        Hound activeHound = null;
        boolean exit = false;
        while (true) {
            if (gameState.getTable().winFox()) {System.out.println("You loose"); return;}
            if (gameState.getTable().winHounds()) {System.out.println("You win"); return; }
            boolean isHoundFound = false;
            while (!isHoundFound) {
                System.out.println("Which hound is to move? A row and a column should be given: (exit: -1)");
                int y = scanner.nextInt(); int x = scanner.nextInt();
                if (y == -1) return;
                activeHound = gameState.getTable().getHound(y, x);
                if (activeHound == null) {
                    System.out.println("There is no hound found.");
                } else isHoundFound = true;
            }
            System.out.println("Give a step of that hound: a/d, x - exit");

            scanner.nextLine();
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': gameState.getTable().doMove(new Move(activeHound, Direction.SOUTHWEST)); break;
                case 'd': gameState.getTable().doMove(new Move(activeHound, Direction.SOUTHEAST)); break;
                case 'x': exit = true; break;
                default: break;
            }
            if (exit) return;
            gameState.setFoxIsOnMove(true);
            System.out.println("The table is now: " + gameState.getTable());
            gameState.getTable().doARandomFoxMove();
            gameState.setFoxIsOnMove(false);
            System.out.println("The table is now: " + gameState.getTable());
        }
    }

    private Direction directionByChar(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a': return Direction.SOUTHWEST;
            case 'd': return Direction.SOUTHEAST;
            case 'q': return Direction.NORTHWEST;
            case 'e': return Direction.NORTHEAST;
            default: return null;
        }
    }
}