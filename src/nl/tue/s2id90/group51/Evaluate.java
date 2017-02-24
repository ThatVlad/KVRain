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
    private static final int BOARDSIZE = 10;
    
    private static final int MAN_SCORE = 15;
    private static final int KING_SCORE = 50;
    private static final int ADJACENT_SCORE = 1;
    private static final int MOVABLE_SCORE = 1;
    
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
        int surroundingScoreWhite = 0;
        int surroundingScoreBlack = 0;
        int movableScoreWhite = 0;
        int movableScoreBlack = 0;
        
        // Get the state after forced capture moves
        // TODO: gwn overslaan in alpha-beta en dan naar laatste gaan?
        //DraughtsState stateToCheck = findStateToCheck(state);
        DraughtsState stateToCheck = state;
        
        // Check for winning state
        // If isEndState is true, then there are no more moves possible
        if (stateToCheck.isEndState()) {
            if (stateToCheck.isWhiteToMove()) {
                return -123456789;
            } else {
                return 123456789;
            }
        }
        
        int[][] pieces = new int[BOARDSIZE][BOARDSIZE];
        
        // Get all pieces
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = ((row & 1) == 0) ? 1 : 0; col < BOARDSIZE; col += 2) {
                pieces[row][col] = stateToCheck.getPiece(row, col);
            }
        }
        
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = ((row & 1) == 0) ? 1 : 0; col < BOARDSIZE; col += 2) {
                int piece = pieces[row][col];
                
                if (piece == DraughtsState.EMPTY || piece == DraughtsState.WHITEFIELD) {
                    continue;
                }
                
                boolean man = true;
                boolean white = true;

                // Check piece type for score
                if (piece == DraughtsState.WHITEPIECE) {
                    pieceScoreWhite += MAN_SCORE;
                } else if (piece == DraughtsState.BLACKPIECE) {
                    white = false;
                    pieceScoreBlack += MAN_SCORE;
                } else {
                    man = false;
                    if (piece == DraughtsState.WHITEKING) {
                        pieceScoreWhite += KING_SCORE;
                    } else if (piece == DraughtsState.BLACKKING) {
                        white = false;
                        pieceScoreBlack += KING_SCORE;
                    }
                }

                // Check position for score
                int positionScore = calculatePositionScore(row, col, white);
                if (white) {
                    positionScoreWhite += positionScore;
                } else {
                    positionScoreBlack += positionScore;
                }
                
                // Check surrounding pieces for score
                int surroundingScore = calculateSurroundingScore(pieces, row, col, white);
                if (white) {
                    surroundingScoreWhite += surroundingScore;
                } else {
                    surroundingScoreBlack += surroundingScore;
                }

                // Check for possible moves
//                int movableScore = calculateMovableScore(pieces, position, white);
//                if (movableScore != 0) {
//                    if (white) {
//                        movableScoreWhite += movableScore;
//                    } else {
//                        movableScoreBlack += movableScore;
//                    }
//                }
            }
        }
        
        int whiteScore = pieceScoreWhite + positionScoreWhite + surroundingScoreWhite;
        int blackScore = pieceScoreBlack + positionScoreBlack + surroundingScoreBlack;

        score = whiteScore - blackScore;

        return score;
    }
    
    /**
     * Calculates the score for the position of the pieces
     * 
     * Maximum of 15;
     */
    private int calculatePositionScore(int row, int col, boolean white) {
        int score = 0;
        
        // Add score based on row, center is better than edges
        if (col == 0 || col == 9) {
            score += 1;
        } else if (col == 1 || col == 8) {
            score += 2;
        } else if (col == 2 || col == 7) {
            score += 3;
        } else if (col == 3 || col == 6) {
            score += 5;
        } else if (col == 4 || col == 5) {
            score += 6;
        }
        
        // Add score based on column, further forward is better
        if (white) {
            row = 9 - row;
        }
        
        score += row;

        return score;
    }
    
    /**
     * Checks the surrounding tiles for pieces of the same color for score.
     * 
     * Max 4*ADJACENT_SCORE per piece.
     */
    private int calculateSurroundingScore(int[][] pieces, int row, int col, boolean white) {
        int score = 0;
        
        for (int y = Math.max(0, row - 1); y < BOARDSIZE; y++) {
            for (int x = Math.max(0, col - 1); x < BOARDSIZE; x++) {
                int piece = pieces[y][x];
                if (white) {
                    if (piece == DraughtsState.WHITEPIECE || piece == DraughtsState.WHITEKING) {
                        score += ADJACENT_SCORE;
                    }
                } else {
                    if (piece == DraughtsState.BLACKPIECE || piece == DraughtsState.BLACKKING) {
                        score += ADJACENT_SCORE;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * Calculates the score for the number of moves that can be done
     */
    private int calculateMovableScore(int[] pieces, int position, boolean white) {
        int movableScore = 0;
        return movableScore;
    }
    
    /**
     * Checks if a given move is possible
     */
    private boolean isMovePossible(int[] pieces, int position, boolean position1, int move, boolean white) {
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
