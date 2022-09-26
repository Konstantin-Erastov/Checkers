package model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

@Getter
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Board board = new Board();
    private Player activePlayer;
    private Player getNextPlayer(){
        return activePlayer == player1 ? player2 : player1;
    }
    private final Player player1;
    private final Player player2;
    @Setter
    private transient Runnable redrawBoard;
    private String state = "in-progress";

    private Player winner;
    private final Stack<Move> moves = new Stack<>();

    public Game() {
        player1 = new Player(this, Color.BLUE, "player 1", Direction.UP);
        player2 = new Player(this, Color.RED, "player 2", Direction.DOWN);
    }

    public void start() {
        state = "in-progress";

        for (int j = 0; j < 8; j++) {
            if (j % 2 == 0) {
                board.getCell(0, j).setDraught(new Draught(player2));
                board.getCell(6, j).setDraught(new Draught(player1));
            } else {
                board.getCell(1, j).setDraught(new Draught(player2));
                board.getCell(7, j).setDraught(new Draught(player1));
            }
        }
        info("game started");
        setActivePlayer(player1);
    }

    public void restart(){
        Arrays.stream(board.getCells()).flatMap(Arrays::stream)
                .forEach(cell -> {
                    if (cell.hasDraught()) {
                        cell.removeDraught();
                    }
                    if (cell.getEnabled()){
                        cell.disable();
                    }
                    if (cell.isSelected()){
                        cell.unselect();
                    }
                });
        player1.getDraughts().clear();
        player2.getDraughts().clear();

        start();
        redrawBoard.run();
    }

    private static void info(String str) {
        System.out.println(str);
    }

    public void onCellClicked(Cell cell) {
        if (!state.equals("in-progress")) {
            throw new IllegalStateException("game is not started");
        }

        var move = activePlayer.getMove();
        if (move.contains(cell)) {
            move.undoUntil(cell);
            board.updateAvailableCells(activePlayer);
            redrawBoard.run();
            return;
        }

        var cells = board.getAvailableCells();
        if (cells.noneMatch(x->x==cell)) {
            info("cell is not in available cells");
            return;
        }
        if (!move.started()) {

                move.addFirstStep(cell);
                board.updateAvailableCells(activePlayer);
                redrawBoard.run();

            return;
        }
        //move.started

        var step = move.addStep(cell);
        board.applyStep(step);
        board.updateAvailableCells(activePlayer);
        if (step.getEatenCell() == null || !cell.canEat()) {

            onMoveMade();
        } else {
            System.out.println(activePlayer.getName() + " continue your move");
        }


        redrawBoard.run();
    }



    public void undo(){
        if (moves.empty()){
            info("can't undo");
            return;
        }
        if (activePlayer.getMove().started()) {
            activePlayer.getMove().undo();
        }
        var move = moves.pop();
        move.undo();
        info("last move undone");

        setActivePlayer(move.getPlayer());
    }

    private void onMoveMade() {

        moves.push(activePlayer.getMove());
        activePlayer.getMove().unselectCells();
        activePlayer.setMove(new Move(board, activePlayer));

        if (getNextPlayer().getDraughts().isEmpty()) {
            state = "finished";
            winner = activePlayer;
        }
        else if (getNextPlayer().hasAvailableMoves()) {
            toggleActivePlayer();
        } else {
            info(getNextPlayer().getName() + " can't move");
            if (!activePlayer.hasAvailableMoves()) {
                info(activePlayer.getName() + " can't move");
                state = "finished";
            }
        }
        if (state.equals("finished")) {
            board.clearAvailableCells();
            info("game over: ");
            info(getResult());
        }
    }

    private String getResult() {
        if (winner == null) {
            return "draw";
        }
        return winner.getName() + " wins";
    }


    private void toggleActivePlayer() {
        setActivePlayer(getNextPlayer());
    }
    private void setActivePlayer(Player player){
        activePlayer = player;
        System.out.println("now it's " + activePlayer.getName() + " move");
        board.updateAvailableCells(activePlayer);
        redrawBoard.run();
    }




}
