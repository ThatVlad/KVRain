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
    
    // Moves up: left-right: 6-5, down: left-right: 4-5
    private static final int[] POSITIONS_1 = new int[] {
        7, 8, 9, 10,
        17, 18, 19, 20,
        27, 28, 29, 30,
        37, 38, 39, 40,
        47, 48, 49, 50
    };
    
    // Moves up: left-right: 5-4, down: left-right: 5-6
    private static final int[] POSITIONS_2 = new int[] {
        1, 2, 3, 4,
        11, 12, 13, 14,
        21, 22, 23, 24,
        31, 32, 33, 34,
        41, 42, 43, 44
    };
    
    /**
     * Evaluates the state.
     * 
     * @param state
     *          The state to be checked
     * @return Score of the state
     */
    public int evaluateState(final DraughtsState state) {
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
            if (piece == DraughtsState.WHITEPIECE) {
                pieceScoreWhite += MAN_SCORE;
            } else if (piece == DraughtsState.BLACKPIECE) {
                white = false;
                pieceScoreBlack += MAN_SCORE;
            } else if (piece == DraughtsState.WHITEKING) {
                pieceScoreWhite += KING_SCORE;
            } else if (piece == DraughtsState.BLACKKING) {
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
    
    /**
     * Calculates the score for the position of the pieces
     */
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
    
    /**
     * Calculates the score for the number of moves that can be done
     */
    private int calculateMovableScore(int[] pieces, int position, boolean white) {
        int movableScore = 0;
        
        //TODO: this doesnt work for king
        if (position <= 5 && white || position >= 46 && !white) {
            return movableScore;
        }

//        // Checking if the piece is against the border, so only one move is possible
//        for (int i = 0; i < 5; i++) {
//            if (LEFT_BORDER[i] == position || RIGHT_BORDER[i] == position) {
//                checked = true;
//                int possibleMove = pieces[position - 5];
//                if (possibleMove == 0) {
//                    movableScore += MOVABLE_SCORE;
//                }
//            }
//        }

        // Check which positions could be a legal move
        boolean bordered = false; // Either left or right border if true
        int possibleMove1 = 0;
        int possibleMove2 = 0;
        boolean position1 = false; // Position on board influcences int difference for next move
        // Check if against a border
        for (int i = 0; i < 5; i++) {
            if (LEFT_BORDER[i] == position) {
                possibleMove1 = white ? position - 5 : position + 5;
                bordered = true;
                position1 = true;
                break;
            } else if (RIGHT_BORDER[i] == position) {
                possibleMove1 = white ? position - 5 : position + 5;
                bordered = true;
                position1 = false;
                break;
            }
        }
        
        if (!bordered) {
            for (int i = 0; i < POSITIONS_1.length; i++) {
                if (POSITIONS_1[i] == position) {
                    position1 = true;
                    break;
                } else if (POSITIONS_2[i] == position) {
                    position1 = false;
                    break;
                }
            }
            
            if (position1) {
                if (white) {
                    possibleMove1 = position - 5;
                    possibleMove1 = position - 6;
                } else {
                    possibleMove1 = position + 4;
                    possibleMove1 = position + 5;
                }
            } else {
                if (white) {
                    possibleMove1 = position - 4;
                    possibleMove1 = position - 5;
                } else {
                    possibleMove1 = position + 5;
                    possibleMove1 = position + 6;
                }
            }
        }
        
        // Check if the legality of the move(s)
        if (isMovePossible(pieces, position, position1, possibleMove1, white)) {
            movableScore += MOVABLE_SCORE;
        }
        if (possibleMove2 != 0 && isMovePossible(pieces, position, position1, possibleMove2, white)) {
            movableScore += MOVABLE_SCORE;
        }
        
        return movableScore;
    }
    
    /**
     * Checks if a given move is possible
     */
    private boolean isMovePossible(int[] pieces, int position, boolean position1, int move, boolean white) {
        int newPosition = pieces[move];
        
        // If empty space, move is possible
        if (newPosition == DraughtsState.EMPTY) {
            return true;
        }
        
        boolean canJump = false;
        
        // Check if new position is occupied by enemy piece
        if (white) {
            if (newPosition == DraughtsState.BLACKPIECE || newPosition == DraughtsState.BLACKKING) {
                canJump = true;
            }
        } else {
            if (newPosition == DraughtsState.WHITEPIECE || newPosition == DraughtsState.WHITEKING) {
                canJump = true;
            }
        }
        
        // Can't jump over edges
        if (!canJump || contains(SCORE4_POSITIONS, newPosition)) {
            return false;
        }
        
        // Check which direction the jump is in
        int moveDifference = Math.abs(newPosition - position);
        
        int jumpPosition = 0;
        
        if (position1) {
            if (white) {
                if (moveDifference == 5) {
                    jumpPosition = newPosition - 4;
                } else if (moveDifference == 6) {
                    jumpPosition = newPosition - 5;                  
                }
            } else {
                if (moveDifference == 4) {
                    jumpPosition = newPosition + 5;
                } else if (moveDifference == 5) {
                    jumpPosition = newPosition + 6;                  
                }
            }
        } else {
            if (white) {
                if (moveDifference == 4) {
                    jumpPosition = newPosition - 5;
                } else if (moveDifference == 5) {
                    jumpPosition = newPosition - 6;                  
                }
            } else {
                if (moveDifference == 5) {
                    jumpPosition = newPosition + 4;
                } else if (moveDifference == 6) {
                    jumpPosition = newPosition + 5;                  
                }
            }
        }
        
        // Check if the new jump position is empty
        if (pieces[jumpPosition] == DraughtsState.EMPTY) {
            return true;
        }
        
        return false;
    }
    
    private boolean contains(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        
        return false;
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
