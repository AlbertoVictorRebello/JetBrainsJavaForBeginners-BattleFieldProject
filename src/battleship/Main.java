package battleship;

import java.io.Console;
import java.util.Scanner;

class Main {
    Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final int ROWS = 10;
        final int COLUMNS = 10;

        BattleField game = new BattleField(ROWS, COLUMNS,"Player 1", "Player 2");
        game.setBoardInicialization();
        //System.out.println("\nThe game starts!\n");
        game.showBoard("unknown", BattleShipSymbols.FOG_OF_WAR.symbol);
        game.TakeAShot("unknown");



    }
}

class BattleField {
    Scanner scanner = new Scanner(System.in);
    private String [] playerNames;
    private Board2D [] gameBoards;

    public BattleField(int rows, int columns, String... playerNames) {
        int playersQuantity = playerNames.length;
        Board2D [] newGameBoards = new Board2D [playersQuantity];
        for (int i = 0; i < playersQuantity; i++) {
            newGameBoards [i] = new Board2D(playerNames[i], rows, columns);
        }
        gameBoards = newGameBoards.clone();
    }

    public void showBoard(String playerName) {
        for (Board2D board : gameBoards) {
            if (playerName.equals(board.getName())) {
                board.showMe();
            }
        }
    }

    public void showBoard(String playerName, String symbol) {
        for (Board2D board : gameBoards) {
            if (playerName.equals(board.getName())) {
                board.showMe(symbol);
            }
        }
    }

    public void  setBoardInicialization(){
        int r1;
        int r2;
        int c1;
        int c2;
        String line;

        for (Board2D board : gameBoards) {
            System.out.printf("%s, place your ships to the game field\n", board.getName());
            board.showMe();
            for (Fleet ship : Fleet.values()) {
                boolean settedShip = false;
                while (!settedShip) {
                    System.out.printf("\nEnter the coordinates of the %s (%d cells):\n\n", ship.shipType, ship.length);
                    line = scanner.next();
                    r1 = line.charAt(0) - 65;
                    c1 = Integer.parseInt(line.substring(1) )- 1;
                    line = scanner.next();
                    r2 = line.charAt(0) - 65;
                    c2 = Integer.parseInt(line.substring(1)) - 1;
                    int proximity = 1;
                    if (board.existsSymbolNearTheRange(r1, c1, r2, c2, proximity, BattleShipSymbols.SIDE.symbol)) {
                        System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    } else if (r1 == r2) {
                        if (Math.abs(c2 - c1) + 1 == ship.length) {
                            board.setPlaceRange(r1, c1, r2, c2, BattleShipSymbols.SIDE.symbol);
                            settedShip = true;
                        } else {
                            System.out.printf("\nError! Wrong length of the %s! Try again:\n", ship.shipType);
                        }
                    } else if (c1 == c2) {
                        if (Math.abs( r2 - r1) + 1 == ship.length) {
                            board.setPlaceRange(r1, c1, r2, c2, BattleShipSymbols.SIDE.symbol);
                            settedShip = true;
                        } else {
                            System.out.printf("\nError! Wrong length of the %s! Try again:\n", ship.shipType);
                        }

                    } else {
                        System.out.println("\nError! Wrong ship location! Try again:\n");
                    }

                }
                //break;
                board.showMe();
            }
            System.out.println("Press Enter and pass the move to another player");
            scanner.nextLine();
            scanner.nextLine();
        }
    }

