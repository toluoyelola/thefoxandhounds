package thefoxandthehounds.businesslogic;

import thefoxandthehounds.minmax.GameState;
import java.util.List;

public class FoxHoundsState implements GameState<Move> {

    private Table table;
    private boolean isFoxTurn; // We will explicitly track the turn here

    public FoxHoundsState(Table table, boolean isFoxTurn) {
        this.table = table;
        this.isFoxTurn = isFoxTurn;
    }

    @Override
    public boolean isTerminal() {
        return table.winFox() || table.winHounds();
    }

    @Override
    public int evaluate() {
        if (table.winFox()) return 1000;
        if (table.winHounds()) return -1000;

        // HEURISTIC UPGRADE:
        // 1. Fox wants to reach row 0 (Max player wants high score)
        int score = (table.getTableSize() - table.getFox().getRow()) * 100;

        // 2. Hounds (Min player) want to lower the score.
        // We add the distance between the hounds and the fox to the score.
        // The closer the hounds get, the lower the score becomes!
        int distancePenalty = 0;
        for (Hound h : table.hounds) {
            distancePenalty += Math.abs(h.getRow() - table.getFox().getRow()) +
                    Math.abs(h.getCol() - table.getFox().getCol());
        }

        return score + distancePenalty;
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
        Table nextTable = table.cloneTable();
        Figure mover = move.getMover();
        Figure clonedMover = null;

        if (mover instanceof Fox) {
            clonedMover = nextTable.getFox();
        } else {
            for (Hound h : nextTable.hounds) {
                if (h.getRow() == mover.getRow() && h.getCol() == mover.getCol()) {
                    clonedMover = h;
                    break;
                }
            }
        }

        nextTable.doMove(new Move(clonedMover, move.getDirection()));
        // Pass the OPPOSITE turn to the next level of the simulation
        return new FoxHoundsState(nextTable, !isFoxTurn);
    }

    @Override
    public boolean isMaxPlayerTurn() {
        return isFoxTurn;
    }
}