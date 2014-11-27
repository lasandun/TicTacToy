/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

/**
 *
 * @author lahiru
 */
public class Test extends TicTacServer{

    public Test(int port) {
        setServerPort(port);
    }
    
    @Override
    public void onUpdate(String message) {
        System.out.println(message);
    }
    
    public static void main(String[] args) {
        //Test t = new Test(9090);
        //t.startServer();
        
        final TicTacGUI x = new TicTacGUI();
        int board[][] = { {1,0,1}, {1,-1,1}, {-1,0,1}};
        x.setBoard(board);
        Thread t = new Thread() {
            public void run() {
                x.setVisible(true);
            }
        };
        t.start();
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                x.setVisible(true);
//            }
//        });
    }
    
}
