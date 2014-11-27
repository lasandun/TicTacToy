package tictactoy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public class TicTacClient {

    String serverURL;
    int port;

    public TicTacClient(String serverURL, int port) {
        this.serverURL = serverURL;
        this.port = port;
    }

    public void sendMessage(String message) {
        try {
            Socket client = new Socket(serverURL, port);
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(TicTacClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String readMessage() {
        String message = null;
        try {
            Socket client = new Socket(serverURL, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            message = input.readLine();
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(TicTacClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }
    
    public static void main(String[] args) {
        TicTacClient x = new TicTacClient("localhost", 9090);
        x.sendMessage("hi");
    }
}
