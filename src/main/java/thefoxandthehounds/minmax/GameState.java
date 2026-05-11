package thefoxandthehounds.minmax;

import java.util.List;

public interface GameState<M> {

    boolean isTerminal();                 // is the state terminal?
    int evaluate();                       // we don't need heuristic value, it is only the final value of the game state

    List<M> getPossibleMoves();           // possible moves

    GameState<M> makeMove(M move);        // returns the result of the move

    boolean isMaxPlayerTurn();            // who is on move?
}
