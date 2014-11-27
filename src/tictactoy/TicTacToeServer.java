package tictactoy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public abstract class TicTacToeServer {
    
    ServerSocket serverSocket;
    int port;

    public TicTacToeServer(int port) {
        this.port = port;
    }
    
    public abstract void onUpdate(String message);
    
    public void startServer() {
        Thread t = new Thread() {
            @Override
            public void run() {
                startServerSocketListner();
            }
        };
        t.start();
    }
    
    private void startServerSocketListner() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = input.readLine();
                    onUpdate(message);
                } catch(Exception ex) {
                    Logger.getLogger(TicTacToeServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    clientSocket.close();
                }
            }
        } catch(IOException ex) {
            Logger.getLogger(TicTacToeServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(TicTacToeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /*public static void main(String[] args) {
        TicTacToeServer x = new TicTacToeServer(1) {

            @Override
            public void onUpdate(String message) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        x.startServer();
    }*/
    
}