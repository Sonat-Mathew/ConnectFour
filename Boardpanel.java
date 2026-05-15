import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel {

    private static final Color BG_DARK     = new Color(13,  17,  28);
    private static final Color BOARD_BLUE  = new Color(30,  60,  200);
    private static final Color CELL_EMPTY  = new Color(18,  22,  35);
    private static final Color HUMAN_COLOR = new Color(210, 40,  40);
    private static final Color BOT_COLOR   = new Color(210, 185, 30);
    private static final Color WIN_RING    = new Color(255, 255, 255);
    private static final Color HOVER_COLOR = new Color(255, 255, 255, 40);

    private static final int CELL_SIZE = 80;
    private static final int PADDING   = 20;
    private static final int PIECE_PAD = 10;

    private final GameLogic  logic;
    private final GameWindow parent;

    private int     hoverCol = -1;
    private int[][] winCells = null;
    private boolean gameOver = false;

    public BoardPanel(GameLogic logic, GameWindow parent) {
        this.logic  = logic;
        this.parent = parent;

        setBackground(BG_DARK);
        setPreferredSize(new Dimension(
                GameLogic.COLS * CELL_SIZE + PADDING * 2,
                GameLogic.ROWS * CELL_SIZE + PADDING * 2
        ));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                
                int col = getColFromX(e.getX());
                int clamped = (col >= 0 && col < GameLogic.COLS) ? col : -1;
                if (clamped != hoverCol) {
                    hoverCol = clamped;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) return;
                int col = getColFromX(e.getX());
                if (col >= 0 && col < GameLogic.COLS) {
                    parent.humanMove(col);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverCol = -1;
                repaint();
            }
        });
    }


    private int getColFromX(int x) {
        return (x - PADDING) / CELL_SIZE;
    }

    public void setWinCells(int[][] cells) {
        this.winCells = cells;
        repaint();
    }

    public void setGameOver(boolean over) {
        this.gameOver = over;
        repaint();
    }

    public void reset() {
        winCells = null;
        gameOver = false;
        hoverCol = -1;
        repaint();
    }

    private boolean isWinCell(int row, int col) {
        if (winCells == null) return false;
        for (int[] cell : winCells)
            if (cell[0] == row && cell[1] == col) return true;
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int boardW = GameLogic.COLS * CELL_SIZE;
        int boardH = GameLogic.ROWS * CELL_SIZE;

        // Board background
        g2.setColor(BOARD_BLUE);
        g2.fillRoundRect(PADDING, PADDING, boardW, boardH, 24, 24);

        // Hover column highlight
        if (hoverCol >= 0 && hoverCol < GameLogic.COLS && !gameOver) {
            g2.setColor(HOVER_COLOR);
            int hx = PADDING + hoverCol * CELL_SIZE;
            g2.fillRoundRect(hx, PADDING, CELL_SIZE, boardH, 8, 8);
        }

        // Cells
        int[][] board = logic.getBoard();
        for (int row = 0; row < GameLogic.ROWS; row++) {
            for (int col = 0; col < GameLogic.COLS; col++) {
                int x    = PADDING + col * CELL_SIZE + PIECE_PAD;
                int y    = PADDING + row * CELL_SIZE + PIECE_PAD;
                int size = CELL_SIZE - PIECE_PAD * 2;

                // Empty hole
                g2.setColor(CELL_EMPTY);
                g2.fillOval(x, y, size, size);

                // Piece
                int cell = board[row][col];
                if (cell == GameLogic.HUMAN) {
                    g2.setColor(HUMAN_COLOR);
                    g2.fillOval(x, y, size, size);
                } else if (cell == GameLogic.BOT) {
                    g2.setColor(BOT_COLOR);
                    g2.fillOval(x, y, size, size);
                }

                // Win ring
                if (isWinCell(row, col)) {
                    g2.setColor(WIN_RING);
                    g2.setStroke(new BasicStroke(4f));
                    g2.drawOval(x + 2, y + 2, size - 4, size - 4);
                    g2.setStroke(new BasicStroke(1f));
                }
            }
        }
    }
}