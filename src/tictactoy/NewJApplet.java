package tictactoy;

import java.awt.Button;
import java.awt.GridLayout;
import javax.swing.JApplet;

/**
 *
 * @author lahiru
 */
public class NewJApplet extends JApplet {
    
    Button[][] buttons;
    boolean player1;
    
    private void buttonAction(int r, int c) {
        System.out.println("button clicked : " + r + " " + c);
        // check for the validity
        if(buttons[r][c].getLabel().equals("")) {
            if(player1) {
                buttons[r][c].setLabel("X");
            } else {
                buttons[r][c].setLabel("O");
            }
            player1 = !player1;
        }
    }
    
    @Override
    public void init() {
        player1 = true;
        buttons = new Button[3][3];
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                buttons[i][j] = new Button("");
            }
        }
        
        setButtonActions();
        
        setLayout(new GridLayout(3,3));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                add(buttons[i][j]);
            }
        }
        
    }
    
    private void setButtonActions() {
        buttons[0][0].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(0, 0);
            }
        });
        buttons[0][1].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(0, 1);
            }
        });
        buttons[0][2].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(0, 2);
            }
        });
        buttons[1][0].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(1, 0);
            }
        });
        buttons[1][1].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(1, 1);
            }
        });
        buttons[1][2].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(1, 2);
            }
        });
        buttons[2][0].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(2, 0);
            }
        });
        buttons[2][1].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(2, 1);
            }
        });
        buttons[2][2].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //this.actionPerformed(evt);
                buttonAction(2, 2);
            }
        });
    }
    // TODO overwrite start(), stop() and destroy() methods
}
