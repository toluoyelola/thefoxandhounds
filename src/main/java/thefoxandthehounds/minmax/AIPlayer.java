package thefoxandthehounds.minmax;

public class AIPlayer {

    public static <M> M findBestMove(GameState<M> state, int depth) {

        M bestMove = null;
        int bestValue = Integer.MAX_VALUE;

        for (M move : state.getPossibleMoves()) {

            GameState<M> child = state.makeMove(move);

            int value = AlphaBeta.minimax(
                    child,
                    depth - 1,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
            );

            if (value < bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }
}