    public void TakeAShot(String playerName) {
        //System.out.println("\nTake a shot!\n");
        boolean validHit = false;
        boolean goodHit;
        boolean existsShipsToSank = true;
        boolean aShipSank;
        String line;
        int r1 = 0;
        int c1 = 0;
        int MaxProximity;
        int opponentBoardId;

        while (existsShipsToSank) {
            for (Board2D board : gameBoards) {
                opponentBoardId = getOpponentBoardId(board.getName());
                gameBoards[opponentBoardId].showMe(true);
                System.out.print("---------------------");
                board.showMe();
                System.out.printf("%s, it's your turn:\n", board.getName());

                while (!validHit) {
                    line = scanner.next();
                    r1 = line.charAt(0) - 65;
                    c1 = Integer.parseInt(line.substring(1)) - 1;
                    if (board.getRows() - 1 < r1 || board.getColumns() - 1 < c1) {
                        System.out.println();
                        System.out.println("\nError! You entered the wrong coordinates! Try again:");
                    } else {
                        validHit = true;
                    }

                }

                goodHit = gameBoards[opponentBoardId].changePlace(r1, c1,
                        BattleShipSymbols.HITTED_SIDE.symbol,
                        BattleShipSymbols.SIDE.symbol);
                if (goodHit) {
                    System.out.println();
                    //board.showMe(true);
                    System.out.println("\nYou hit a ship!");
                    //board.showMe();
                    aShipSank = !gameBoards[opponentBoardId].existsSymbolNearTheRange(r1, c1, 1, BattleShipSymbols.SIDE.symbol);
                    MaxProximity = Math.max(gameBoards[opponentBoardId].getRows(), gameBoards[opponentBoardId].getColumns());
                    existsShipsToSank = gameBoards[opponentBoardId].existsSymbolNearTheRange(r1, c1, MaxProximity, BattleShipSymbols.SIDE.symbol);
                    if (!existsShipsToSank) {
                        System.out.println("\nYou sank the last ship. You won. Congratulations!");
                        continue;
                    } else if (aShipSank) {
                        System.out.println("\nYou sank a ship! Specify a new target:");
                    }
                } else {
                    gameBoards[opponentBoardId].changePlace(r1, c1,
                            BattleShipSymbols.MISSED_SHOT.symbol,
                            BattleShipSymbols.FOG_OF_WAR.symbol);
                    System.out.println();
                    //board.showMe(true);
                    System.out.println("\nYou missed!");
                    //board.showMe();
                }
                validHit = false;
                System.out.println("Press Enter and pass the move to another player");
                scanner.nextLine();
                scanner.nextLine();

            }
        }
    }

    public int getOpponentBoardId(String boardName) {
        for (int i = 0; i < gameBoards.length; i++) {
            if (!gameBoards[i].getName().equals(boardName)) {
                return i;
            }
        }
        return -1;
    }

    public void getScore(Board2D [] boards) {

    }


}

class Board2D {
    private final String name;
    private final int rows;
    private final int columns;
    String [][] places;


    public Board2D(String name, int rows, int columns) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.places = new String [rows][columns];
        setPlaceRange(0, 0, rows - 1, columns - 1, BattleShipSymbols.FOG_OF_WAR.symbol);

        //System.out.println( "New board created");
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public String[][] getPlaces() {
        return places;
    }

