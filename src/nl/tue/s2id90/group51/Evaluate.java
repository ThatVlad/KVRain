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
    private static final int ADJACENT_SCORE = 3;
    private static final int FORMATION_SCORE = 3;
    private static final int FORMATION_BONUS_SCORE = 1;

    /**
     * Evaluates the state.
     *
     * @param state The state to be checked
     * @return Score of the state
     */
    public int evaluateState(final DraughtsState state) {
        int score = 0;

        int pieceScoreWhite = 0;
        int pieceScoreBlack = 0;
        int kingScoreWhite = 0;
        int kingScoreBlack = 0;
        int positionScoreWhite = 0;
        int positionScoreBlack = 0;
        int surroundingScoreWhite = 0;
        int surroundingScoreBlack = 0;
        int formationScoreWhite = 0;
        int formationScoreBlack = 0;

        // Get the state
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

        // Initialize all pieces
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = ((row & 1) == 0) ? 1 : 0; col < BOARDSIZE; col += 2) {
                pieces[row][col] = stateToCheck.getPiece(row, col);
            }
        }

        // Loop through all pieces
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = ((row & 1) == 0) ? 1 : 0; col < BOARDSIZE; col += 2) {
                // Get piece on position
                int piece = pieces[row][col];

                // Skip if empty
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
                        kingScoreWhite += KING_SCORE;
                    } else if (piece == DraughtsState.BLACKKING) {
                        white = false;
                        kingScoreBlack += KING_SCORE;
                    }
                }

                // Check position for score
                int positionScore = calculatePositionScore(row, col, white, !man);
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
                
            }
        }
        
        // Check possible formations for score
        // Get the furthest ahead piece per color for each column
        // First and last 2 columns are not checked to prefer center play more
        for (int col = 2; col < BOARDSIZE - 2; col++) {
            for (int row = ((col & 1) == 0 ? 1 : 0); row < BOARDSIZE; row += 2) {
                if (pieces[row][col] == DraughtsState.WHITEPIECE) {
                    formationScoreWhite += calculateFormationScore(pieces, row, col, true);
                    break;
                }
            }
            for (int row = ((col & 1) == 0 ? 9 : 8); row > 0; row -= 2) {
                if (pieces[row][col] == DraughtsState.BLACKPIECE) {
                    formationScoreBlack += calculateFormationScore(pieces, row, col, false);
                    break;
                }
            }
        }

        int whiteScore = pieceScoreWhite 
                + kingScoreWhite
                + positionScoreWhite 
                + surroundingScoreWhite
                + formationScoreWhite;
        int blackScore = pieceScoreBlack 
                + kingScoreBlack
                + positionScoreBlack 
                + surroundingScoreBlack
                + formationScoreBlack;
        
        // Give an advantage to the color with more pieces
        if (pieceScoreWhite > pieceScoreBlack) {
            whiteScore += MAN_SCORE;
        } else if (pieceScoreBlack > pieceScoreWhite) {
            blackScore += MAN_SCORE;
        }
        
        if (kingScoreWhite > kingScoreBlack) {
            whiteScore += KING_SCORE;
        } else if (kingScoreBlack > kingScoreWhite) {
            blackScore += KING_SCORE;
        }

        score = whiteScore - blackScore;

        return score;
    }

    /**
     * Calculates the score for the position of the pieces
     *
     * Maximum of 6 + 9 = 15;
     */
    private int calculatePositionScore(int row, int col, boolean white, boolean king) {
        int score = 0;

        // Add score based on row, center is better than edges
        if (!king) {
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
        } else {
            score += 6;
        }

        // Add score based on column, further forward is better
        // King's vertical positioning may be neglected
        if (!king) {
            if (white) {
                row = 9 - row;
            }
            score += row;
        } else {
            score += 9;
        } // must give max score to kings to not have "dip" in score upon promotion

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
     * Checks for possible formations of 3 pieces in a row.
     * Adds score based on number of possibilities until a maximum of 3.
     * 
     * 
     */
    private int calculateFormationScore(int[][] pieces, int row, int col, boolean white) {
        int score = 0;
        
        //check for row for pieces to make formation
        int piecesLeft = checkPossiblePieces(pieces, row + 1, col - 1, white);
        if (piecesLeft >= 2) {
            int piecesLeftLeft;
            piecesLeftLeft = checkPossiblePieces(pieces, row + 2, col - 3, white);
            if (piecesLeftLeft >= 1) {
                // Formation possible
                score += FORMATION_SCORE;

                // Bonus score if there is more than 1 possible formation
                if (piecesLeft > 2) {
                    score += FORMATION_BONUS_SCORE;
                }
                if (piecesLeftLeft > 1) {
                    score += FORMATION_BONUS_SCORE;
                }
            }
        }
        
        int piecesRight = checkPossiblePieces(pieces, row + 1, col - 1, white);
        if (piecesRight >= 2) {
            int piecesRightRight;
            piecesRightRight = checkPossiblePieces(pieces, row + 2, col - 3, white);
            if (piecesRightRight >= 1) {
                // Formation possible
                score += FORMATION_SCORE;

                // Bonus score if there is more than 1 possible formation
                if (piecesRight > 2) {
                    score += FORMATION_BONUS_SCORE;
                }
                if (piecesRightRight > 1) {
                    score += FORMATION_BONUS_SCORE;
                }
            }
        }
        
        return score;
    }
    
    /**
    * Checks for a certain number of pieces of a certain color in a cone above/below a position.
    */
    private int checkPossiblePieces(int[][] pieces, int row, int col, boolean white) {
        int pieceCount = 0;
        
        int originalCol = col;
        int colCount = 0; // Increases width of cone
        
        if (white) {
            // Double for loop for the cone below initial position
            for (row++; row < BOARDSIZE; row++) {
                colCount++;
                for (col = Math.max(originalCol - colCount, 0); col <= originalCol + colCount && col < BOARDSIZE; col += 2) {
                    // Check if there is a white piece in the cone
                    int piece = pieces[row][col];
                    if (piece == DraughtsState.WHITEPIECE || piece == DraughtsState.WHITEKING) {
                        // Increment counter
                        pieceCount++;
                    }
                }
            }
        } else {
            // Double for loop for the cone above initial position
            for (row--; row >= 0; row--) {
                colCount++;
                for (col = Math.max(originalCol - colCount, 0); col <= originalCol + colCount && col < BOARDSIZE; col += 2) {
                    // Check if there is a black piece in the cone
                    int piece = pieces[row][col];
                    if (piece == DraughtsState.BLACKPIECE || piece == DraughtsState.BLACKKING) {
                        // Increment counter
                        pieceCount++;
                    }
                }
            }
        }
        
        return pieceCount;
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
