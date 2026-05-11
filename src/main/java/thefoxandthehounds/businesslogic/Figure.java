package thefoxandthehounds.businesslogic;

/**
 * The interface is arepresentation of the game pieces of the board
 * it tracks the movement of the pieces
 * and allows the position to change with the getter and setter
 */
public interface Figure {
    int getCol();
    int getRow();
    void setCol(int x);
    void setRow(int a);
}
