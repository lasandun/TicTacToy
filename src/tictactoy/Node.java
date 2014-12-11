package tictactoy;

/**
 *
 * @author lahiru
 */
public class Node {
    
    int board[][];

    Node childs[];
    int level;
    int evaluationValue;
    int noOfFreeBoxes;

    public Node(int[][] board, int noOfFreeBoxes, int level) {
        this.board = Util.copyBoard(board);
        this.childs = new Node[noOfFreeBoxes];
        this.level = level;
        this.noOfFreeBoxes = noOfFreeBoxes;
        evaluationValue = 99;
    }
    
    public boolean isMaxNode() {
        return (level % 2 == 0);
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

    public void setEvaluationValueAsALeave() {
        int player = 1;
        int opponent = -1;
        evaluationValue = winningPossibilities(player, board) - winningPossibilities(opponent, board);
    }
    
    public void showBoard() {
        for(int r = 0; r < 3; ++r) {
            for(int c = 0; c < 3; ++c) {
                int val = board[r][c];
                if(val < 0) System.out.print(val + " ");
                else        System.out.print(" " + val + " ");
            }
            System.out.println("");
        }
    }
    
    public void computeEvaluationValueFromChilds() {
        boolean isMaxNode = isMaxNode();
        int eval = isMaxNode ? -99: 99;
        System.out.println("-------" + level + "--------");
        for(Node n: childs) {
            System.out.print(n.evaluationValue + " ");
            if(isMaxNode) eval = Math.max(eval, n.evaluationValue);
            else          eval = Math.min(eval, n.evaluationValue);
        }
        evaluationValue = eval;
        
        System.out.println();
        System.out.println("chosen : " + eval);
        System.out.println("----------------");
        
        if(Math.abs(eval) > 10) {
            System.out.println("Error evaluation value : " + eval);
            System.exit(-99);
        }
        
    }
    
    public void showChilds() {
        for(int i = 0; i < childs.length; ++i) {
            childs[i].showBoard();
        }
    }

}