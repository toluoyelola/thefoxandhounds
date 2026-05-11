package thefoxandthehounds.businesslogic;

/**
 * an implementation of the figure interface
 * represents the position of the fox on the bord
 */
public class Fox implements Figure {
    private int  row,col;
    public Fox(int p_row, int p_col) {
        row = p_row;  col = p_col;
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
    public void setRow(int p_row) {
        this.row = p_row;
    }

    @Override
    public String toString() {
        return "Fox{" + "row=" + row + ", col=" + col + '}';
    }
}
