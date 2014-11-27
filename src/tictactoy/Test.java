/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

/**
 *
 * @author lahiru
 */
public class Test extends TicTacToeServer{

    public Test(int port) {
        super(port);
    }
    
    @Override
    public void onUpdate(String message) {
        System.out.println(message);
    }
    
    public static void main(String[] args) {
        Test t = new Test(9090);
        t.startServer();
    }
    
}
