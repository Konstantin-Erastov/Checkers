package model;


import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

@Getter
public class Cell  implements Serializable {
    private final Board board;
    private final Position position;
    private final Color color;


    @Setter
    private boolean isSelected;

    private Boolean enabled = false;
    private Draught draught;

    public Cell(Board board, Position position, Color color) {

        this.board = board;
        this.position = position;
        this.color = color;
    }


    public boolean hasDraught() {
        return draught != null;
    }

    public boolean isEmpty() {
        return draught == null;
    }

    public void removeDraught() {
        if (!hasDraught()) {
            throw new RuntimeException("can't remove null draught");
        }

        draught.setCell(null);
        draught = null;

    }

    public void setDraught(Draught draught) {
        if (this.draught != null) {
            throw new RuntimeException("cell s already occupied");
        }
        this.draught = draught;
        draught.setCell(this);
        if (isLastRow(draught)) {
            draught.setQueen(true);
            System.out.println("draught become queen on " + position);
        }
    }

    private boolean isLastRow(Draught draught) {
        var direction = draught.getPlayer().getDirection();
        switch (direction) {
            case UP -> {
                return position.row() == 0;
            }
            case DOWN -> {
                return position.row() == 7;
            }
            default -> throw new RuntimeException("unknown direction");
        }
    }

    public boolean hasAvailableMoves() {
        return !getAvailableMoves().isEmpty();
    }

    public List<Cell> getAvailableMoves() {
        return board.getAvailableMoves(this);
    }

    public boolean hasAvailableEats() {
        return !getAvailableEats().isEmpty();
    }
    public List<Cell> getAvailableEats() {
        return board.getAvailableEats(this);
    }

    public boolean canEat() {
        return !getAvailableEats().isEmpty();
    }


    public Cell getUpperLeftCell(Direction direction) {

        switch (direction) {
            case UP -> {
                return board.getCell(position.i() - 1, position.j() - 1);
            }
            case DOWN -> {
                return board.getCell(position.i() + 1, position.j() + 1);
            }
            default -> throw new RuntimeException("unknown direction");
        }
    }

    public Cell getUpperRightCell(Direction direction) {
        switch (direction) {
            case UP -> {
                return board.getCell(position.i() - 1, position.j() + 1);
            }
            case DOWN -> {return board.getCell(position.i() + 1, position.j() - 1);
            }
            default -> throw new RuntimeException("unknown direction");
        }
    }

    public Cell getDownLeftCell(Direction direction) {

        switch (direction) {
            case UP -> {
                return board.getCell(position.i() + 1, position.j() - 1);
            }
            case DOWN -> {
                return board.getCell(position.i() - 1, position.j() + 1);
            }
            default -> throw new RuntimeException("unknown direction");
        }
    }

    public Cell getDownRightCell(Direction direction) {



        switch (direction) {
            case UP -> {
                return board.getCell(position.i() + 1, position.j() + 1);
            }
            case DOWN -> {
                return board.getCell(position.i() - 1, position.j() - 1);
            }
            default -> throw new RuntimeException("unknown direction");
        }
    }

    public void select() {
        setSelected(true);
    }

    public void unselect() {
        setSelected(false);
    }

    public boolean diagonalWith(Cell cell2) {
        return position.diagonalWith(cell2.getPosition());
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public void enable() {

        enabled = true;
    }

    public void disable() {
        if (!enabled){
            throw new IllegalStateException();
        }
        enabled = false;
    }

}
