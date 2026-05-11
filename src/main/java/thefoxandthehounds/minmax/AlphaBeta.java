package thefoxandthehounds.minmax;

public class AlphaBeta {

    public static <M> int minimax(GameState<M> state, int depth, int alpha, int beta) {

        if (depth == 0 || state.isTerminal()) {
            return state.evaluate();
        }

        if (state.isMaxPlayerTurn()) {

            int value = Integer.MIN_VALUE;

            for (M move : state.getPossibleMoves()) {
                GameState<M> child = state.makeMove(move);

                value = Math.max(value,
                        minimax(child, depth - 1, alpha, beta));

                alpha = Math.max(alpha, value);

                if (alpha >= beta) {
                    break; // beta vágás
                }
            }

            return value;

        } else {

            int value = Integer.MAX_VALUE;

            for (M move : state.getPossibleMoves()) {
                GameState<M> child = state.makeMove(move);

                value = Math.min(value,
                        minimax(child, depth - 1, alpha, beta));

                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    break; // alfa vágás
                }
            }

            return value;
        }
    }
}
