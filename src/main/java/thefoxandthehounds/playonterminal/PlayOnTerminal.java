
package thefoxandthehounds.playonterminal;

import thefoxandthehounds.businesslogic.Direction;
import thefoxandthehounds.businesslogic.Fox;
import thefoxandthehounds.businesslogic.Hound;
import thefoxandthehounds.businesslogic.Move;
import thefoxandthehounds.businesslogic.Table;

import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import static java.lang.System.in;


public class PlayOnTerminal {

    static final Logger logger = Logger.getLogger(PlayOnTerminal.class.getName());
    static int size = -1;
    static Table table = null;
    static Boolean humanPlaysWithFox = null;
    static Scanner scanner = null;
    static boolean isLoadedAGame = false;
    static Boolean foxIsOnMove = null;


    public static void main(String[] args) {
        isLoadedAGame =  questionForLoading();
        if (!isLoadedAGame) {
            readInSize();
            editing();
        }
        System.out.println("The table to play is: "+table);
        readInSide();
        playing();
        saveThePlayedGame();
    }


    public static boolean questionForLoading() {
        scanner = new Scanner(in);
        System.out.println("Do you want to load a saved game from the database? ");
        String read = null;
        char answer = ' ';
        while (!(answer == 'y' || answer == 'n')) {
            System.out.print("Give 'y' or 'n' as answer: \n ");
            read = scanner.nextLine();
            answer = read.toLowerCase().charAt(0);
        }
        if (answer == 'n' ) {return false;}
        else {
            try {
                Class.forName ("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                logger.severe("H2 database not found");
            }
            loadAGame();
            return true;
        }




    }

    /**
     * the method gets info from the databasa to fill in the board
     */
    private static void loadAGame() {
        String queryForAllSavedGames = "SELECT * FROM  SavedGameFoxAndHounds ORDER BY ID;";
        String queryForOneSavedGameByID =  "SELECT SIZE,NAME,TABLEDESCRIPTION,IS_FOX_ON_MOVE,IS_HUMAN_WITH_FOX "+
                "FROM  SavedGameFoxAndHounds WHERE ID = ?;";
        int ID = -1;
        int IDMAX = -1;
        try (Connection connection = DriverManager.getConnection
                ("jdbc:h2:tcp://localhost/~/test", "sa", "")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryForAllSavedGames);
            System.out.println("Here come the names of the saved games:");
            while (resultSet.next()) {
                ID = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                System.out.println(ID + " " + name);
            }
            resultSet.close();
            statement.close();
            IDMAX = ID;
            scanner = new Scanner(in);
            ID = -1;
            while (!(1 <= ID && ID <= IDMAX)) {
                System.out.println("Please select an existing ID (the max is: "+IDMAX+") of a saved game which you will play: ");
                String readLine = scanner.nextLine();
                try {
                    ID = Integer.parseInt(readLine);
                } catch (NumberFormatException exception) {
                    logger.warning("wrong format for a number");
                }
            }
        } catch (SQLException sqlex) {logger.severe("loading of names of saved games from db failed");}

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "")) {

            PreparedStatement preparedStatement = connection.prepareStatement(queryForOneSavedGameByID);
            preparedStatement.setInt(1,ID);
            ResultSet resultSet2 = preparedStatement.executeQuery();
            resultSet2.next();
            size = resultSet2.getInt("SIZE");
            humanPlaysWithFox = (resultSet2.getInt("IS_HUMAN_WITH_FOX")==1)?true:false;
            foxIsOnMove = (resultSet2.getInt("IS_FOX_ON_MOVE")==1)?true:false;
            String tableDescription =  resultSet2.getString("TABLEDESCRIPTION");
            table = Table.getEmptyTable(size);
            for (int row=0; size>row;row++) {
                for (int col=0; size>col;col++) {
                    if (tableDescription.charAt(row*size+col)=='h') {
                        table.addHound(new Hound(row,col));
                    }
                    if (tableDescription.charAt(row*size+col)=='f') {
                        table.addFox(new Fox(row,col));
                    }

                }
            }
            isLoadedAGame = true;


        }
        catch(SQLException ex) {logger.severe("There is a problem with the chosen ID: "+ex.getSQLState());}



    }


    public static void readInSize() {
        scanner = new Scanner(in);
        System.out.print("Give the size of the table as an even integer between 4 and 12: ");
        String read = null;
        while (!(size <= 12 && size >= 4 && size % 2 == 0)) {
            try {
                read = scanner.nextLine();
                size = Integer.parseInt(read);
            } catch (NumberFormatException ex) {
                logger.warning("not integer is given as size");
                System.out.print("Give integer as size \n ");
            }
            if (!(size <= 12 && size >= 4 && size % 2 == 0)) {
                System.out.print("\nGive the size of the table as an even integer between 4 and 12: ");
            }
            else System.out.print("Ok, the size of the table: " + size + "\n");
        }
    }

    /**
     * lets player play wit either hound or fox
     */
    public static void readInSide() {
        if (humanPlaysWithFox == null) {
            scanner = new Scanner(in);
            String read = null;
            System.out.print("\nWould you lead fox (f) or hounds (h) or will you exit (x)?: ");
            boolean player_determined = false;
            read = null;
            while (!player_determined) {
                String line = scanner.nextLine();
                char c = line.charAt(0);
                if (Arrays.asList('f', 'h', 'x', 'F', 'H', 'X').contains(c)) {
                    player_determined = true;
                    switch (Character.toLowerCase(c)) {
                        case ('f'):
                            humanPlaysWithFox = true;
                            System.out.println("You will play with the fox");
                            break;
                        case ('h'):
                            humanPlaysWithFox = false;
                            System.out.println("You will play with the hounds");
                            break;
                        case ('x'):
                            humanPlaysWithFox = null;
                            System.exit(0);
                            break;
                    }
                } else {
                    System.out.println("Once again, would you lead fox (f) or hounds (h) or will you exit (x)?: ");

                }

            }
        }
    }


    /**
     * lets the player choose whick kind of table they want to play from
     */
    public static void editing() {
        Table table = Table.getStarterTable(size);
        System.out.println("Editing happens now from empty table.\n"+
                "How do you wish the editing? From empty table (e)\n"+
                "or from the starter table by steps(s) "+"or with a random table? (r)\n"+"x -exit(x)"+
                "random does not work yet");
        Character userInput = ' ';
        scanner = new Scanner(in);
        while (!Arrays.asList('e','s','x').contains(Character.toLowerCase(userInput))) {
            System.out.println("e/s/x: ");
            userInput = scanner.nextLine().charAt(0);
        }
        switch (Character.toLowerCase(userInput)) {

            case 'e': {
                editingFromEmptyTable(); break;
            }
            case 's': {
                editingFromStartingPosition(); break;
            }

            default: System.exit(0);

        }
        return;
    }

    public static void editingFromEmptyTable() {
        scanner = new Scanner(in);
        table = Table.getEmptyTable(size);
        System.out.println("Input row and column coordinates of the fox  indexed starting with 0: ");
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        int row = Integer.parseInt(lineScanner.next());
        int col= Integer.parseInt(lineScanner.next());
        table.addFox(new Fox(row,col));
        for (int i=1; i<=size/2; i++) {
            System.out.println("Input the "+i+". hounds row and column positions  indexed starting with 0: ");
            line = scanner.nextLine();
            lineScanner = new Scanner(line);
            row = Integer.parseInt(lineScanner.next());
            col= Integer.parseInt(lineScanner.next());
            table.addHound(new Hound(row,col));
        }
        return;
    }

    public static void editingFromStartingPosition() {
        scanner = new Scanner(in);
        boolean exit = false;
        Character c;
        Hound activeHound = null;
        table = Table.getStarterTable(size);
        System.out.println("THe starting table: "+table);
        c = 'a';
        while (Arrays.asList('a','d','q','e','x').contains(Character.toLowerCase(c))) {
            System.out.println("GIve a step of the fox: a-southwest/d-southeast/q-northwest/e-northeast, p - out from editing");
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': table.doMove(new Move(table.getFox(),Direction.SOUTHWEST)); break;
                case 'd': table.doMove(new Move(table.getFox(),Direction.SOUTHEAST)); break;
                case 'q': table.doMove(new Move(table.getFox(),Direction.NORTHWEST)); break;
                case 'e': table.doMove(new Move(table.getFox(),Direction.NORTHEAST)); break;
                case 'p': exit = true; break;
                default: break;
            }
            if (exit) return;
            System.out.println("THe table is now: "+table); //DEBUG

            boolean isHoundFound = false;
            while (!isHoundFound) {
                System.out.println("Which hound is to move? A row and a column should be given: ");
                int y = scanner.nextInt(); int x = scanner.nextInt();
                activeHound = table.getHound(y, x);
                if (activeHound == null)  {
                    System.out.println("THere is no hound.");
                } else isHoundFound=true;
            }
            System.out.println("What is the direction of the move?: a(southwest)/d(southeast), p - out from editing");

            scanner.nextLine();
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': table.doMove(new Move(activeHound,Direction.SOUTHWEST)); break;
                case 'd': table.doMove(new Move(activeHound,Direction.SOUTHEAST)); break;
                case 'p': exit = true; break;
                default: break;
            }
            if (exit) return;
            System.out.println("The table is edited by moves:  "+table); //DEBUG
        }
    }



    public static void playing() {
        System.out.println("The starting table is: "+table);
        if (humanPlaysWithFox) {
            playWithFox();

        }
        else {
            playWithHounds();
        }

    }

    private static Direction directionByChar(Character c){
        switch (Character.toLowerCase(c.charValue())  ) {
            case 'a': return Direction.SOUTHWEST;
            case 'q': return Direction.NORTHWEST;
            case 'd': return Direction.SOUTHEAST;
            case 'e': return Direction.NORTHEAST;
            default: return null;
        }
    }

    public static void playWithFox() {
        if (foxIsOnMove == null) foxIsOnMove = true;
        if (!foxIsOnMove)  {
            if (table.winFox()) {System.out.println("You won");  return;}
            if (table.winHounds()) {System.out.println("You loose");  return; }
            table.doARandomHoundMove();
            foxIsOnMove=true;
            System.out.println("The table is now: "+table);
        }
        char c = ' ';
        scanner = new Scanner(in);
        while (true) {
            if (table.winFox()) {System.out.println("You won");  return;}
            if (table.winHounds()) {System.out.println("You loose");  return; }
            System.out.print("\nYour move: a/d/q/e, exit: x ");
            String s = scanner.nextLine();
            c = Character.toLowerCase(s.charAt(0));
            while(!Arrays.asList('a','d','q','e','x').contains(c )) {
                System.out.println("Invalid move! Please enter a/d/q/e for movement or x to exit");
                s = scanner.nextLine();
                c = Character.toLowerCase(s.charAt(0));

            }
            if (c == 'x') return;
            table.doMove(new Move(table.getFox(),directionByChar(c)));
            foxIsOnMove = false;
            System.out.println("The table is now: "+table);
            table.doARandomHoundMove();
            foxIsOnMove=true;
            System.out.println("The table is now: "+table);
        }
    }

    private static void playWithHounds() {
        if (foxIsOnMove==null) foxIsOnMove=false;
        if (foxIsOnMove)  {
            if (table.winFox()) {System.out.println("You loose"); return;}
            if (table.winHounds()) {System.out.println("You won"); return; }
            table.doARandomFoxMove();
            foxIsOnMove=false;
            System.out.println("The table is now: "+table);
        }

        scanner = new Scanner(in);
        char c = ' ';
        Hound activeHound = null;
        boolean exit = false;
        while (true) {
            if (table.winFox()) {System.out.println("You loose"); return;}
            if (table.winHounds()) {System.out.println("You win"); return; }
            boolean isHoundFound = false;
            while (!isHoundFound) {
                System.out.println("Which hound is to move? A row and a column should be given: (exit: -1)");
                int y = scanner.nextInt(); int x = scanner.nextInt();
                if (y == -1) return;
                activeHound = table.getHound(y, x);
                if (activeHound == null)  {
                    System.out.println("There is no hound found.");
                } else isHoundFound=true;
            }
            System.out.println("Give a step of that hound: a/d, x - exit");

            scanner.nextLine(); // a nextInt() után beveszi az újsor-karaktert
            c = scanner.nextLine().charAt(0);
            switch (Character.toLowerCase(c)) {
                case 'a': table.doMove(new Move(activeHound,Direction.SOUTHWEST)); break;
                case 'd': table.doMove(new Move(activeHound,Direction.SOUTHEAST)); break;
                case 'x': exit = true; break;
                default: break;
            }
            if (exit) return;
            foxIsOnMove=true;
            System.out.println("The table is now: "+table);
            table.doARandomFoxMove();
            foxIsOnMove=false;
            System.out.println("The table is now: "+table);
        }
    }

    private static void saveThePlayedGame() {
        System.out.println("Possible saving of the actual played game");
        try (Connection connection =
                     DriverManager.getConnection
                             ("jdbc:h2:tcp://localhost/~/test", "sa", "")) {
            System.out.println("You can save the actual game.");
            System.out.println("Please type in a new name without spaces otherwise just push an enter");
            scanner = new Scanner(in);
            String read = scanner.nextLine();
            if (read==null || read.length()==0) {
                System.out.println("No saving happened");
                return;
            }
            String askedName = Arrays.stream(read.split(" ")).findFirst().get();
            StringBuilder sb = new StringBuilder();
            Character[][] matrix = table.getMatrix();
            for (int row=0;size>row;row++) for (int col=0;size>col;col++) {
                sb.append(matrix[row][col]);
            }
            String tableDescriptionAsString = sb.toString();
            String queryToInsert = "INSERT INTO SavedGameFoxAndHounds"+
                    "(SIZE,NAME,TABLEDESCRIPTION,IS_FOX_ON_MOVE,IS_HUMAN_WITH_FOX)" +
                    "VALUES" +
                    "("+4+
                    ",'"+askedName+"','"+tableDescriptionAsString+"',"+(foxIsOnMove?1:0)+","+
                    (humanPlaysWithFox?1:0)+");";
            System.out.println("queryToInsert: "+queryToInsert);
            PreparedStatement insertStatement = connection.prepareStatement(queryToInsert);
            System.out.println("Ennyi sor változott"+insertStatement.executeUpdate());
        } catch (SQLException sqlex) {logger.severe("loading of names of saved games from db failed");}


    }


}
    