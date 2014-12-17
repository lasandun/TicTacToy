package tictactoy;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import javax.swing.JTextField;
import org.apache.http.NameValuePair;

/**
 *
 * @author lahiru
 */
public class GameGUI extends javax.swing.JFrame {

    JTextField[][] boxes = new JTextField[3][3];
    boolean isPossibleToMoveSP;
    boolean player1Chance;
    int board[][];
    String mode;
    int gameIDOP;
    int playerOP;
    int turnOP;
    
    public GameGUI() {
        initGUIConfigs();
        initComponents();
        boxes[0][0] = box00;
        boxes[0][1] = box01;
        boxes[0][2] = box02;
        boxes[1][0] = box10;
        boxes[1][1] = box11;
        boxes[1][2] = box12;
        boxes[2][0] = box20;
        boxes[2][1] = box21;
        boxes[2][2] = box22;
        setButtonActions();
        
        board = new int[3][3];
        isPossibleToMoveSP = false;
        player1Chance = true;
        mode = "idle";
    }

    public void setMessageText(String text) {
        textMessage.setText(text);
    }
    
    public void resetGame() {
        if(btnReset.getText().equals("Start Game")) {
            btnReset.setText("Restart Game");
        }
        board = new int[3][3];
        setBoard();
        setMessageText("");
        
        if(singlePlayer.isSelected()) {
            mode = "singlePlayer";
            if(!chkBoxStarting.isSelected()) {
                isPossibleToMoveSP = false;
                board[1][1] = 1;
                setBoard();
            }
            isPossibleToMoveSP = true;
        } else if(internet.isSelected()) {
            mode = "internet";
            isPossibleToMoveSP = false;
            String reply = Util.sendPost(Util.connectURL, new ArrayList<NameValuePair>());
            reply = reply.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", "");
            System.out.println(reply);
            String parts[] = reply.split(" ");
            gameIDOP = Integer.parseInt(parts[0]);
            playerOP = Integer.parseInt(parts[1]);
            turnOP = Integer.parseInt(parts[2]);
            
        } else {
            mode = "withFriend";
            player1Chance = true;
        }
    }
    
    public void setBoard() {
        for(int r = 0; r < 3; ++r) {
            for(int c = 0; c < 3; ++c) {
                if(board[r][c] == 1) {
                    boxes[r][c].setText("X");
                } else if(board[r][c] == -1) {
                    boxes[r][c].setText("O");
                } else {
                    boxes[r][c].setText("");
                }
            }
        }
    }
    
    public void finishGameSP() {
        int p = Util.isAPlayerWon(board);
        if(p > 0) {
            setMessageText("You lost");
        }
        else if(p == 0) {
            setMessageText("Game drawn");
        }
        else {
            setMessageText("You won");
        }
    }
    
    public void singlePlayer(int r, int c) {
        if(isPossibleToMoveSP && board[r][c] == 0) {
            isPossibleToMoveSP = false;
            board[r][c] = -1;
            setBoard();
            
            if(Util.isAPlayerWon(board) != 0  || Util.getNoOfFreeCells(board) == 0) {
                isPossibleToMoveSP = false;
                finishGameSP();
                return;
            }
            int bestMove[] = GameTree.getBestMove(board);
            int bestRow = bestMove[0];
            int bestCol = bestMove[1];
            board[bestRow][bestCol] = 1;
            setBoard();
            isPossibleToMoveSP = true;
            if(Util.isAPlayerWon(board) != 0  || Util.getNoOfFreeCells(board) == 0) {
                isPossibleToMoveSP = false;
                finishGameSP();
                return;
            }
        }
    }
    
    public void withFriend(int r, int c) {
        if(board[r][c] != 0) return;
        
        if(Util.isAPlayerWon(board) != 0  || Util.getNoOfFreeCells(board) == 0) {
            return;
        }
        
        int currPlayer = player1Chance ? 1 : -1;
        player1Chance = !player1Chance;
        board[r][c] = currPlayer;
        setBoard();
        if(Util.isAPlayerWon(board) != 0  || Util.getNoOfFreeCells(board) == 0) {
            int p = Util.isAPlayerWon(board);
            if(p > 0) {
                setMessageText("Player 1 won");
            }
            else if(p == 0) {
                setMessageText("Game drawn");
            }
            else {
                setMessageText("Player 2 won");
            }
            return;
        }
    }
    
