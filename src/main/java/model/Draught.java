package model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
public class Draught   implements Serializable {
    private final Player player;
    @Setter
    private boolean isQueen = false;
    private Cell cell;
    public Draught(Player player) {
        this.player = player;
        player.getDraughts().add(this);
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
    }

