/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author lahiru
 */
public class PollingTimer extends TimerTask {

//    Timer timer;
//    int player;
//    int gameID;
//    int delay;
    GameGUI gui;

    public PollingTimer(GameGUI gui) {
        this.gui = gui;
    }
    
//    public void setValues(int player, int gameID, int delay) {
//        this.player = player;
//        this.gameID = gameID;
//        this.delay = delay;
//    }

    public void run() {
//        Util.sendOnlineMove(-1, -1, player, gameID);
        gui.makeMove(-1, -1);
        gui.checkStatusOfGameOP();
        System.out.println("Time's up!");
        if(gui.turnOP == 0 && Util.getNoOfFreeCells(gui.board) == 9) {
            gui.setMessageText("Waiting for another player...");
        } else if(gui.turnOP == gui.playerOP) {
            gui.setMessageText("It's your turn");
        } else if(gui.turnOP == (-gui.playerOP)) {
            gui.setMessageText("It's your opponent's turn");
        }
    }
    
    public static void main(String args[]) {
        System.out.println("About to schedule task.");
        new Remainder(2);
        System.out.println("Task scheduled.");
    }
}