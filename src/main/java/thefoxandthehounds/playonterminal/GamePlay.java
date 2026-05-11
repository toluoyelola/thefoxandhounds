package thefoxandthehounds.playonterminal;

import thefoxandthehounds.businesslogic.*;
import thefoxandthehounds.minmax.AIPlayer;

public class GamePlay {
    private GameIO gameIO;
    private GameState gameState;

    private static final int AI_SEARCH_DEPTH = 6;

    public GamePlay(GameIO gameIO, GameState gameState) {
        this.gameIO = gameIO;
        this.gameState = gameState;
    }

    // 1. CENTRAL GAME LOOP
    public void play() {
        if (gameState.getFoxIsOnMove() == null) {
            gameState.setFoxIsOnMove(true); // Fox always starts
        }

        System.out.println("The starting table is: \n" + gameState.getTable());

        // One clean loop that alternates turns until someone wins
        while (!isGameOver()) {
            if (gameState.getFoxIsOnMove()) {
                handleFoxTurn();
            } else {
                handleHoundTurn();
            }
            System.out.println("The table is now: \n" + gameState.getTable());
        }

        announceWinner();
    }

    // 2. TURN HANDLERS
    private void handleFoxTurn() {
        if (gameState.getHumanPlaysWithFox()) {
            humanFoxMove();
        } else {
            aiMove("Fox");
        }
        gameState.setFoxIsOnMove(false); // Pass turn to Hounds
    }

    private void handleHoundTurn() {
        if (!gameState.getHumanPlaysWithFox()) {
            humanHoundMove();
        } else {
            aiMove("Hounds");
        }
        gameState.setFoxIsOnMove(true); // Pass turn to Fox
    }

    // 3. HUMAN INPUT HANDLERS (Delegating to GameIO)
    private void humanFoxMove() {
        while (true) {
            Direction dir = gameIO.getFoxMove();
            if (dir == null) System.exit(0); // User chose to exit

            if (isValidMove(gameState.getTable().getFox(), dir)) {
                gameState.getTable().doMove(new Move(gameState.getTable().getFox(), dir));
                break;
            }
        }
    }

    private void humanHoundMove() {
        while (true) {
            int[] pos = gameIO.getHoundPosition();
            if (pos == null) System.exit(0);

            Hound activeHound = gameState.getTable().getHound(pos[0], pos[1]);
            if (activeHound == null) {
                gameIO.showError("There is no hound found at those coordinates.");
                continue;
            }

            Direction dir = gameIO.getHoundMove();
            if (dir == null) System.exit(0);

            if (isValidMove(activeHound, dir)) {
                gameState.getTable().doMove(new Move(activeHound, dir));
                break;
            }
        }
    }

    // 4. CENTRALIZED AI LOGIC
    private void aiMove(String actor) {
        System.out.println("AI " + actor + " is thinking...");
        FoxHoundsState currentState = new FoxHoundsState(gameState.getTable());
        Move bestAiMove = AIPlayer.findBestMove(currentState, AI_SEARCH_DEPTH);

        if (bestAiMove != null) {
            gameState.getTable().doMove(bestAiMove);
        } else {
            if (actor.equals("Fox")) gameState.getTable().doARandomFoxMove();
            else gameState.getTable().doARandomHoundMove();
        }
    }

    // 5. CENTRALIZED VALIDATION LOGIC
    private boolean isValidMove(Figure mover, Direction dir) {
        int targetRow = mover.getRow() + dir.getRowStep();
        int targetCol = mover.getCol() + dir.getColStep();
        int size = gameState.getSize();

        // Check Boundaries
        if (targetRow < 0 || targetRow >= size || targetCol < 0 || targetCol >= size) {
            gameIO.showError("Invalid move! That space is off the board.");
            return false;
        }

        // Check Collisions
        boolean isOccupiedByHound = gameState.getTable().getHound(targetRow, targetCol) != null;
        boolean isOccupiedByFox = targetRow == gameState.getTable().getFox().getRow() && targetCol == gameState.getTable().getFox().getCol();

        if (isOccupiedByHound || isOccupiedByFox) {
            gameIO.showError("Invalid move! That space is occupied by another piece.");
            return false;
        }

        return true;
    }

    // 6. HELPER METHODS
    private boolean isGameOver() {
        return gameState.getTable().winFox() || gameState.getTable().winHounds();
    }

    private void announceWinner() {
        if (gameState.getTable().winFox()) {
            System.out.println(gameState.getHumanPlaysWithFox() ? "You won!" : "You loose!");
        } else if (gameState.getTable().winHounds()) {
            System.out.println(!gameState.getHumanPlaysWithFox() ? "You won!" : "You loose!");
        }
    }
}