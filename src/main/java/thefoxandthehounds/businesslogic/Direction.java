package thefoxandthehounds.businesslogic;

/**
 * Represents the 4 diagonal directions a piece from the game can move
 */
public enum Direction {
    NORTHWEST(-1,-1), NORTHEAST(-1,1),
    SOUTHWEST(1,-1),SOUTHEAST(1,1);
    final private int rowStep;
    final private int colStep;
    public int getRowStep() {return rowStep;}
    public int getColStep() {return colStep;}
    private Direction(int row, int column) {rowStep=row; colStep=column;}
}
