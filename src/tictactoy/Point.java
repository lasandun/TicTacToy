/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoy;

/**
 *
 * @author lahiru
 */
public class Point {
    public int r, c;

    public Point(int r, int c) {
        this.r = r;
        this.c = c;
    }
    
    public void show() {
        System.out.println("point: " + r +", " + c);
    }
    
    @Override
    public String toString() {
        return r + "," + c;
    }
}
