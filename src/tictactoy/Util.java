/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lahiru
 */
public class Util {
    
    public static final String connectURL = "http://ttt-hirantha.rhcloud.com/onlinePlayerConnect";
    public static final String moveURL = "http://ttt-hirantha.rhcloud.com/onlinePlayerMove";
    
    
    public static String sendPost(String url, List<NameValuePair> nameValuePairs) {
        String reply = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                reply += line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server sent reply: " + reply);
        return reply;
    }
    
    public static String sendOnlineMove(int r, int c, int player, int gameID) {
        String value = gameID + " " + player + " " + r + " " + c;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("move", value));
        String rep = sendPost(moveURL, nameValuePairs);
        System.out.println(rep);
        return rep;
    }
    
    public static void main(String[] args) {
        //sendPost("", "");
    }
    
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
    
    public static int isAPlayerWon(int board[][]) {
        int p;
        for(int r = 0; r < 3; ++r) {
            p = board[r][0];
            if(p != 0 && board[r][1] == p && board[r][2] == p) return p;
        }
        for(int c = 0; c < 3; ++c) {
            p = board[0][c];
            if(p != 0 && board[1][c] == p && board[2][c] == p) {
                return p;
            }
        }
        p = board[1][1];
        if(p != 0) {
            if(board[0][0] == p && board[2][2] == p) return p;
            if(board[2][0] == p && board[0][2] == p) return p;
        }
        
        return 0;
    }
    
}
