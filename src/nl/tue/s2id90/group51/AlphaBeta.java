/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group51;

import java.util.ArrayList;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 *
 * @author Kerry, Vlad
 */
public class AlphaBeta {

    // TODO: don't count forced capture moves as depth
    // reference to the player who created this AlphaBeta
    // necessary to check whether we are ordered to stop (with player.stopped)
    MagnusCarlsen player;
    Evaluate evaluate;
    int statesSearched = 0;
    int statesEvaluated = 0;

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
    public AlphaBeta(MagnusCarlsen player, Evaluate evaluate) {
        this.player = player;
        this.evaluate = evaluate;
    }

    public int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth, List<Move> moveList, List<Move> oldMoveList)
            throws AIStoppedException {
        // throw an exception if our player is forced to stop
        if (player.stopped) {
            player.stopped = false;
            throw new AIStoppedException();
        }

        // obtain a reference to the current active state
        DraughtsState currState = node.getState();
        // if final depth reached, then return the value of this leaf
        if (depth <= 0 || currState.isEndState()) {
            statesEvaluated++;
            return evaluate.evaluateState(currState);
        }

        // if not final depth, then generate all possible branches
        // determine the best move and corresponding state-value
        List<Move> moves = currState.getMoves();

        // if only a single move is possible, don't decrease depth and jump to next level right away
        if (moves.size() == 1) {
            statesSearched++;
            Move move = moves.get(0);
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            List<Move> childMoveList = new ArrayList<Move>();
            int result = alphaBetaMin(childNode, alpha, beta, depth - 1, childMoveList, oldMoveList);
            currState.undoMove(move);
            alpha = result;
            node.setBestMove(move);
            childMoveList.add(move);
            // append child move list to move list
            moveList.addAll(childMoveList);
            return alpha;
        }

        // if the list of old-moves is not empty yet, then continue
        // tracing the best move of the previous run by putting the old move
        // in the first position to be evaluated.
        // This leads to better pruning.
        if (!oldMoveList.isEmpty()) {
            Move oldMove = oldMoveList.remove(0);

            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).equals(oldMove)) {
                    Move temp = moves.get(0);
                    moves.set(0, moves.get(i));
                    moves.set(i, temp);
                    break;
                }
            }
        }

        // keep track of the moveList of the best move
        // combine it with the input moveList to get full move-history
        List<Move> bestMoveList = new ArrayList<>();

        //access via new for-loop
        for (Move move : moves) {
            statesSearched++;
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            List<Move> childMoveList = new ArrayList<Move>();
            int result = alphaBetaMin(childNode, alpha, beta, depth - 1, childMoveList, oldMoveList);
            currState.undoMove(move);
            if (result > alpha) {
                alpha = result;
                node.setBestMove(move);
                bestMoveList = childMoveList;
                bestMoveList.add(move);
            }
            if (alpha >= beta) {
                // append child move list to move list
                moveList.addAll(bestMoveList);
                return (alpha + 1);
            }
        }
        // append child move list to move list
        moveList.addAll(bestMoveList);
        return alpha;
    }

    public int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth, List<Move> moveList, List<Move> oldMoveList)
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
            statesEvaluated++;
            return evaluate.evaluateState(currState);
        }

        // if not final depth, then generate all possible branches
        // determine the best move and corresponding state-value
        List<Move> moves = currState.getMoves();

        // if only a single move is possible, don't decrease depth and jump to next level right away
        if (moves.size() == 1) {
            statesSearched++;
            Move move = moves.get(0);
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            List<Move> childMoveList = new ArrayList<Move>();
            int result = alphaBetaMin(childNode, alpha, beta, depth - 1, childMoveList, oldMoveList);
            currState.undoMove(move);
            alpha = result;
            node.setBestMove(move);
            childMoveList.add(move);
            // append child move list to move list
            moveList.addAll(childMoveList);
            return alpha;
        }

        // if the list of old-moves is not empty yet, then continue
        // tracing the best move of the previous run by putting the old move
        // in the first position to be evaluated.
        // This leads to better pruning.
        if (!oldMoveList.isEmpty()) {
            Move oldMove = oldMoveList.remove(0);
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).equals(oldMove)) {
                    Move temp = moves.get(0);
                    moves.set(0, moves.get(i));
                    moves.set(i, temp);
                    break;
                }
            }
        }
        // keep track of the moveList of the best move
        // combine it with the input moveList to get full move-history
        List<Move> bestMoveList = new ArrayList<>();
        //access via new for-loop
        for (Move move : moves) {
            statesSearched++;
            currState.doMove(move);
            DraughtsNode childNode = new DraughtsNode(currState);
            List<Move> childMoveList = new ArrayList<Move>();
            int result = alphaBetaMax(childNode, alpha, beta, depth - 1, childMoveList, oldMoveList);
            currState.undoMove(move);
            if (result < beta) {
                beta = result;
                node.setBestMove(move);
                bestMoveList = childMoveList;
                bestMoveList.add(move);
            }
            if (alpha >= beta) {
                // append child move list to move list
                moveList.addAll(bestMoveList);
                return (beta - 1);
            }
        }
        // append child move list to move list
        moveList.addAll(bestMoveList);
        return beta;
    }

}
