/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group51;

import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;

/**
 *
 * @author Kerry, Vlad
 */
public class Evaluate {
    
    private final DraughtsPlayer player;
    
    public Evaluate(DraughtsPlayer player) {
        this.player = player;
    }
    
    public int evaluateState(DraughtsState state) {
        int score = 0;
        
        return score;
    }
    
    /*
    Piece       Score
    Own Man     1
    Other Man   -1
    Own King    10
    Other King  -10
    
    Check for next moves, might swing the board state
    Check for number of movable pieces
    Check for position of pieces
    
    */
    
}
