package thefoxandthehounds.businesslogic;

/**
 * represents a move of the fox or hound
 */
public class Move {
    private Figure mover;
    private Direction direction;

    public Move(Figure p_mover) {
        mover = p_mover;
    }

    public Move(Figure p_mover, Direction p_direction) {
        mover = p_mover;
        direction = p_direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction p_direction) {
        this.direction = p_direction;
    }

    public Figure getMover() {
        return mover;
    }

    public void setMover(Figure p_mover) {
        this.mover = p_mover;
    }

    @Override
    public String toString() {
        return "Move{" + "direction=" + direction + ", mover=" + mover + '}';
    }
}
