package tictactoy;

import java.util.StringTokenizer;

/**
 *
 * @author lahiru
 */

public class TicTacToy extends TicTacServer implements Runnable{
    
    public final int player;
    public final int free;
    public final int opponent;
    int board[][];
    
    GameGUI gameGUI;
    MainGUI mainGUI;
    Thread mainGuiThread;
    Thread gamePlayThread; // runs the GameGUI
    
    int gameHostServerPort;
    int gameHostClientPort;
    int gameClientServerPort;
    int gameClientClientPort;
    
    private boolean clientConnected;
    private String clientURL;
    private boolean myTurn;
    private boolean gameGoingOn;
    
    TicTacClient client;
    String clientIP;
    
    int state;
    final int hosting     = 0;
    final int connected   = 1;
    final int playing     = 2;
    final int idle        = 3;
    final int ticked       = 4;
    
    private Point lastMove;
    
    public TicTacToy() {
        board = new int[3][3];
        
        // states
        setState(idle);
        player = 1;
        free = 0;
        opponent = -1;
        
        // connection properties
        gameHostServerPort = Integer.parseInt(SysProperty.getProperty("gameHostServerPort"));
        gameHostClientPort = Integer.parseInt(SysProperty.getProperty("gameHostClientPort"));
        gameClientServerPort = Integer.parseInt(SysProperty.getProperty("gameClientServerPort"));
        gameClientClientPort = Integer.parseInt(SysProperty.getProperty("gameClientClientPort"));
        
        // gameplay
        clientConnected = false;
        myTurn = false;
        gameGoingOn = false;
        
        // gui & threads
//        /gameGUI = new GameGUI(this);
//        gameGUI.show();
//        mainGUI = new MainGUI(this);
//        mainGuiThread = new Thread(mainGUI);
//        gamePlayThread = new Thread(this);
//        startGame();
    }

    private synchronized void setState(int state) {
        this.state = state;
    }
    
    private void startGame() {
        gameGUI.setVisibilityOfGUI(false);
        mainGUI.setVisibilityOfGUI(true);
        mainGuiThread.start();
        gamePlayThread.start();
    }
    
    @Override
    public synchronized void onUpdate(String message) {
        System.out.println(message);
        if(message.startsWith("connecting:")) {
            clientURL = message.replaceFirst("connecting:", "");
            clientConnected = true;
            return;
        }
        else if(message.startsWith("tic")) {
            String parts[] = message.split(" ");
            int r = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);
            lastMove = new Point(r, c);
            setState(ticked);
            //tic(r, c, opponent);
            //myTurn = true;
            return;
        }
        else if(message.equals("host")) {
            setState(hosting);            
            return;
        }
        else if(message.startsWith("MainGUI-connect:")) {
            setState(connected);
            clientIP = message.replaceFirst("MainGUI-connect:", "");
            return;
        }
        else if(message.equals("GameGUI-stopGame")) {
            
        }
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
        if(r > 3 || c > 3 || r < 0 || c < 0) {
            System.out.println("Invalid cell : " + r + "," + c);
        }
        if(board[r][c] != free) {
            System.out.println("Error: box already allocated");
            System.exit(1);
        }
        board[r][c] = player;
        gameGUI.setBoard(board);
        showBoard();
        if(isAPlayerWon() != 0 || isGameDrawn() ) {
            finishGame();
        }
    }
    
    int isAPlayerWon() {
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
    
    boolean isGameDrawn() {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(board[i][j] == free) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void finishGame() {
        if(isAPlayerWon() == 1) {
            gameGUI.setMessageText("you won!");
        }
        else if(isAPlayerWon() == -1) {
            gameGUI.setMessageText("you lost!");
        } else if(isGameDrawn()) {
            gameGUI.setMessageText("Game drawn!");
        }
        myTurn = false;
        gameGoingOn = false;
        downServer();
    }
    
    public void connectGame(String serverURL) {
        client = new TicTacClient(serverURL, gameHostServerPort);
        boolean successful = client.sendMessage("connecting:");
        if(successful) {
            mainGUI.setVisibilityOfGUI(false);
            gameGUI.setVisibilityOfGUI(true);
            setState(playing);
            myTurn = false;
            setServerPort(gameClientServerPort);
            startServer();
            gameGoingOn = true;
            gameGUI.setVisibilityOfGUI(true);
        }
        else {
            setState(idle);
        }
    }
    
    public void hostGame() {
        myTurn = true;
        gameGoingOn = true;
        setServerPort(gameHostServerPort);
        startServer();
        setState(playing);
        mainGUI.setMessage("waiting for a client to connect....");
        while(!clientConnected) {
            System.out.print(""); // make a small delay - use wait() instead
        }
        mainGUI.setMessage("");
        mainGUI.setVisibilityOfGUI(false);
        gameGUI.setVisibilityOfGUI(true);
                
        System.out.println("-----------------");
        client = new TicTacClient(clientURL, gameClientServerPort);
        client.sendMessage("successfully connected");
        gameGUI.setVisibilityOfGUI(true);
    }
    
    @Override
    public void run() {
        while(true) {
            if(state == hosting) {                
                System.out.println("*************");
                hostGame();
                continue;
            }
            else if(state == connected) {                
                System.out.println("-------------");
                connectGame(clientIP);
                continue;
            }
            else if(state == playing) {
                
            }
            else if(state == ticked) {
                tic(lastMove.r, lastMove.c, opponent);
                myTurn = true;
                setState(playing);
            }
            else {
                System.out.print("");
            }
        }
    }
    
    public String getBoardAsString() {
        String brd = "";
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                brd += board[i][j] + " ";
            }
        }
        return brd;
    }
    
    public void setBoard(String brd) {
        StringTokenizer st = new StringTokenizer(brd, " ");
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }
    
    public static void main(String[] args) {
        
        TicTacToy game = new TicTacToy();
        
    }

}
