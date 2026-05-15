import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    // Colors
    private static final Color BG_DARK      = new Color(13,  17,  28);
    private static final Color ACCENT_BLUE  = new Color(30,  80,  220);
    private static final Color HUMAN_COLOR  = new Color(210, 40,  40);
    private static final Color BOT_COLOR    = new Color(210, 185, 30);
    private static final Color TEXT_WHITE   = new Color(230, 230, 240);
    private static final Color TEXT_GRAY    = new Color(140, 145, 160);
    private static final Color CARD_BG      = new Color(22,  27,  42);
    private static final Color BORDER_COLOR = new Color(45,  52,  75);

    private final GameLogic  logic;
    private final BoardPanel boardPanel;

    private JLabel turnLabel;
    private JLabel humanScoreLabel;
    private JLabel botScoreLabel;


    private JPanel humanScoreCard;
    private JPanel botScoreCard;

    private int     humanScore = 0;
    private int     botScore   = 0;
    private boolean humanTurn  = true;
    private boolean gameOver   = false;

    public GameWindow() {
        logic = new GameLogic();


        boardPanel = new BoardPanel(logic, this);

        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    // TOP BAR
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_DARK);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel("⬡  CONNECT FOUR");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_WHITE);

        JLabel online = new JLabel("⚉  ONLINE");
        online.setFont(new Font("SansSerif", Font.BOLD, 13));
        online.setForeground(TEXT_GRAY);

        bar.add(title,  BorderLayout.WEST);
        bar.add(online, BorderLayout.EAST);
        return bar;
    }

    // CENTER
    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));


        turnLabel = new JLabel("● YOUR TURN", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnLabel.setForeground(TEXT_WHITE);
        turnLabel.setOpaque(true);
        turnLabel.setBackground(BG_DARK);
        turnLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 2, true),
                BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel scoreRow = buildScoreRow();
        scoreRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(turnLabel);
        center.add(Box.createVerticalStrut(18));
        center.add(scoreRow);
        center.add(Box.createVerticalStrut(18));
        center.add(boardPanel);

        return center;
    }

    private JPanel buildScoreRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setBackground(BG_DARK);

        row.setMaximumSize(new Dimension(boardPanel.getPreferredSize().width, 80));

        humanScoreLabel = new JLabel("0", SwingConstants.CENTER);
        botScoreLabel   = new JLabel("0", SwingConstants.CENTER);


        humanScoreCard = buildScoreCard("HUMAN", humanScoreLabel, true);
        botScoreCard   = buildScoreCard("BOT",   botScoreLabel,   false);

        row.add(humanScoreCard);
        row.add(botScoreCard);
        return row;
    }

    private JPanel buildScoreCard(String title, JLabel scoreLabel, boolean active) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? TEXT_WHITE : BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLbl.setForeground(TEXT_GRAY);

        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        scoreLabel.setForeground(TEXT_WHITE);

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(scoreLabel, BorderLayout.CENTER);
        return card;
    }


    private void updateScoreCardBorders(boolean humanActive) {
        if (humanScoreCard == null || botScoreCard == null) return;
        humanScoreCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(humanActive ? TEXT_WHITE : BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        botScoreCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(humanActive ? BORDER_COLOR : TEXT_WHITE, 2, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        humanScoreCard.repaint();
        botScoreCard.repaint();
    }


    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_DARK);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton newGame = new JButton("NEW GAME");
        newGame.setFont(new Font("SansSerif", Font.BOLD, 16));
        newGame.setForeground(Color.WHITE);
        newGame.setBackground(ACCENT_BLUE);
        newGame.setFocusPainted(false);
        newGame.setBorderPainted(false);
        newGame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGame.setPreferredSize(new Dimension(200, 52));

        newGame.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(((JButton) c).getModel().isPressed()
                        ? ACCENT_BLUE.darker() : ACCENT_BLUE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                FontMetrics fm = g2.getFontMetrics(c.getFont());
                g2.setColor(Color.WHITE);
                g2.setFont(c.getFont());
                String text = ((JButton) c).getText();
                int x = (c.getWidth()  - fm.stringWidth(text)) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);
            }
        });

        newGame.addActionListener(e -> resetGame());
        bottom.add(newGame, BorderLayout.CENTER);
        return bottom;
    }

    // GAME LOGIC

    public void humanMove(int col) {
        if (!humanTurn || gameOver) return;
        if (logic.isColumnFull(col)) return;

        logic.dropPiece(col, GameLogic.HUMAN);
        boardPanel.repaint();

        if (logic.checkWin(GameLogic.HUMAN)) {
            humanScore++;
            humanScoreLabel.setText(String.valueOf(humanScore));
            endGame("🎉 YOU WIN!", GameLogic.HUMAN);
            return;
        }
        if (logic.isBoardFull()) { endGame("DRAW!", 0); return; }

        humanTurn = false;
        updateTurnLabel();

        Timer timer = new Timer(400, e -> botMove());
        timer.setRepeats(false);
        timer.start();
    }

    private void botMove() {
        int col = logic.getBotMove();
        logic.dropPiece(col, GameLogic.BOT);
        boardPanel.repaint();

        if (logic.checkWin(GameLogic.BOT)) {
            botScore++;
            botScoreLabel.setText(String.valueOf(botScore));
            endGame("BOT WINS!", GameLogic.BOT);
            return;
        }
        if (logic.isBoardFull()) { endGame("DRAW!", 0); return; }

        humanTurn = true;
        updateTurnLabel();
    }

    private void endGame(String message, int winner) {
        gameOver = true;
        boardPanel.setGameOver(true);
        if (winner != 0) {
            boardPanel.setWinCells(logic.getWinningCells(winner));
        }
        turnLabel.setText(message);
        turnLabel.setForeground(
                winner == GameLogic.HUMAN ? HUMAN_COLOR :
                        winner == GameLogic.BOT   ? BOT_COLOR   : TEXT_GRAY
        );
        // Clear active-border state when game ends
        updateScoreCardBorders(false);
    }

    private void updateTurnLabel() {
        if (humanTurn) {
            turnLabel.setText("● YOUR TURN");
            turnLabel.setForeground(TEXT_WHITE);
        } else {
            turnLabel.setText("● BOT THINKING...");
            turnLabel.setForeground(BOT_COLOR);
        }
        updateScoreCardBorders(humanTurn);
    }

    private void resetGame() {
        logic.resetBoard();
        humanTurn = true;
        gameOver  = false;
        boardPanel.reset();
        updateTurnLabel();
    }
}