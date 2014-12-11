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
        int noOfFreeBoxes = 0;
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(board[i][j] == 0) {
                    noOfFreeBoxes++;
                }
            }
        }
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
        LinkedList<Node> nodeList = new LinkedList<Node>();
        for(int i = maxLevels - 1; i >= 0; --i) {
            nodeList = getNodesAtLevel(i);
            for(Node n : nodeList) {
                n.computeEvaluationValueFromChilds();
            }
            nodeList.clear();
        }
    }
    
    public void getBestSolutionBoard() {
        int bestEval = root.evaluationValue;
        for(Node n : root.childs) {
            if(n.evaluationValue == bestEval) {
                n.showBoard();
                System.out.println("");
            }
        }
    }
    
    public static void main(String[] args) {
        int board[][] = {{0,0,0},{0,-1,0},{0,0,0}};
        GameTree x = new GameTree(board);
        int levels = 4;
        x.createChilds(levels);
        x.setEvaluationValuesOfTree();
        x.getBestSolutionBoard();
        
        //x.showTree();
        
    }
    
}
