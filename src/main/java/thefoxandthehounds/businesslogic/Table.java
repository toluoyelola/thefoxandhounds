package thefoxandthehounds.businesslogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Table {

    Fox fox;

    public List<Hound> hounds = new ArrayList<>();

    Boolean isFoxOnMove;

    public Boolean isFoxOnMOve() {
        return isFoxOnMove;
    }

    public void setFoxOnMove(boolean b) {
        isFoxOnMove = b;
    }

    Character[][] matrix;

    public Character[][] getMatrix() {
        return matrix;
    }

    Boolean isFoxWinning = null;

    int tableSize;

    private boolean fits(int n) {
        return 0 <= n && n < tableSize;
    }

    public void setTableSize(int size) {
        this.tableSize = size;
    }

    public int getTableSize() {
        return tableSize;
    }


    public void addFox(Fox newFox) {
        fox = newFox;
        matrix[fox.getRow()][fox.getCol()] = 'f';
    }

    public Fox getFox() {
        return fox;
    }

    List<Move> foxPossibleMoves = null;

    public void determineFoxPossibleMoves() {
        foxPossibleMoves = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            int row = fox.getRow() + dir.getRowStep();
            int col = fox.getCol() + dir.getColStep();
            if (fits(row) && fits(col)
                    && matrix[row][col].equals(' ')) {
                foxPossibleMoves.add(new Move(fox, dir));
            }
        }

    }

    public boolean winFox() {
        isFoxWinning = (getFox().getRow() <= 0);
        return isFoxWinning;
    }

    public Hound getHound ( int row, int col){
        if (matrix[row][col] != 'h') {
            return null;
        } else {
            Hound answer = null;
            for (Hound h : hounds) {
                if (h.getRow() == row && h.getCol() == col) {
                    answer = h;
                }
            }
            return answer;
        }
    }

    public void addHound (Hound newHound){
        hounds.add(newHound);
        matrix[newHound.getRow()][newHound.getCol()] = 'h';
    }

    List<Move> houndsPossibleMoves = null;

    public void determineHoundsPossibleMoves() {
        houndsPossibleMoves = new ArrayList<>();

        for (Hound hound : hounds) {
            for (Direction dir : Arrays.asList(Direction.SOUTHEAST, Direction.SOUTHWEST)) {
                if (fits(hound.getRow() + dir.getRowStep())
                        && fits(hound.getCol() + dir.getColStep())
                        && matrix[hound.getRow() + dir.getRowStep()][hound.getCol() + dir.getColStep()].equals(' ')) {
                    houndsPossibleMoves.add(new Move(hound, dir));
                }
            }
        }
    }

    public boolean winHounds() {
        determineFoxPossibleMoves();

        return !(getFox().getRow()<=0) && foxPossibleMoves.isEmpty();
    }

    public static Table getStarterTable(int size) {

        if (size <= 3 || size > 12 || size % 2 != 0) {
            return null;
        }
        Table board = new Table();
        board.setTableSize(size);
        board.matrix = new Character[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board.matrix[i][j] = ' ';
            }
        }
        board.fox = new Fox(size - 1, 0);
        board.matrix[size - 1][0] = 'f';
        board.hounds = new ArrayList<>();
        for (int i = 0; i < size / 2; i++) {
            board.hounds.add(new Hound(0, 2 * i + 1));
            board.matrix[0][2 * i + 1] = 'h';
        }
        board.isFoxOnMove = false;

        return board;
    }

    public static Table getEmptyTable(int size) {

        if (size <= 3 || size > 12 || size % 2 != 0) {
            return null;
        }
        Table answer = new Table();
        answer.setTableSize(size);
        answer.matrix = new Character[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                answer.matrix[i][j] = ' ';
            }
        }
        answer.isFoxOnMove = false;
        return answer;

    }

    public void doMove(Move move) {
        Figure fig = move.getMover();
        int row = fig.getRow();
        int col = fig.getCol();
        fig.setRow(row + move.getDirection().getRowStep());
        fig.setCol(col + move.getDirection().getColStep());

        matrix[row][col] = ' ';
        if (fig instanceof Fox) {
            matrix[fig.getRow()][fig.getCol()] = 'f'; // these are the new values
        } else { // The mover is a hound
            matrix[fig.getRow()][fig.getCol()] = 'h'; // these are the new values
        }

        // ONLY toggle this ONCE at the end
        if (isFoxOnMove != null) {
            isFoxOnMove = !isFoxOnMove;
        }
    }

    public void doARandomHoundMove() {
        determineHoundsPossibleMoves();
        int numberOfPossibleMoves = houndsPossibleMoves.size();
        if (numberOfPossibleMoves == 0) {
            return;
        }
            int index = new Random().nextInt(numberOfPossibleMoves);
            doMove(houndsPossibleMoves.get(index));


    }

    public void doARandomFoxMove() {
        determineFoxPossibleMoves();
        int numberOfPossibleMoves = foxPossibleMoves.size();
        if (numberOfPossibleMoves == 0) {
            return;
        }
            int index = new Random().nextInt(numberOfPossibleMoves);
            doMove(foxPossibleMoves.get(index));


    }

    @Override
    public String toString() {
        int size = tableSize;
        char[][] tableAsCharArray = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                tableAsCharArray[row][col] = '0';
            }
        }
        tableAsCharArray[fox.getRow()][fox.getCol()] = 'f';
        for (Hound h : hounds) {
            tableAsCharArray[h.getRow()][h.getCol()] = 'h';
        }
        StringBuilder sb = new StringBuilder("[\n");
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                sb.append(tableAsCharArray[row][col] + " ");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public void clear() {
        fox = null;
        hounds = new ArrayList<>();
        matrix = new Character[tableSize][tableSize];
        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < tableSize; j++) {
                matrix[i][j] = ' ';
            }
        }
    }
    public Table cloneTable() {
        Table copy = new Table();
        copy.setTableSize(this.tableSize);
        copy.matrix = new Character[tableSize][tableSize];

        // Copy the matrix
        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < tableSize; j++) {
                copy.matrix[i][j] = this.matrix[i][j];
            }
        }

        // Copy the Fox
        copy.fox = new Fox(this.fox.getRow(), this.fox.getCol());

        // Copy the Hounds
        copy.hounds = new ArrayList<>();
        for (Hound h : this.hounds) {
            copy.hounds.add(new Hound(h.getRow(), h.getCol()));
        }

        copy.isFoxOnMove = this.isFoxOnMove;
        return copy;
    }


}
