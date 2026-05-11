package thefoxandthehounds.playonterminal;
import thefoxandthehounds.businesslogic.*;

import thefoxandthehounds.businesslogic.Table;

public class GameState {
    private int size;
    private Table table;
    private Boolean humanPlaysWithFox;
    private Boolean foxIsOnMove;


    public GameState() {
        this.size = -1;
        this.table = null;
        this.humanPlaysWithFox = null;
        this.foxIsOnMove = null;

    }

    public GameState(int size) {
        validateSize(size);
        this.size = size;
        this.table = Table.getEmptyTable(size);
        this.humanPlaysWithFox = null;
        this.foxIsOnMove = null;
    }

    private void validateSize(int size) {
        if (!(size <= 12 && size >= 4 && size % 2 == 0)) {
            throw new IllegalArgumentException("Size must be an even number between 4 and 12");
        }
    }

    // State manipulation methods
    public void initializeEmptyTable() {
        this.table = Table.getEmptyTable(size);
    }

    public void initializeStarterTable() {
        this.table = Table.getStarterTable(size);
    }

    public void addFox(int row, int col) {
        this.table.addFox(new Fox(row, col));
    }

    public void addHound(int row, int col) {
        this.table.addHound(new Hound(row, col));
    }

    public void makeMove(Move move) {
        this.table.doMove(move);
        this.foxIsOnMove = !this.foxIsOnMove;
    }

    public boolean isGameOver() {
        return table.winFox() || table.winHounds();
    }

    public String getWinner() {
        if (table.winFox()) return "Fox";
        if (table.winHounds()) return "Hounds";
        return null;
    }

    // Getters and Setters
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        validateSize(size);
        this.size = size;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Boolean getHumanPlaysWithFox() {
        return humanPlaysWithFox;
    }

    public void setHumanPlaysWithFox(Boolean humanPlaysWithFox) {
        this.humanPlaysWithFox = humanPlaysWithFox;
    }

    public Boolean getFoxIsOnMove() {
        return foxIsOnMove;
    }

    public void setFoxIsOnMove(Boolean foxIsOnMove) {
        this.foxIsOnMove = foxIsOnMove;
    }



    // Utility methods
    public Hound getHound(int row, int col) {
        return table.getHound(row, col);
    }

    public Fox getFox() {
        return table.getFox();
    }

    public void doRandomHoundMove() {
        table.doARandomHoundMove();
        foxIsOnMove = true;
    }

    public void doRandomFoxMove() {
        table.doARandomFoxMove();
        foxIsOnMove = false;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "size=" + size +
                ", table=" + table +
                ", humanPlaysWithFox=" + humanPlaysWithFox +
                ", foxIsOnMove=" + foxIsOnMove +
                '}';
    }
}
