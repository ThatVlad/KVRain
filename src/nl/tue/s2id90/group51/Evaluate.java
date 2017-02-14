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
    
    private static final int MAN_SCORE = 3;
    private static final int KING_SCORE = 15;
    private static final int MOVABLE_SCORE = 1;
    
    private static final int[] LEFT_BORDER = new int[] {
        6, 16, 26, 36, 46
    };
    private static final int[] RIGHT_BORDER = new int[] {
        5, 15, 25, 35, 45
    };
    
    // Score = 5
    private static final int[] SCORE4_POSITIONS = new int[] {
        1,  2,  3,  4,  5,
        6,              15,
        16,             25,
        26,             35,     
        36,             45,
        46, 47, 48, 49, 50
    };
    
    // Score = 4
    private static final int[] SCORE3_POSITIONS = new int[] {
        7,  8,  9,  10,
        11,         20,
        21,         30,
        31,         40,
        41, 42, 43, 44
    };
    
    // Score = 3
    private static final int[] SCORE2_POSITIONS = new int[] {
        12, 13, 14,
        17,     24,
        27,     34,
        37, 38, 39
    };
    
    // Score = 2
    private static final int[] SCORE1_POSITIONS = new int[] {
        18, 19,
        22, 23,
        28, 29,
        21, 33
    };
    
    
    public int evaluateState(DraughtsState state) {
        int score = 0;
        
        int pieceScoreWhite = 0;
        int pieceScoreBlack = 0;
        int positionScoreWhite = 0;
        int positionScoreBlack = 0;
        int movableScoreWhite = 0;
        int movableScoreBlack = 0;
        
        int[] pieces = state.getPieces();
        
        for (int position = 1; position < pieces.length; position++) {
            int piece = pieces[position];
            
            boolean man = true;
            boolean white = true;
            
            // Check piece type for score
            if (piece == 1) {
                pieceScoreWhite += MAN_SCORE;
            } else if (piece == 2) {
                white = false;
                pieceScoreBlack += MAN_SCORE;
            } else if (piece == 3) {
                pieceScoreWhite += KING_SCORE;
            } else if (piece == 4) {
                white = false;
                pieceScoreBlack += KING_SCORE;
            }
            
            // Check position for score
            int positionScore = calculatePositionScore(position);
            if (positionScore != 0) {
                if (white) {
                    positionScoreWhite += positionScore;
                } else {
                    positionScoreBlack += positionScore;
                }
            }
            
            
            // Check for possible moves
            int movableScore = calculateMovableScore(pieces, position, white);
            if (movableScore != 0) {
                if (white) {
                    movableScoreWhite += movableScore;
                } else {
                    movableScoreBlack += movableScore;
                }
            }
        }
        
        int whiteScore = pieceScoreWhite + positionScoreWhite + movableScoreWhite;
        int blackScore = pieceScoreBlack + positionScoreBlack + movableScoreBlack;
        
        score = whiteScore - blackScore;
        
        return score;
    }
    
    private int calculatePositionScore(int position) {
        for (int i = 0; i < SCORE4_POSITIONS.length; i++) {
            if (SCORE4_POSITIONS[i] == position) {
                return 4;
            }

            if (i < SCORE3_POSITIONS.length) {
                if (SCORE3_POSITIONS[i] == position) {
                    return 3;
                }
            } else {
                break;
            }

            if (i < SCORE2_POSITIONS.length) {
                if (SCORE2_POSITIONS[i] == position) {
                    return 2;
                }
            } else {
                break;
            }

            if (i < SCORE1_POSITIONS.length) {
                if (SCORE1_POSITIONS[i] == position) {
                    return 1;
                }
            }
        }
        
        return 0;
    }
    
    private int calculateMovableScore(int[] pieces, int position, boolean white) {
        int movableScore = 0;
        boolean checked = false;
        if (position <= 5 && white || position >= 46 && !white) {
            checked = true;
        }

        for (int i = 0; i < 5; i++) {
            if (LEFT_BORDER[i] == position || RIGHT_BORDER[i] == position) {
                checked = true;
                int possibleMove = pieces[position - 5];
                if (possibleMove == 0) {
                    movableScore += MOVABLE_SCORE;
                }
            }
        }

        if (!checked) {
            int possibleMove1 = pieces[position - 5];
            int possibleMove2 = pieces[position - 6];
            if (possibleMove1 == 0) {
                movableScore += MOVABLE_SCORE;
            }
            if (possibleMove2 == 0) {
                movableScore += MOVABLE_SCORE;
            }
        }
        
        return movableScore;
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
    
    /* 1: sum total number of your pieces (subtract from adversary) */
/* 2: sum total number of your king pieces (subtract from opponent king pieces) */
/* 3: Offense: count how advanced are your pieces (how close to become a king)
(subtract from opponents advanced position). How to count this? For example, count
how much is the sum of all your “a” component. Note that depending if you are red or
white, high sum or low sum may be preferred.*/
/* 4: Defense: how well defended are the pieces? sum total number of red previous
neighbors (n3,n4) to a red piece and subtract from total number of white previous
neighbors (n1, n2) to a white piece. */
/* 5: Defense against kings: sum total number of red post neighbors (n1,n2) to a red
piece and subtract from total number of white post neighbors (n1, n2) to a white
piece. Combine with 4. */
/* 6: Defense on sides: see if pieces are on the sides of the board, so that they are
defended. This criteria should be combined with 4,5.*/
/*7: Defense: Kings on corners are better defended. This is should be combined with
4, 5 and 6 */
/* 8: Dynamic position. Count number of possible moves by pieces. Count number of
possible moves by kings. Compare with opponents.
    
    INT
    0   =   Empty
    1   =   White Man
    2   =   Black Man
    3   =   White King
    4   =   Black King
    5   =   Non-playing field
    
    */
    
}
