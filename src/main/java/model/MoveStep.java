package model;

import lombok.Getter;

import java.io.Serializable;


@Getter
public class MoveStep implements Serializable {

    private final Cell cell1;
    private final Cell cell2;
    private final Cell eatenCell;
    private final Draught eatenDraught;

    public MoveStep(Cell cell) {
        this(null, cell, null);
    }

    public MoveStep(Cell cell1, Cell cell2, Cell eatenCell) {
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.eatenCell = eatenCell;
        this.eatenDraught = eatenCell == null ? null : eatenCell.getDraught();
    }

    public void restoreEatenDraught() {
        getEatenCell().setDraught(getEatenDraught());
    }
}
