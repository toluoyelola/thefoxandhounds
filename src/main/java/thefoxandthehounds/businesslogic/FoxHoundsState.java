package thefoxandthehounds.businesslogic;

import thefoxandthehounds.minmax.GameState;

import java.util.List;

public class FoxHoundsState implements GameState<Move> {

    private Table table;

    public FoxHoundsState(Table table) {
        this.table = table;
    }

    @Override
    public boolean isTerminal() {
        return table.winFox() || table.winHounds();
    }

    @Override
    public int evaluate() {
        // If the game is over, assign massive points.
        if (table.winFox()) return 1000;
        if (table.winHounds()) return -1000;

        // HEURISTIC: Unlike Tic-Tac-Toe, we need to score mid-game boards.
        // The Fox wants to reach row 0. So, the closer to 0, the higher the score.
        int score = (table.getTableSize() - table.getFox().getRow()) * 10;
        return score;
    }

    @Override
    public List<Move> getPossibleMoves() {
        if (isMaxPlayerTurn()) {
            table.determineFoxPossibleMoves();
            return table.foxPossibleMoves;
        } else {
            table.determineHoundsPossibleMoves();
            return table.houndsPossibleMoves;
        }
    }

    @Override
    public GameState<Move> makeMove(Move move) {
        // 1. Create a clone so we don't mess up the real game
        Table nextTable = table.cloneTable();

        // 2. We have to map the old move to the new cloned figures
        Figure mover = move.getMover();
        Figure clonedMover = null;

        if (mover instanceof Fox) {
            clonedMover = nextTable.getFox();
        } else {
            // Find the matching hound in the cloned table
            for (Hound h : nextTable.hounds) {
                if (h.getRow() == mover.getRow() && h.getCol() == mover.getCol()) {
                    clonedMover = h;
                    break;
                }
            }
        }

        // 3. Apply the move to the cloned table
        nextTable.doMove(new Move(clonedMover, move.getDirection()));
        return new FoxHoundsState(nextTable);
    }

    @Override
    public boolean isMaxPlayerTurn() {
        // Let's assume Fox is Max, Hounds are Min
        return table.isFoxOnMOve();
    }
}
