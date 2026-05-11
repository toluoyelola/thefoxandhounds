package thefoxandthehounds.playonterminal;

import java.util.logging.Logger;

public class GameManager {
    private static final Logger logger = Logger.getLogger(GameManager.class.getName());
    private GameState gameState;
    private GameIO gameIO;
    private TableEditor tableEditor;

    public GameManager() {
        this.gameIO = new GameIO();
        this.gameState = new GameState();
        this.tableEditor = new TableEditor(gameIO, gameState);
    }

    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.startGame();
    }

    public void startGame() {
        // Removed the question to load a saved game. Always starts fresh.
        int size = gameIO.readTableSize();
        gameState = new GameState(size);
        TableEditor editor = new TableEditor(gameIO, gameState);
        editor.editTable();

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
        // Removed the saving logic. Just print a goodbye message.
        System.out.println("Game Over. Thanks for playing!");
    }
}