    public void onlinePlay(int r, int c) {
        if(Util.isAPlayerWon(board) != 0) {
            turnOP = 0;
            if(Util.isAPlayerWon(board) == playerOP) {
                setMessageText("You won");
            } else {
                setMessageText("You lost");
            }
        } else if(Util.getNoOfFreeCells(board) == 0) {
            turnOP = 0;
            setMessageText("Game drawn");
        }
        
        String reply = Util.sendOnlineMove(r, c, playerOP, gameIDOP);
        reply = reply.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", "");
        String parts[] = reply.split(" ");
//        int isAPlayerWon = Integer.parseInt(parts[0]);
//        int noOfFreeCells = Integer.parseInt(parts[1]);
        turnOP = Integer.parseInt(parts[2]);
        
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                board[i][j] = Integer.parseInt(parts[3 + 3 * i + j]);
            }
        }
        setBoard();
        if(Util.isAPlayerWon(board) != 0) {
            turnOP = 0;
            if(Util.isAPlayerWon(board) == playerOP) {
                setMessageText("You won");
            } else {
                setMessageText("You lost");
            }
        } else if(Util.getNoOfFreeCells(board) == 0) {
            turnOP = 0;
            setMessageText("Game drawn");
        }
    }
    
    public void buttonAction(int r, int c) {
        if(mode.equals("singlePlayer")) {
            singlePlayer(r, c);
        }
        else if(mode.equals("internet")) {
            onlinePlay(r, c);
        }
        else if(mode.equals("withFriend")) {
            withFriend(r, c);
        }
    }

    private void setButtonActions() {
        boxes[0][0].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(0, 0);
            }
        });
        boxes[0][1].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(0, 1);
            }
        });
        boxes[0][2].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(0, 2);
            }
        });
        boxes[1][0].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(1, 0);
            }
        });
        boxes[1][1].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(1, 1);
            }
        });
        boxes[1][2].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(1, 2);
            }
        });
        boxes[2][0].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(2, 0);
            }
        });
        boxes[2][1].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(2, 1);
            }
        });
        boxes[2][2].addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAction(2, 2);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        box00 = new javax.swing.JTextField();
        box01 = new javax.swing.JTextField();
        box02 = new javax.swing.JTextField();
        box10 = new javax.swing.JTextField();
        box11 = new javax.swing.JTextField();
        box12 = new javax.swing.JTextField();
        box20 = new javax.swing.JTextField();
        box21 = new javax.swing.JTextField();
        box22 = new javax.swing.JTextField();
        textMessage = new javax.swing.JTextField();
        btnExit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        chkBoxStarting = new javax.swing.JCheckBox();
        singlePlayer = new javax.swing.JRadioButton();
        withFriend = new javax.swing.JRadioButton();
        internet = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        box00.setEditable(false);
        box00.setFont(new java.awt.Font("Dialog", 0, 48));
        box00.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box00.setToolTipText("");
        box00.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box00ActionPerformed(evt);
            }
        });

        box01.setEditable(false);
        box01.setFont(new java.awt.Font("Dialog", 0, 48));
        box01.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box01.setToolTipText("");
        box01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box01ActionPerformed(evt);
            }
        });

        box02.setEditable(false);
        box02.setFont(new java.awt.Font("Dialog", 0, 48));
        box02.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box02.setToolTipText("");
        box02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box02ActionPerformed(evt);
            }
        });

        box10.setEditable(false);
        box10.setFont(new java.awt.Font("Dialog", 0, 48));
        box10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box10.setToolTipText("");
        box10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box10ActionPerformed(evt);
            }
        });

        box11.setEditable(false);
        box11.setFont(new java.awt.Font("Dialog", 0, 48));
        box11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box11.setToolTipText("");
        box11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box11ActionPerformed(evt);
            }
        });

        box12.setEditable(false);
        box12.setFont(new java.awt.Font("Dialog", 0, 48));
        box12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box12.setToolTipText("");
        box12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box12ActionPerformed(evt);
            }
        });

        box20.setEditable(false);
        box20.setFont(new java.awt.Font("Dialog", 0, 48));
        box20.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box20.setToolTipText("");
        box20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box20ActionPerformed(evt);
            }
        });

        box21.setEditable(false);
        box21.setFont(new java.awt.Font("Dialog", 0, 48));
        box21.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box21.setToolTipText("");
        box21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box21ActionPerformed(evt);
            }
        });

        box22.setEditable(false);
        box22.setFont(new java.awt.Font("Dialog", 0, 48));
        box22.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box22.setToolTipText("");
        box22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box22ActionPerformed(evt);
            }
        });

        textMessage.setEditable(false);

        btnExit.setText("Exit Game");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnReset.setText("Start Game");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        chkBoxStarting.setSelected(true);
        chkBoxStarting.setText("I'm Starting Game ");

        buttonGroup1.add(singlePlayer);
        singlePlayer.setSelected(true);
        singlePlayer.setText("Single Player");
        singlePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singlePlayerActionPerformed(evt);
            }
        });

        buttonGroup1.add(withFriend);
        withFriend.setText("Play with a friend");
        withFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withFriendActionPerformed(evt);
            }
        });

        buttonGroup1.add(internet);
        internet.setText("Play in Internet");
        internet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internetActionPerformed(evt);
            }
        });

        jLabel2.setText("(Offline)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(box20, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(box21, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(box22, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(box10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(box11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(box12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(box00, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(box01, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(box02, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(singlePlayer)
                                .addComponent(withFriend)
                                .addComponent(internet))
                            .addGap(76, 76, 76))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                            .addComponent(chkBoxStarting)
                            .addGap(25, 25, 25)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box00, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box01, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box02, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(withFriend))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box20, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box21, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(box22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(internet))
                        .addGap(41, 41, 41)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExit)
                            .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(singlePlayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkBoxStarting)
                        .addGap(58, 58, 58)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void box00ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box00ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box00ActionPerformed

