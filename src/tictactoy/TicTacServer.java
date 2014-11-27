package tictactoy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public abstract class TicTacServer {
    
    private ServerSocket serverSocket;
    private int port;
    private boolean isServerUp;
    
    public TicTacServer() {
        isServerUp = false;
    }

    public void setServerPort(int port) {
        this.port = port;
    }
    
    public abstract void onUpdate(String message);
    
    public void startServer() {
        isServerUp = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                startServerSocketListner();
            }
        };
        t.start();
    }
    
    public void downServer() {
        isServerUp = false;
    }
    
    private void startServerSocketListner() {
        try {
            serverSocket = new ServerSocket(port);
            while (isServerUp) {
                Socket clientSocket = serverSocket.accept();
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = input.readLine();
                    onUpdate(message);
                } catch(Exception ex) {
                    Logger.getLogger(TicTacServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    clientSocket.close();
                }
            }
        } catch(IOException ex) {
            Logger.getLogger(TicTacServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                serverSocket.close();
                System.out.println("closing server socket");
            } catch (IOException ex) {
                Logger.getLogger(TicTacServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        TicTacServer x = new TicTacServer() {

            @Override
            public void onUpdate(String message) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        x.setServerPort(9090);
        x.startServer();
    }
}