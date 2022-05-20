package model;

import java.io.Serializable;

public record Position(int i, int j) implements Serializable {
    public int x() {
        return j;
    }

    public int y() {
        return i;
    }

    public static Position fromXy(int x, int y) {
        return new Position(y, x);
    }

    public int row() {
        return i;
    }

    public int column() {
        return j;
    }

    public Position add(Position other) {
        return new Position(i + other.i(), j + other.j);
    }

    public Position subtract(Position other) {
        return new Position(i - other.i(), j - other.j);
    }

    public boolean isSquare() {
        return absI() == absJ();
    }

    public int absI() {
        return Math.abs(i);
    }

    public int absJ() {
        return Math.abs(j);
    }

    public boolean diagonalWith(Position other) {
        return this.subtract(other).isSquare();
    }

    @Override
    public String toString() {
        return "(" + i + ":" + j + ")";
    }
}
