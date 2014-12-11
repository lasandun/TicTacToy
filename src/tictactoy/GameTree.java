package tictactoy;

import java.util.LinkedList;

/**
 *
 * @author lahiru
 */
public class GameTree {
    Node root;
    int maxLevels;  // equals to the level attribute of the leaves

    public GameTree(Node root) {
        this.root = root;
    }
    
    public GameTree(int board[][]) {
        int noOfFreeBoxes = Util.getNoOfFreeCells(board);
//        for(int i = 0; i < 3; ++i) {
//            for(int j = 0; j < 3; ++j) {
//                if(board[i][j] == 0) {
//                    noOfFreeBoxes++;
//                }
//            }
//        }
        root = new Node(board, noOfFreeBoxes, 0);
        root.childs = new Node[noOfFreeBoxes];
    }
    
    public void showTree() {
        LinkedList<Node> list = new LinkedList<Node>();
        for(int i = 0; i < maxLevels + 1; ++i) {
            System.out.println("***************** " + (i) + " ************************");
            getChildsAtLevel(root, i, list);
            for(Node n : list) {
                n.showBoard();
                System.out.println("eval: " + n.evaluationValue);
                System.out.println();
            }
            list.clear();
        }
    }
    
    private void createChildsInNode(Node node) {
        int creatingLevel = node.level + 1;
        int noOfFreeBoxes = node.childs.length;
        for(int i = 0; i < noOfFreeBoxes; ++i) {
            node.childs[i] = new Node(node.board, noOfFreeBoxes - 1, node.level + 1);
        }
        
        int k = noOfFreeBoxes;
        int player = (creatingLevel % 2 == 1) ? 1 : -1;
        for(int r = 0; r < 3; ++r) {
            for(int c = 0; c < 3; ++c) {
                if(k <= 0) return;
                if(node.childs[noOfFreeBoxes - k].board[r][c] == 0) {
                    node.childs[noOfFreeBoxes - k].board[r][c] = player;
                    --k;
                }
            }
        }
    }
    
    private void createChildsInternal(int level, Node node) {
        if(level >= maxLevels) return;
        
        // if a player has already won
        int winner = node.isAPlayerWon();
        if(winner != 0) {
            node.evaluationValue = winner ;//* Constants.evaluationAtAVicoty;
            return;
        }
        
        createChildsInNode(node);
        for(Node n : node.childs) {
            createChildsInternal(level + 1, n);
        }
    }
    
    public void createChilds(int maxLevels) {
        this.maxLevels = maxLevels;
        createChildsInternal(0, root);
    }
    
    public void getChildsAtLevel(Node node, final int level, final LinkedList<Node> list) {
        if(node == null) return;
        if(node.level > level) return;
        
        if(node.level == level) {
            list.addLast(node);
        }
        
        for(Node n : node.childs) {
            getChildsAtLevel(n, level, list);
        }
    }
    
    private LinkedList<Node> getNodesAtLevel(int level) {
        LinkedList<Node> list = new LinkedList<Node>();
        getChildsAtLevel(root, level, list);
        return list;
    }
    
    private void setEvaluationValuesAtLeaves() {
        LinkedList<Node> leaveList = getNodesAtLevel(maxLevels);
        for(Node n : leaveList) {
            n.setEvaluationValueAsALeave();
        }
    }
    
    public void setEvaluationValuesOfTree() {
        setEvaluationValuesAtLeaves();
//        LinkedList<Node> nodeList = new LinkedList<Node>();
//        for(int i = maxLevels - 1; i >= 0; --i) {
//            nodeList = getNodesAtLevel(i);
//            for(Node n : nodeList) {
//                n.computeEvaluationValueFromChilds();
//            }
//            nodeList.clear();
//        }
    }
    
    public int showBestSolutionBoard() {
        int bestIndex = 0;
        for(int i = 1; i < root.childs.length; ++i) {
            if(root.childs[bestIndex].evaluationValue < root.childs[i].evaluationValue) {
                bestIndex = i;
            }
        }
        System.out.println();
        System.out.println("******* BEST SOLUTION *************");
        root.childs[bestIndex].showBoard();
        System.out.println();
        return bestIndex;
    }
    
    int count = 0;
    public int miniMax(Node node, boolean isMaximizing) {
        ++count;
        if(node.level == maxLevels || node.childs == null || node.childs[0] ==null ||
                Math.abs(node.evaluationValue) != Constants.unsetEvaluationVal) {
            return node.evaluationValue;
        }
        
        if(isMaximizing) {
            int best = -1000;
            for(Node child : node.childs) {
                int val = miniMax(child, false);
                if(Math.abs(val) == Constants.unsetEvaluationVal) {
                    System.out.println("errr : " + val);
                    child.showBoard();
                    System.out.println(child.childs[0].evaluationValue);
                    System.exit(0);
                }
                best = Math.max(val, best);
            }
            node.evaluationValue = best;
            return best;
        }
        else {
            int best = 1000;
            for(Node child : node.childs) {
                int val = miniMax(child, true);
                best = Math.min(val, best);
            }
            node.evaluationValue = best;
            return best;
        }
    }
    
    
    public static int[] getBestMove(int board[][]) {
        if((Util.getNoOfFreeCells(board) == 8 && board[1][1] == 0) || 
                Util.getNoOfFreeCells(board) == 9) {
            int ret[] = {1, 1};
            return ret;
        }
        GameTree x = new GameTree(board);
        int maxLevels = Util.getNoOfFreeCells(board);
        x.createChilds(maxLevels);
        x.setEvaluationValuesAtLeaves();
        x.miniMax(x.root, true);
        
        //x.showTree();

        int bestIndex = x.showBestSolutionBoard();
        int move[] = Util.extractLastMove(board, x.root.childs[bestIndex].board);
        return move;
    }
    
    public static void main(String[] args) {
        int board[][] = {{0,0,0}, {0,0,0}, {0,0,0}};
        getBestMove(board);
    }
}
