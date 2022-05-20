package model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Stack;


public class Move implements Serializable {

    private final Stack<MoveStep> cells = new Stack<>();

    private final Board board;
    @Getter
    private final Player player;

    public Move(Board board, Player activePlayer) {
        this.board = board;
        this.player = activePlayer;
    }

    public boolean started() {
        return !cells.empty();

    }

    public Cell getLast() {
        return cells.lastElement().getCell2();
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
        cells.push(step);
        cell.setSelected(true);

        System.out.println("cell " + cell + " added to move");
        return step;
    }

    public void addFirstStep(Cell cell) {
        if (started()) {
            throw new RuntimeException("can't set start cell to started move");
        }
        cells.push(new MoveStep(cell));

        cell.setSelected(true);
    }



    public boolean canAddStep(Cell cell2) {
        if (cells.size() == 1) {
            return getLast().getAvailableMoves().contains(cell2);
        }
        return getLast().getAvailableEats().contains(cell2);
    }

    public boolean contains(Cell cell) {
        return cells.stream().anyMatch(x -> x.getCell2() == cell);
    }

    public void undoUntil(Cell cell) {
        if (!contains(cell)) {
            throw new RuntimeException("");
        }
        while (true) {
            var last = cells.peek();
            if (last.getCell2() == cell) {
                if (cells.size() == 1) {
                    cells.pop();
                    last.getCell2().unselect();
                }
                break;
            }
            cells.pop();
            last.getCell2().unselect();
            board.undo(last);
        }
        if (started()) {
            cells.peek().getCell2().select();
        }
    }

    public void unselectCells() {
        cells.forEach(x -> x.getCell2().unselect());
    }

    public void undo() {
        undoUntil(cells.firstElement().getCell2());
    }
}
