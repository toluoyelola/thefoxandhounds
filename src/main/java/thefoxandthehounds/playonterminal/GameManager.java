package thefoxandthehounds.playonterminal;
import thefoxandthehounds.playonterminal.DatabaseManager;
import java.util.logging.Logger;

public class GameManager {private static final Logger logger = Logger.getLogger(GameManager.class.getName());
    private GameState gameState;
    private GameIO gameIO;
    private DatabaseManager dbManager;
    private TableEditor tableEditor;

    public GameManager() {
        this.gameIO = new GameIO();
        this.dbManager = new DatabaseManager();
        this.gameState = new GameState();
        this.tableEditor = new TableEditor(gameIO, gameState);
    }
    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.startGame();
    }

    public void startGame() {
        boolean isLoadedGame = gameIO.askForGameLoading();
        if (isLoadedGame) {
            gameState = dbManager.loadGame();
        } else {
            int size = gameIO.readTableSize();
            gameState = new GameState(size);
            TableEditor editor = new TableEditor(gameIO, gameState);  // Pass both parameters
            editor.editTable();
        }

        System.out.println("The table to play is: " + gameState.getTable());
        if (gameState.getHumanPlaysWithFox() == null) {
            gameState.setHumanPlaysWithFox(gameIO.chooseSide());
        }

        playGame();
        endGame();
    }

    private void playGame() {
        GamePlay gamePlay = new GamePlay(gameIO, gameState);
        gamePlay.play();
    }

    public void endGame() {
        String saveName = gameIO.getGameSaveName();
        dbManager.saveGame(saveName, gameState);
    }
}
