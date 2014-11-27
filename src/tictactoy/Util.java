/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

/**
 *
 * @author lahiru
 */
public class Util {
    
    public static int[][] copyBoard(int board[][]) {
        int boardNew[][] = new int[3][3];
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                boardNew[i][j] = board[i][j];
            }
        }
        return boardNew;
    }
    
}
