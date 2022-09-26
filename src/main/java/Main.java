import model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class Main {
    private static Game game = new Game();
    public static void main(String[] args){

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setVisible(true);
        var boardPanel = new BoardPanel();
        boardPanel.setGame(game);
        frame.setContentPane(new MainForm(boardPanel).getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setSize(500,500);
        frame.setLocation(500,400);
        frame.setTitle("checkers");



        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu(boardPanel));

        frame.setJMenuBar(menuBar);

        game.restart();
    }
    private static JMenu createFileMenu(BoardPanel boardPanel)
    {
        JMenu file = new JMenu("File");
        JMenuItem newGame = new JMenuItem("New game");
        newGame.addActionListener(arg0 -> game.restart());
        file.add(newGame);

        JMenuItem save = new JMenuItem("save");
        save.addActionListener(arg0 -> saveGame(game));
        file.add(save);

        JMenuItem load = new JMenuItem("load");
        load.addActionListener(arg0 -> {
            game = loadGame(boardPanel);
            boardPanel.repaint();
        });
        file.add(load);


        JMenuItem undo = new JMenuItem("undo");
        undo.addActionListener(arg0 -> {
            game.undo();
        });
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        file.add(undo);

        file.addSeparator();

        JMenuItem exit = new JMenuItem(new ExitAction());
        file.add(exit);

        return file;
    }

    private static void saveGame(Game game)   {

        try {

        FileOutputStream outputStream = new FileOutputStream("game.txt");
        ObjectOutputStream objectOutputStream = null;
            objectOutputStream = new ObjectOutputStream(outputStream);


        objectOutputStream.writeObject(game);


        objectOutputStream.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    }

    private static Game loadGame(BoardPanel boardPanel)   {

        try {
            var fileInputStream = new FileInputStream("game.txt");
            ObjectInputStream objectInputStream = null;
            objectInputStream = new ObjectInputStream(fileInputStream);
            var game = (Game) objectInputStream.readObject();
            boardPanel.setGame(game);
            return game;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    static class ExitAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        ExitAction() {
            putValue(NAME, "Exit");
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
