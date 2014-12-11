/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static String getClientPort(Socket clientSocket) {
        String ipNport = clientSocket.getRemoteSocketAddress().toString().substring(1);
        int semicolonIndex = ipNport.indexOf(":");
        String ip = ipNport.substring(0, semicolonIndex);
        return ip;
    }
    
    public static int getPropertyInt(String property) {
        return Integer.parseInt(SysProperty.getProperty(property));
    }
    
    public static void sleepThread(long milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int getNoOfFreeCells(int board[][]) {
        int noOfFreeBoxes = 0;
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(board[i][j] == 0) {
                    noOfFreeBoxes++;
                }
            }
        }
        return noOfFreeBoxes;
    }
    
    public static int[] extractLastMove(int previous[][], int current[][]) {
        for(int r = 0; r < 3; ++r) {
            for(int c = 0; c < 3; ++c) {
                if(previous[r][c] != current[r][c]) {
                   int lastMove[] = {r, c};
                   return lastMove;
                }
            }
        }
        return null;
    }
    
}
