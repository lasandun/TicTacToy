package tictactoy;

/**
 *
 * @author lahiru
 */

public class TicTacToy {
    
    public final int player;
    public final int free;
    public final int opponent;
    int board[][];
    
    public TicTacToy() {
        player = 1;
        free = 0;
        opponent = -1;
        board = new int[3][3];
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
    
    public static void main(String[] args) {
        int player = 1;
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
        System.out.println(x.isGameOver());
    }
    
}
