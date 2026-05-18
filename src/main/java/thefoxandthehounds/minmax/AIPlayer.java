package thefoxandthehounds.minmax;

public class AIPlayer {

    public static <M> M findBestMove(GameState<M> state, int depth) {

        M bestMove = null;
        int bestValue = Integer.MAX_VALUE;

        int color = state.isMaxPlayerTurn() ? 1 : -1;

        for (M move : state.getPossibleMoves()) {
            GameState<M> child = state.makeMove(move);

            // Notice the negative sign and the swapped/inverted alpha-beta bounds.
            // This is the core magic of Negamax.
            int value = -negamax(child, depth - 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, -color);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static <M> int negamax(GameState<M> state, int depth, int alpha, int beta, int color) {
        if (depth == 0 || state.isTerminal()) {
            // We multiply the static evaluation by the color of the current player.
            return color * state.evaluate();
        }

        int maxVal = Integer.MIN_VALUE;

        for (M move : state.getPossibleMoves()) {
            GameState<M> child = state.makeMove(move);

            int value = -negamax(child, depth - 1, -beta, -alpha, -color);

            maxVal = Math.max(maxVal, value);
            alpha = Math.max(alpha, value);

            // Alpha-Beta Pruning
            if (alpha >= beta) {
                break;
            }
        }
        return maxVal;
    }

}
