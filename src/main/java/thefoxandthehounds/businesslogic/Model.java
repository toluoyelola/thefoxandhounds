package thefoxandthehounds.businesslogic;

/**
 * accesses and modifies table
 */
public class Model {
    Table table;


    public Model(int N) {
        table = Table.getEmptyTable(N);
    }

    // Default constructor for loading a saved game
    public Model() {}


    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}