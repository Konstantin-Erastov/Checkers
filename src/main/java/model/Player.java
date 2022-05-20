package model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
public class Player  implements Serializable {
    private final Color color;
    private final String name;
    private final Direction direction;
    @Setter
    private Move move;

    private final Set<Draught> draughts = new HashSet<>();

    public Player(Game game, Color color, String name, Direction direction) {
        this.color = color;
        this.name = name;
        this.direction = direction;
        var board = game.getBoard();
        move = new Move(board, this);
    }

    public boolean isMine(Cell cell) {
        var draught = cell.getDraught();
        return draught != null && draught.getPlayer() == this;
    }

    public boolean hasAvailableMoves() {
        return draughts.stream()
                .map(Draught::getCell)
                .filter(Objects::nonNull)
                .anyMatch(Cell::hasAvailableMoves);
    }
}
