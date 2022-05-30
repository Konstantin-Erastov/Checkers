package model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Stack;


public class Move implements Serializable {

    private final Stack<MoveStep> steps = new Stack<>();

    private final Board board;
    @Getter
    private final Player player;

    public Move(Board board, Player activePlayer) {
        this.board = board;
        this.player = activePlayer;
    }

    public boolean started() {
        return !steps.empty();

    }

    public Cell getLast() {
        return steps.lastElement().getCell2();
    }

    public void addFirstStep(Cell cell) {
        if (started()) {
            throw new RuntimeException("can't set start cell to started move");
        }
        steps.push(new MoveStep(cell));

        cell.setSelected(true);
    }

    public MoveStep addStep(Cell cell) {
        if (!started()) {
            throw new RuntimeException("start first");
        }
        Cell eatenCell = null;
        if (board.canEat(getLast(), cell)) {
            eatenCell = board.getEatenCell(getLast(), cell);
        }
        var step = new MoveStep(getLast(), cell, eatenCell);
        steps.push(step);
        cell.setSelected(true);

        System.out.println("cell " + cell + " added to move");
        return step;
    }

    public boolean contains(Cell cell) {
        return steps.stream().anyMatch(x -> x.getCell2() == cell);
    }

    public void undoUntil(Cell cell) {

        while (true) {
            var last = steps.peek();
            if (last.getCell2() == cell) {
                if (steps.size() == 1) {
                    steps.pop();
                    last.getCell2().unselect();
                }
                break;
            }
            steps.pop();
            last.getCell2().unselect();
            board.undo(last);
        }
        if (started()) {
            steps.peek().getCell2().select();
        }
    }

    public void unselectCells() {
        steps.forEach(x -> x.getCell2().unselect());
    }

    public void undo() {
        undoUntil(steps.firstElement().getCell2());
    }
}
