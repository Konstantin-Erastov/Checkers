import lombok.Setter;
import model.Cell;
import model.Draught;
import model.Game;
import model.Position;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class BoardPanel extends JPanel {

    private Game game;

    public BoardPanel() {
        super();
        setBorder(new LineBorder(Color.BLUE));
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var cell = getCell(e.getPoint());
                System.out.println("cell " + cell + " clicked");
                if (cell != null && game.getState().equals("in-progress")) {
                    game.onCellClicked(cell);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public void setGame(Game game) {
        this.game = game;
        game.setRedrawBoard(this::repaint);

    }

    private Cell getCell(Point point) {

        if (point.x > getBoardLength() || point.y > getBoardLength()) {

            return null;
        }
        var x = point.x / getCellLength(); //
        var y = point.y / getCellLength();
        var position = Position.fromXy(x, y);

        return game.getBoard().getCell(position);
    }

    private int getBoardLength() {
        return getCellLength() * 8;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        var size = getSize();
        var len = Math.min(size.width, size.height);
        g.drawLine(10, 10, len, len);
        paintCells(g);
    }

    private void paintCells(Graphics g) {
        var cells = game.getBoard().getCells();
        Arrays.stream(cells)
                .flatMap(Arrays::stream)
                .forEach(cell -> paintCell(g, cell));
    }

    private void paintCell(Graphics g, Cell cell) {
        var position = cell.getPosition();
        var i = position.i();
        var j = position.j();
        var length = getCellLength();
        var cellPosition = new Position(i * length, j * length);
        g.setColor(cell.getColor());
        g.fillRect(cellPosition.x(), cellPosition.y(), length, length);

        if (cell.hasDraught()) {

            paintDraught(g, cell.getDraught(), new Position((int) (cellPosition.i() + 0.1 * length), (int) (cellPosition.j() + 0.1 * length)), 0.8 * length);
        }
        if (cell.getEnabled()) {
            g.setColor(Color.gray);
            g.drawOval(j * length + 2, i * length + 2, 2, 2);
        }
        if (cell.isSelected()) {
            g.setColor(Color.BLACK);
            g.drawRect(j * length, i * length, length, length);
        }
    }

    private void paintDraught(Graphics g, Draught draught, Position position, double length) {
        g.setColor(draught.getPlayer().getColor());
        g.fillOval(position.x(), position.y(), (int) length, (int) length);
        if (draught.isQueen()) {
            g.setColor(Color.YELLOW);
            g.drawOval(position.x() + 2, position.y() + 2, (int) length - 4, (int) length - 4);

        }

    }

    private int getCellLength() {
        var size = getSize();
        var length = Math.min(size.height, size.width);
        return length / 8;
    }


}
