/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group51;

import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 *
 * @author Kerry, Vlad
 */
public class AlphaBeta {

    // reference to the player who created this AlphaBeta
    // necessary to check whether we are ordered to stop (with player.stopped)
    MagnusCarlsen player;

    /**
     * Does an alphabeta computation with the given alpha and beta where the
     * player that is to move in node is the minimizing player.
     *
     * <p>
     * Typical pieces of code used in this method are:
     * <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     * <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     * <li><code>node.setBestMove(bestMove);</code></li>
     * <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     * </ul>
     * </p>
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been
     * set to true.
     */
    public AlphaBeta(MagnusCarlsen player) {
        this.player = player;
    }

    public int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        // throw an exception if our player is forced to stop
        if (player.stopped) {
            player.stopped = false;
            throw new AIStoppedException();
        }
        
        // obtain a reference to the current active state
        DraughtsState currState = node.getState();
        // if final depth reached, then return the value of this leaf
        if (depth <= 0) {
            return 0; // PLACEHOLDER, actually return the VALUE of currState
        }
        
        // if not final depth, then generate all possible branches
        // determine the best move and corresponding state-value
        List<Move> moves = currState.getMoves();
        //access via new for-loop
        for (Move move : moves) {
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            alpha = Math.max(alpha, alphaBetaMax(childNode, alpha, beta, depth - 1));
            if (alpha >= beta) {
                node.setBestMove(move);
                return beta;
            }
            currState.undoMove(move);
        }
        return alpha;
    }

    // CURRENTLY HAS BODY OF MIN COPY-PASTED, UPDATE!
    public int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        // throw an exception if our player is forced to stop
        if (player.stopped) {
            player.stopped = false;
            throw new AIStoppedException();
        }
        
        // obtain a reference to the current active state
        DraughtsState currState = node.getState();
        // if final depth reached, then return the value of this leaf
        if (depth <= 0) {
            return 0; // PLACEHOLDER, actually return the VALUE of currState
        }
        
        // if not final depth, then generate all possible branches
        // determine the best move and corresponding state-value
        List<Move> moves = currState.getMoves();
        //access via new for-loop
        for (Move move : moves) {
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            beta = Math.min(beta, alphaBetaMax(childNode, alpha, beta, depth - 1));
            if (alpha >= beta) {
                node.setBestMove(move);
                return alpha;
            }
            currState.undoMove(move);
        }
        return beta;
    }

}
