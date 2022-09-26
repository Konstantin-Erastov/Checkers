package model;


import lombok.Getter;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
public class Board implements Serializable {
    private final Cell[][] cells = new Cell[8][8];

    public Board() {
        final var blackColor = Color.lightGray;
        var color = blackColor;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j] = new Cell(this, new Position(i, j), color);
                color = (color == blackColor) ? Color.WHITE : blackColor;
            }
            color = (color == blackColor) ? Color.WHITE : blackColor;
        }


    }

    public Cell getCell(Position position) {
        return cells[position.i()][position.j()];
    }

    public Cell getCell(int i, int j) {
        if (i < 0 || i > 7 || j < 0 || j > 7) {
            return null;
        }
        return cells[i][j];
    }

    public List<Cell> getCellsWithDraughtsBetween(Cell cell1, Cell cell2) {

        return getCellsBetween(cell1, cell2)
                .stream().filter(Cell::hasDraught).toList();
    }

    public List<Cell> getCellsBetween(Cell cell1, Cell cell2) {
        if (cell1.getPosition().i() > cell2.getPosition().i()) {
            var tmp = cell1;
            cell1 = cell2;
            cell2 = tmp;
        }
        var list = new ArrayList<Cell>();

        int deltaJ;
        if (cell2.getPosition().j() > cell1.getPosition().j()) {
            deltaJ = 1;
        } else {
            deltaJ = -1;
        }

        var j = cell1.getPosition().j() + deltaJ;
        for (int i = cell1.getPosition().i() + 1; i < cell2.getPosition().i(); i++) {
            list.add(cells[i][j]);
            j += deltaJ;
        }

        return list;
    }

    public List<Cell> getAvailableMoves(Cell cell) {
        return getAvailableMoves(cell, false);
    }

    public List<Cell> getAvailableEats(Cell cell) {
        return getAvailableMoves(cell, true);
    }

    public List<Cell> getAvailableMoves(Cell cell1, boolean eatsOnly) {
        var draught = cell1.getDraught();
        if (draught == null) {
            throw new RuntimeException("cell cant be empty");
        }
        var player = draught.getPlayer();
        if (draught.isQueen()) {
            return addQueenMoves(cell1, eatsOnly, player);
        } else {
            return addDraughtMoves(cell1, eatsOnly, player);
        }
    }

    private List<Cell> addQueenMoves(Cell cell1, boolean eatsOnly, Player player) {
        var moves = new ArrayList<Cell>();
        var eats = new ArrayList<Cell>();
        addQueenMove(eats, moves, eatsOnly, cell1, c -> c.getUpperLeftCell(player.getDirection()), player);
        addQueenMove(eats, moves, eatsOnly, cell1, c -> c.getUpperRightCell(player.getDirection()), player);
        addQueenMove(eats, moves, eatsOnly, cell1, c -> c.getDownLeftCell(player.getDirection()), player);
        addQueenMove(eats, moves, eatsOnly, cell1, c -> c.getDownRightCell(player.getDirection()), player);
        if (eats.size() > 0 || eatsOnly) {
            return eats;
        } else {
            return moves;
        }
    }

    private void addQueenMove(ArrayList<Cell> eats, ArrayList<Cell> moves, boolean eatsOnly, Cell cell1, Function<Cell, Cell> getNeighbour, Player player) {
        var neighbour = cell1;
        while (true) {
            neighbour = getNeighbour.apply(neighbour);
            if (neighbour == null) {
                return;
            }
            if (neighbour.hasDraught()) {
                if (player.isMine(neighbour)) {
                    return;
                }
                break;
            }
            if (!eatsOnly) {
                moves.add(neighbour);
            }
        }
        while (true) {
            neighbour = getNeighbour.apply(neighbour);
            if (neighbour == null || neighbour.hasDraught()) {
                return;
            }
            eats.add(neighbour);
        }
    }

    private List<Cell> addDraughtMoves(Cell cell1, boolean eatsOnly, Player player) {
        var cells = new ArrayList<Cell>();
        addEat(cells, cell1, c -> c.getUpperLeftCell(player.getDirection()), player);
        addEat(cells, cell1, c -> c.getUpperRightCell(player.getDirection()), player);
        addEat(cells, cell1, c -> c.getDownLeftCell(player.getDirection()), player);
        addEat(cells, cell1, c -> c.getDownRightCell(player.getDirection()), player);

        if (cells.size() > 0) {
            eatsOnly = true;
        }
        if (eatsOnly) {
            return cells;
        }
        addMove(cells, cell1, c -> c.getUpperLeftCell(player.getDirection()));
        addMove(cells, cell1, c -> c.getUpperRightCell(player.getDirection()));
        return cells;
    }

    private void addMove(ArrayList<Cell> cells, Cell cell1, Function<Cell, Cell> getNeighbour) {


        var cell2 = getNeighbour.apply(cell1);
        if (cell2 == null || cell2.hasDraught()) {
            return;
        }
        cells.add(cell2);


    }

    private void addEat(ArrayList<Cell> cells, Cell cell1, Function<Cell, Cell> getter, Player player) {


        var eaten = getter.apply(cell1);
        if (eaten == null || eaten.isEmpty() || player.isMine(eaten)) {
            return;
        }
        var cell2 = getter.apply(eaten);
        if (cell2 == null || cell2.hasDraught()) {
            return;
        }
        cells.add(cell2);

    }

    public void undo(MoveStep step) {
        var cell1 = step.getCell1();
        var cell2 = step.getCell2();
        if (cell1 == null) {

            return;
        }
        var draught = cell2.getDraught();
        cell2.removeDraught();
        cell1.setDraught(draught);
        if (step.getEatenCell() != null) {
            step.restoreEatenDraught();
        }
    }

    public Cell getEatenCell(Cell cell1, Cell cell2) {
        if (!cell1.diagonalWith(cell2)) {
            throw new RuntimeException("not diagonal cells");
        }

        var distance = cell1.getPosition().subtract(cell2.getPosition());
        if (!distance.isSquare() || (!cell1.getDraught().isQueen() && distance.absI() != 2)) {
            throw new RuntimeException("wrong distance between cells");
        }
        var cells = getCellsWithDraughtsBetween(cell1, cell2);
        if (cells.size() != 1) {
            throw new RuntimeException("wrong distance between cells");
        }
        return cells.get(0);
    }

    public void applyStep(MoveStep step) {
        var cell1 = step.getCell1();
        var cell2 = step.getCell2();
        var draught = cell1.getDraught();
        System.out.println("make move: " + cell1 + "-> " + cell2);

        if (step.getEatenCell() != null) {
            eat(step);
        }
        cell1.removeDraught();
        cell2.setDraught(draught);
    }

    private void eat(MoveStep step) {
        var player = step.getEatenDraught().getPlayer();
        System.out.println(player.getName() + " draught eaten");
        step.getEatenCell().removeDraught();
        player.getDraughts().remove(step.getEatenDraught());
        System.out.println("player " + player.getName() + " has " + player.getDraughts().size() + " draughts");
    }

    public boolean canEat(Cell cell1, Cell cell2) {

        var cells = getCellsWithDraughtsBetween(cell1, cell2);
        if (cells.size() != 1) {

            return false;
        }
        if (cells.get(0).getDraught().getPlayer() == cell1.getDraught().getPlayer()) {

            return false;
        }
        return true;
    }

    public void updateAvailableCells(Player player) {
        clearAvailableCells();
        if (player.getMove().started()) {
            addAvailableCells(player.getMove().getLast().getAvailableMoves().stream());
            return;
        }
        var moves = player.getDraughts().stream().map(Draught::getCell).filter(Cell::hasAvailableEats);
        if (moves.findAny().isEmpty()) {
            moves = player.getDraughts().stream().map(Draught::getCell).filter(Cell::hasAvailableMoves);
        } else {
            moves = player.getDraughts().stream().map(Draught::getCell).filter(Cell::hasAvailableEats);
        }
        addAvailableCells(moves);
    }

    public void clearAvailableCells() {
        Arrays.stream(cells).flatMap(Arrays::stream).filter(Cell::getEnabled).forEach(Cell::disable);
    }

    private void addAvailableCells(Stream<Cell> cells) {
        cells.forEach(Cell::enable);
    }

    public Stream<Cell> getAvailableCells() {
        return Arrays.stream(cells).flatMap(Arrays::stream).filter(Cell::getEnabled);
    }

}
