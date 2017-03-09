package nl.tue.s2id90.group51;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 *
 * @author huub
 */
public class MagnusCarlsen extends DraughtsPlayer {

    private int bestValue = 0;
    private Move bestMoveFound;
    int depthReached = 0;
    int maxSearchDepth;

    // variables used to test usefulness of moveLists
    int totDepthReached;
    int totStatesSearched;
    int turnCount;

    AlphaBeta alphaBeta;
    Evaluate evaluate;

    /**
     * boolean that indicates that the GUI asked the player to stop thinking.
     */
    public boolean stopped;

    public MagnusCarlsen(int maxSearchDepth) {
        super("best.png");
        this.maxSearchDepth = maxSearchDepth;
        this.evaluate = new Evaluate(this);
        this.alphaBeta = new AlphaBeta(this, evaluate);
    }

    @Override
    public Move getMove(DraughtsState s) {
        System.err.println("Current state score: " + evaluate.evaluateState(s));

        Move bestMove = null;
        bestValue = 0;
        turnCount++;
        System.err.println("===============================");
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
            System.err.println("Completed full search");
        } catch (AIStoppedException ex) {
            System.err.println("AIStoppedException caught - search aborted prematurely");
        } finally {
            System.err.println("Turncount: " + turnCount);
            System.err.println("Max depth searched: " + depthReached);
            totDepthReached += depthReached;
            System.err.println("Tot depth searched: " + totDepthReached);
            System.err.println("States searched: " + alphaBeta.statesSearched);
            totStatesSearched += alphaBeta.statesSearched;
            System.err.println("Tot states searched: " + totStatesSearched);
            System.err.println("States evaluated: " + alphaBeta.statesEvaluated);
            System.err.println("Best value found: " + bestValue);

            // set bestMove to the best move found of largest completed iteration
            bestMove = bestMoveFound;

            // print the results for debugging reasons
            System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(), maxSearchDepth, bestMove, bestValue
            );
        }

        if (bestMove == null) {
            System.err.println("ERROR: No valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    }

    /**
     * This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to
     * getMove(s).
     */
    @Override
    public Integer getValue() {
        return bestValue;
    }

    /**
     * Tries to make alphabeta search stop. Search should be implemented such
     * that it throws an AIStoppedException when boolean stopped is set to true;
     *
     */
    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * returns random valid move in state s, or null if no moves exist.
     */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Implementation of alphabeta that automatically chooses the white player
     * as maximizing player and the black player as minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     *
     */
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        //iterative deepening
        int returnValue = 0;
        depthReached = 0;
        alphaBeta.statesSearched = 0;
        alphaBeta.statesEvaluated = 0;
        List<Move> oldMoveList = new ArrayList<>();
        for (int i = 1; i <= depth; i++) {
            if (stopped) {
                stopped = false;
                throw new AIStoppedException();
            }
            ArrayList<Move> depthMoveList = new ArrayList<>();
            if (node.getState().isWhiteToMove()) {
                returnValue = alphaBeta.alphaBetaMax(node, alpha, beta, i, depthMoveList, oldMoveList);
            } else {
                returnValue = alphaBeta.alphaBetaMin(node, alpha, beta, i, depthMoveList, oldMoveList);
            }
            bestValue = returnValue;
            bestMoveFound = node.getBestMove();
            depthReached = i;

            Collections.reverse(depthMoveList);
            oldMoveList = depthMoveList;
        }
        return returnValue;
    }

    /**
     * A method that evaluates the given state.
     */
    int evaluate(DraughtsState state) {
        return evaluate.evaluateState(state);
    }
}
