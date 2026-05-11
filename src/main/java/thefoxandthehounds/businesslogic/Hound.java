package thefoxandthehounds.businesslogic;
/**
 * an implementation of the figure interface
 * represents the position of the hound on the bord
 */
public class Hound implements Figure{
    private int row,
            col;

    public Hound(int p_row, int p_col) {
        col = p_col;
        row = p_row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int p_col) {
        this.col = p_col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int p_row) {this.row = p_row;}

    @Override
    public String toString() {
        return "Hound{" + "row=" + row + ", col=" + col + '}';
    }
}
