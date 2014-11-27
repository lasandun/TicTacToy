package tictactoy;

/**
 *
 * @author lahiru
 */

public class TicTacToy extends TicTacServer{
    
    public final int player;
    public final int free;
    public final int opponent;
    int board[][];
    final TicTacGUI gui;
    int gameHostServerPort;
    int gameHostClientPort;
    int gameClientServerPort;
    int gameClientClientPort;
    
    private boolean clientConnected;
    private String clientURL;
    private boolean myTurn;
    private boolean gameGoingOn;
    
    TicTacClient client;
    
    public TicTacToy() {
        player = 1;
        free = 0;
        opponent = -1;
        board = new int[3][3];
        gameHostServerPort = Integer.parseInt(SysProperty.getProperty("gameHostServerPort"));
        gameHostClientPort = Integer.parseInt(SysProperty.getProperty("gameHostClientPort"));
        gameClientServerPort = Integer.parseInt(SysProperty.getProperty("gameClientServerPort"));
        gameClientClientPort = Integer.parseInt(SysProperty.getProperty("gameClientClientPort"));
        
        // gameplay
        clientConnected = false;
        myTurn = false;
        gameGoingOn = false;
        
        // create GUI
        gui = new TicTacGUI();
        gui.setTicTacToy(this);
        Thread guiThread = new Thread() {
            public void run() {
                gui.setVisible(true);
            }
        };
        guiThread.start();
    }
    
    public void onUpdateFromGUI(int r, int c) {
        if(gameGoingOn && myTurn) {
            tic(r, c, player);
            client.sendMessage("tic " + r + " " + c);
            myTurn = false;
        }
    }
    
    private boolean isRowFree(int row, int player, int board[][]) {
        int opponent = player * (-1);
        for(int col = 0; col < 3; ++col) {
            if(board[row][col] == opponent) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isColumnFree(int col, int player, int board[][]) {
        int opponent = player * (-1);
        for(int row = 0; row < 3; ++row) {
            if(board[row][col] == opponent) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isDiagonal1Free(int player, int board[][]) {
        int opponent = player * (-1);
        for(int i = 0; i < 3; ++i) {
            if(board[i][i] == opponent) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isDiagonal2Free(int player, int board[][]) {
        int opponent = player * (-1);
        for(int i = 0; i < 3; ++i) {
            if(board[i][2 - i] == opponent) {
                return false;
            }
        }
        return true;
    }
    
    int winningPossibilities(int player, int board[][]) {
        int count = 0;
        for(int i = 0; i < 3; ++i) {
            if(isColumnFree(i, player, board)) count++;
            if(isRowFree(i, player, board))    count++;
        }
        if(isDiagonal1Free(player, board))     count++;
        if(isDiagonal2Free(player, board))     count++;
        
        return count;
    }

    private int evaluate(int board[][]) {
        int opponent = player * (-1);
        int val = winningPossibilities(player, board) - winningPossibilities(opponent, board);
        return val;
    }
    
    public void showBoard() {
        System.out.println("----------------------");
        for(int r = 0; r < 3; ++r) {
            System.out.print("|");
            for(int c = 0; c < 3; ++c) {
                String val = null;
                if(board[r][c] == free)        val = " |";
                else if(board[r][c] == player) val = "X|";
                else                           val = "O|";
                System.out.print(val);
            }
            System.out.println();
        }
        System.out.println("----------------------");
        System.out.println();
    }
    
    Point bestMove(int player, int board[][]) {
        Point bestMove = null;
        int evaluationValue = -9;
        for(int r = 0; r < 3; ++r) {
            for(int c = 0; c < 3; ++c) {
                if(board[r][c] != free) continue;
                
                int newBoard[][] = Util.copyBoard(board);
                newBoard[r][c] = player;
                int newEvaluationValue = evaluate(board);
                if(evaluationValue < newEvaluationValue) {
                    newEvaluationValue = evaluationValue;
                    bestMove = new Point(r, c);
                }
            }
        }        
        return bestMove;
    }
    
    public void tic(int r, int c, int player) {
        if(board[r][c] != free) {
            System.out.println("Error: box already allocated");
            System.exit(1);
        }
        board[r][c] = player;
        gui.setBoard(board);
        showBoard();
        if(isGameOver() != 0) {
            finishGame();
        }
    }
    
    int isGameOver() {
        int p;
        for(int r = 0; r < 3; ++r) {
            p = board[r][0];
            if(board[r][1] == p && board[r][2] == p) return p;
        }
        for(int c = 0; c < 3; ++c) {
            p = board[0][c];
            if(board[1][c] == p && board[2][c] == p) return p;
        }
        p = board[1][1];
        if(board[0][0] == p && board[2][2] == p) return p;
        if(board[2][0] == p && board[0][2] == p) return p;
        
        return 0;
    }
    
    public void finishGame() {
        if(isGameOver() == 1) {
            gui.setMessageText("you won!");
        }
        else if(isGameOver() == -1) {
            gui.setMessageText("you lost!");
        }
        myTurn = false;
        gameGoingOn = false;
        downServer();
    }
    
    
    @Override
    public void onUpdate(String message) {
        System.out.println(message);
        if(message.startsWith("connecting")) {
            clientURL = "localhost";
            clientConnected = true;
        }
        else if(message.startsWith("tic")) {
            String parts[] = message.split(" ");
            int r = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);
            tic(r, c, opponent);
            myTurn = true;
        }
    }
    
    public void connectGame(String serverURL) {
        myTurn = false;
        setServerPort(gameClientServerPort);
        startServer();
        client = new TicTacClient(serverURL, gameHostServerPort);
        client.sendMessage("connecting");
        gameGoingOn = true;
    }
    
    public void hostGame() {
        myTurn = true;
        gameGoingOn = true;
        setServerPort(gameHostServerPort);
        startServer();
        while(!clientConnected) {
            System.out.print(""); // make a small delay
        }
        System.out.println("-----------------");
        client = new TicTacClient(clientURL, gameClientServerPort);
        client.sendMessage("successfully connected");
    }
    
    public static void main(String[] args) {
        /*int player = 1;
        int opponent = -1;
        int free = 0;
        TicTacToy x = new TicTacToy();
        x.tic(0, 1, player);
        x.tic(1, 0, opponent);
        x.tic(2, 1, player);
        x.tic(0, 2, opponent);
        x.tic(1, 1, opponent);
        //x.tic(1, 2, opponent);
        x.showBoard();
        x.bestMove(opponent, x.board).show();
        System.out.println(x.isGameOver());*/
        
        TicTacToy x = new TicTacToy();
        if(args[0].equals("host")) {
            x.hostGame();
        }
        else if(args[0].equals("client")) {
            x.connectGame("localhost");
        }
        else {
            System.out.println("undefined operation");
            System.exit(0);
        }
        
        
    }

    
}