private void box01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box01ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box01ActionPerformed

private void box02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box02ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box02ActionPerformed

private void box10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box10ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box10ActionPerformed

private void box11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box11ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box11ActionPerformed

private void box12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box12ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box12ActionPerformed

private void box20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box20ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box20ActionPerformed

private void box21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box21ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box21ActionPerformed

private void box22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box22ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_box22ActionPerformed

private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
    System.exit(0);
}//GEN-LAST:event_btnExitActionPerformed

private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
    resetGame();
}//GEN-LAST:event_btnResetActionPerformed

private void withFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withFriendActionPerformed
// TODO add your handling code here:
    if(withFriend.isSelected()) {
        chkBoxStarting.setEnabled(false);
    }
}//GEN-LAST:event_withFriendActionPerformed

private void singlePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singlePlayerActionPerformed
// TODO add your handling code here:
    if(singlePlayer.isSelected()) {
        chkBoxStarting.setEnabled(true);
    }
}//GEN-LAST:event_singlePlayerActionPerformed

private void internetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internetActionPerformed
// TODO add your handling code here:
    if(internet.isSelected()) {
        chkBoxStarting.setEnabled(false);
    }
}//GEN-LAST:event_internetActionPerformed

    private void initGUIConfigs() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GameGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField box00;
    private javax.swing.JTextField box01;
    private javax.swing.JTextField box02;
    private javax.swing.JTextField box10;
    private javax.swing.JTextField box11;
    private javax.swing.JTextField box12;
    private javax.swing.JTextField box20;
    private javax.swing.JTextField box21;
    private javax.swing.JTextField box22;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnReset;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkBoxStarting;
    private javax.swing.JRadioButton internet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton singlePlayer;
    private javax.swing.JTextField textMessage;
    private javax.swing.JRadioButton withFriend;
    // End of variables declaration//GEN-END:variables

}
