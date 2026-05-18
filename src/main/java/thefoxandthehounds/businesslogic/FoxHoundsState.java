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
        if (table.winFox()) return 10000;
        if (table.winHounds()) return -10000;

        int score = 0;
        Fox fox = table.getFox();

        // 1. FOX PROGRESS (Max player wants higher score)
        // Heavily reward the fox for getting closer to row 0
        score += (table.getTableSize() - fox.getRow()) * 100;

        // 2. FOX MOBILITY (How many moves does the fox have right now?)
        // A trapped fox is a dead fox. Hounds want to minimize this, Fox wants to maximize it.
        table.determineFoxPossibleMoves();
        score += table.foxPossibleMoves.size() * 15;

        // 3. HOUND EVALUATION (Min player wants to lower the score)
        for (Hound h : table.hounds) {
            // If a hound is BEHIND the fox (higher row number), it can never move backward to catch it.
            // This hound is useless. We penalize the hounds (increase the score for the fox)
            if (h.getRow() > fox.getRow()) {
                score += 300;
            } else {
                // Hound is in front of the fox. Good for hounds!
                // We use Manhattan distance, but weigh vertical distance slightly more
                // because blocking the forward path is more important than horizontal distance.
                int verticalDist = Math.abs(h.getRow() - fox.getRow());
                int horizontalDist = Math.abs(h.getCol() - fox.getCol());

                // Hounds want this distance to be as small as possible
                score += (verticalDist * 10) + (horizontalDist * 5);
            }
        }

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