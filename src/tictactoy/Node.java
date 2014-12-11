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
        evaluationValue = Constants.unsetEvaluationVal;
    }
    
    public boolean isMaxNode() {
        return (level % 2 == 0);
    }
    
    public void setEvaluationValueAsALeave() {
        evaluationValue = isAPlayerWon();
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
        int eval = isMaxNode ? -Constants.unsetEvaluationVal: Constants.unsetEvaluationVal;
        for(Node n: childs) {
            if(isMaxNode) eval = Math.max(eval, n.evaluationValue);
            else          eval = Math.min(eval, n.evaluationValue);
        }
        evaluationValue = eval;

        if(Math.abs(eval) == Constants.unsetEvaluationVal) {
            System.out.println("Error evaluation value : " + eval);
            System.exit(-1);
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
    
    public void showChilds() {
        for(int i = 0; i < childs.length; ++i) {
            childs[i].showBoard();
        }
    }

}