    public boolean existsSymbolNearTheRange(int row1, int column1, int row2, int column2, int proximity, String symbol) {
        int rowLimit = row1 > row2 ? row1 + 1: row2 + 1;
        rowLimit = Math.min(rowLimit + proximity, this.rows);
        int columnLimit = column1 > column2 ? column1  + 1: column2 + 1;
        columnLimit = Math.min(columnLimit + proximity, this.columns);
        int rowBase = Math.min(row1, row2);
        rowBase = Math.max(rowBase - proximity, 0);
        int columnBase = Math.min(column1, column2);
        columnBase = Math.max(columnBase - proximity, 0);

        for (int i = rowBase; i < rowLimit; i++) {
            for (int j = columnBase; j < columnLimit; j++) {
                if (symbol.equals(places[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean existsSymbolNearTheRange(int row, int column, int proximity, String symbol) {
        int rowLimit = row + 1;
        rowLimit = Math.min(rowLimit + proximity, this.rows);
        int columnLimit = column + 1;
        columnLimit = Math.min(columnLimit + proximity, this.columns);
        int rowBase = row;
        rowBase = Math.max(rowBase - proximity, 0);
        int columnBase = column;
        columnBase = Math.max(columnBase - proximity, 0);

        for (int i = rowBase; i < rowLimit; i++) {
            for (int j = columnBase; j < columnLimit; j++) {
                if (symbol.equals(places[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setPlaceRange(int row1, int column1, int row2, int column2, String symbol) {
        int rowLimit = row1 > row2 ? row1 + 1: row2 + 1;
        rowLimit = rowLimit <= 0 ? 1 : rowLimit;
        int columnLimit = column1 > column2 ? column1  + 1: column2 + 1;
        columnLimit = columnLimit <= 0 ? 1 : columnLimit;
        int rowBase = Math.min(row1, row2);
        rowBase = Math.max(rowBase, 0);
        int columnBase = Math.min(column1, column2);
        columnBase = Math.max(columnBase, 0);

        for (int i = rowBase; i < rowLimit; i++) {
            for (int j = columnBase; j < columnLimit; j++) {
                places[i][j] = symbol;
            }
        }
    }

    public String[][] getPlaceRange(int row1, int column1, int row2, int column2) {
        int rowLimit = row1 > row2 ? row1 + 1: row2 + 1;
        rowLimit = rowLimit <= 0 ? 1 : rowLimit;
        int columnLimit = column1 > column2 ? column1  + 1: column2 + 1;
        columnLimit = columnLimit <= 0 ? 1 : columnLimit;
        int rowBase = Math.min(row1, row2);
        rowBase = Math.max(rowBase, 0);
        int columnBase = Math.min(column1, column2);
        columnBase = Math.max(columnBase, 0);

        String[][] returnArray = new String[rowLimit][columnLimit];

        for (int i = rowBase; i < rowLimit; i++) {
            if (columnLimit - columnBase >= 0)
                System.arraycopy(places[i], columnBase, returnArray[i], columnBase, columnLimit - columnBase);
        }
        return returnArray;
    }

    public boolean changePlace(int row, int col, String newSymbol, String actualSymbol) {
        String [][] returnArray = getPlaceRange(row, col, row, col);
        for (int i = row; i < row + 1; i++) {
            for (int j = col; j < col + 1; j++) {
                if (!returnArray[i][j].equals(actualSymbol)) {
                    return false;
                }
            }
        }

        setPlaceRange(row, col, row, col,newSymbol);
        return true;
    }

    public void shot(String shot) {

    }

    public void showMe() {
        char rowName = 65;
        System.out.println();
        System.out.print("  ");
        for (int j = 1; j < columns + 1; j++) {
            System.out.print( j + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print( rowName + " ");
            for (int j = 0; j < rows; j++) {
                System.out.print(places[i][j] + " ");
            }
            rowName++;
            System.out.println();
        }
    }

    public void showMe(String symbol) {
        char rowName = 65;
        System.out.print("  ");
        for (int j = 1; j < columns + 1; j++) {
            System.out.print( j + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print( rowName + " ");
            for (int j = 0; j < rows; j++) {
                System.out.print(symbol + " ");
            }
            rowName++;
            System.out.println();
        }
    }

    public void showMe(boolean fog) {
        char rowName = 65;
        System.out.print("  ");
        for (int j = 1; j < columns + 1; j++) {
            System.out.print( j + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print( rowName + " ");
            for (int j = 0; j < rows; j++) {
                if (fog &&
                        !BattleShipSymbols.SIDE.symbol.equals(places[i][j])) {
                    System.out.print(places[i][j] + " ");

                } else {
                    System.out.print(BattleShipSymbols.FOG_OF_WAR.symbol + " ");
                }

            }
            rowName++;
            System.out.println();
        }
    }

    public void showMe(int row, int column, String symbol) {
        char rowName = 65;
        System.out.print("  ");
        for (int j = 1; j < columns + 1; j++) {
            System.out.print( j + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print( rowName + " ");
            for (int j = 0; j < rows; j++) {
                if (i == row && j == column){
                    System.out.print(places[i][j] + " ");
                } else {
                    System.out.print(symbol + " ");
                }

            }
            rowName++;
            System.out.println();
        }
    }
}

enum Fleet {
    AIRCRAFT_CARRIER(5, "Aircraft Carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer");

    int length;
    String shipType;

    Fleet(int length, String shipType) {
        this.length = length;
        this.shipType = shipType;
    }
}

enum BattleShipSymbols {
    FOG_OF_WAR("~"),
    SIDE("O"),
    HITTED_SIDE("X"),
    MISSED_SHOT("M");

    String symbol;

    BattleShipSymbols(String symbol) {
        this.symbol = symbol;
    }
}