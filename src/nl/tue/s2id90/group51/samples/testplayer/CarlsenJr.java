package nl.tue.s2id90.group51.samples.testplayer;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group51.AIStoppedException;
import nl.tue.s2id90.group51.DraughtsNode;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 *
 * @author huub
 */
public class CarlsenJr extends DraughtsPlayer {

    private int bestValue = 0;
    int maxSearchDepth;
    AlphaBeta alphaBeta;
    Evaluate evaluate;

    /**
     * boolean that indicates that the GUI asked the player to stop thinking.
     */
    public boolean stopped;

    public CarlsenJr(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
        this.evaluate = new Evaluate(this);
        this.alphaBeta = new AlphaBeta(this, evaluate);
    }

    @Override
    public Move getMove(DraughtsState s) {
        System.err.println("Entering getMove()");
        Move bestMove = null;
        bestValue = 0;
        
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {        
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
            System.err.println("bestValue computed!");
        } catch (AIStoppedException ex) {
            System.err.println("AIStoppedException caught!"); 
        } finally {
            System.err.println("Entered finally"); 
            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove = node.getBestMove();
            
            // print the results for debugging reasons
            System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(), maxSearchDepth, bestMove, bestValue
            );
        }
        
        if (bestMove == null) {
            System.err.println("no valid move found!");
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
        List<Move> oldMoveList = new ArrayList<Move>();
        for (int i = 1; i <= depth; i++) {
            if (stopped) {
                stopped = false;
                throw new AIStoppedException();
            }
            ArrayList<Move> depthMoveList = new ArrayList<Move>();
            if (!node.getState().isWhiteToMove()) {
                returnValue = alphaBeta.alphaBetaMax(node, alpha, beta, i, depthMoveList, oldMoveList);
            } else {
                returnValue = alphaBeta.alphaBetaMin(node, alpha, beta, i, depthMoveList, oldMoveList);
            }
            Collections.reverse(depthMoveList);
            oldMoveList = depthMoveList;
        }
        return returnValue;
    }

    /**
     * A method that evaluates the given state.
     */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        return evaluate.evaluateState(state);
    }
}
