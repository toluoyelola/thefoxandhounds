package thefoxandthehounds.playonterminal;

import thefoxandthehounds.businesslogic.Fox;
import thefoxandthehounds.businesslogic.Hound;
import thefoxandthehounds.businesslogic.Table;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Logger;

import static java.lang.System.in;

public class DatabaseManager {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:h2:~/test";//jdbc:h2:~/test,  jdbc:h2:tcp://localhost/~/test
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";



    public GameState loadGame() {
        GameState gameState = new GameState();
        try {
            Class.forName("org.h2.Driver");
            loadGameFromDatabase(gameState);
        } catch (ClassNotFoundException e) {
            logger.severe("H2 database not found");
        }
        return gameState;
    }

    public void saveGame(String gameName, GameState gameState) {
        if (gameName == null) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String tableDescription = createTableDescription(gameState);
            String queryToInsert = createInsertQuery(gameName, gameState, tableDescription);

            System.out.println("queryToInsert: " + queryToInsert);
            PreparedStatement insertStatement = connection.prepareStatement(queryToInsert);
            System.out.println("Rows affected: " + insertStatement.executeUpdate());
        } catch (SQLException sqlex) {
            logger.severe("Failed to save game to database: " + sqlex.getMessage());
        }
    }

    private String createTableDescription(GameState gameState) {
        StringBuilder sb = new StringBuilder();
        Character[][] matrix = gameState.getTable().getMatrix();
        int size = gameState.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                sb.append(matrix[row][col]);
            }
        }
        return sb.toString();
    }

    private String createInsertQuery(String gameName, GameState gameState, String tableDescription) {
        return "INSERT INTO SavedGameFoxAndHounds " +
                "(SIZE,NAME,TABLEDESCRIPTION,IS_FOX_ON_MOVE,IS_HUMAN_WITH_FOX) " +
                "VALUES (" +
                gameState.getSize() + "," +
                "'" + gameName + "'," +
                "'" + tableDescription + "'," +
                (gameState.getFoxIsOnMove() ? 1 : 0) + "," +
                (gameState.getHumanPlaysWithFox() ? 1 : 0) + ");";
    }

    private void loadGameFromDatabase(GameState gameState) {
        String queryForAllSavedGames = "SELECT * FROM SavedGameFoxAndHounds ORDER BY ID;";
        String queryForOneSavedGameByID = "SELECT SIZE,NAME,TABLEDESCRIPTION,IS_FOX_ON_MOVE,IS_HUMAN_WITH_FOX " +
                "FROM SavedGameFoxAndHounds WHERE ID = ?;";

        // First get the ID from all saved games
        int selectedID = getGameIdFromUser(queryForAllSavedGames);
        if (selectedID == -1) {
            return;
        }

        // Then load the specific game
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(queryForOneSavedGameByID);
            preparedStatement.setInt(1, selectedID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Update GameState with database values
                int size = resultSet.getInt("SIZE");
                boolean humanPlaysWithFox = resultSet.getInt("IS_HUMAN_WITH_FOX") == 1;
                boolean foxIsOnMove = resultSet.getInt("IS_FOX_ON_MOVE") == 1;
                String tableDescription = resultSet.getString("TABLEDESCRIPTION");

                // Set values in GameState
                gameState.setSize(size);
                gameState.setHumanPlaysWithFox(humanPlaysWithFox);
                gameState.setFoxIsOnMove(foxIsOnMove);

                // Create and populate table
                Table table = Table.getEmptyTable(size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        if (tableDescription.charAt(row * size + col) == 'h') {
                            table.addHound(new Hound(row, col));
                        }
                        if (tableDescription.charAt(row * size + col) == 'f') {
                            table.addFox(new Fox(row, col));
                        }
                    }
                }
                gameState.setTable(table);
                gameState.setLoadedGame(true);
            }
        } catch (SQLException ex) {
            logger.severe("There is a problem with the chosen ID: " + ex.getSQLState());
            throw new RuntimeException("Failed to load game data", ex);
        }
    }

    private int getGameIdFromUser(String query) {
        int id = -1;
        int maxId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Here come the names of the saved games:");
            while (resultSet.next()) {
                id = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                System.out.println(id + " " + name);
            }
            maxId = id;

        } catch (SQLException sqlex) {
            logger.severe("Loading of saved games from db failed");
            return -1;
        }

        // This part should ideally be in GameIO
        return GameIO.getGameIdFromUser(1, maxId);
    }
}