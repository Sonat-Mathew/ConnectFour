public class GameLogic {

    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int EMPTY = 0;
    public static final int HUMAN = 1;
    public static final int BOT = 2;

    private int[][] board;

    public GameLogic() {
        board = new int[ROWS][COLS];
    }

    public int[][] getBoard() {
        return board;
    }

    public void resetBoard() {
        board = new int[ROWS][COLS];
    }


    public int dropPiece(int col, int player) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = player;
                return row;
            }
        }
        return -1;
    }

    public boolean isColumnFull(int col) {
        return board[0][col] != EMPTY;
    }

    public boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (!isColumnFull(col)) return false;
        }
        return true;
    }

    public boolean checkWin(int player) {
        // Horizontal
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r][c+1] == player &&
                        board[r][c+2] == player && board[r][c+3] == player)
                    return true;

        // Vertical
        for (int r = 0; r <= ROWS - 4; r++)
            for (int c = 0; c < COLS; c++)
                if (board[r][c] == player && board[r+1][c] == player &&
                        board[r+2][c] == player && board[r+3][c] == player)
                    return true;

        // Diagonal down-right
        for (int r = 0; r <= ROWS - 4; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r+1][c+1] == player &&
                        board[r+2][c+2] == player && board[r+3][c+3] == player)
                    return true;

        // Diagonal up-right
        for (int r = 3; r < ROWS; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r-1][c+1] == player &&
                        board[r-2][c+2] == player && board[r-3][c+3] == player)
                    return true;

        return false;
    }

    // Get winning positions
    public int[][] getWinningCells(int player) {
        // Horizontal
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r][c+1] == player &&
                        board[r][c+2] == player && board[r][c+3] == player)
                    return new int[][]{{r,c},{r,c+1},{r,c+2},{r,c+3}};

        // Vertical
        for (int r = 0; r <= ROWS - 4; r++)
            for (int c = 0; c < COLS; c++)
                if (board[r][c] == player && board[r+1][c] == player &&
                        board[r+2][c] == player && board[r+3][c] == player)
                    return new int[][]{{r,c},{r+1,c},{r+2,c},{r+3,c}};

        // Diagonal downright
        for (int r = 0; r <= ROWS - 4; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r+1][c+1] == player &&
                        board[r+2][c+2] == player && board[r+3][c+3] == player)
                    return new int[][]{{r,c},{r+1,c+1},{r+2,c+2},{r+3,c+3}};

        // Diagonal upright
        for (int r = 3; r < ROWS; r++)
            for (int c = 0; c <= COLS - 4; c++)
                if (board[r][c] == player && board[r-1][c+1] == player &&
                        board[r-2][c+2] == player && board[r-3][c+3] == player)
                    return new int[][]{{r,c},{r-1,c+1},{r-2,c+2},{r-3,c+3}};

        return null;
    }

    //  MINIMAX

    public int getBotMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = 3; // default center
        for (int col = 0; col < COLS; col++) {
            if (!isColumnFull(col)) {
                int row = dropPiece(col, BOT);
                int score = minimax(4, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                board[row][col] = EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }
        return bestCol;
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        if (checkWin(BOT)) return 1000 + depth;
        if (checkWin(HUMAN)) return -1000 - depth;
        if (isBoardFull() || depth == 0) return evaluateBoard();

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (!isColumnFull(col)) {
                    int row = dropPiece(col, BOT);
                    int score = minimax(depth - 1, alpha, beta, false);
                    board[row][col] = EMPTY;
                    maxScore = Math.max(maxScore, score);
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha) break;
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (!isColumnFull(col)) {
                    int row = dropPiece(col, HUMAN);
                    int score = minimax(depth - 1, alpha, beta, true);
                    board[row][col] = EMPTY;
                    minScore = Math.min(minScore, score);
                    beta = Math.min(beta, score);
                    if (beta <= alpha) break;
                }
            }
            return minScore;
        }
    }

    private int evaluateBoard() {
        int score = 0;
        // Prefer center column
        for (int r = 0; r < ROWS; r++) {
            if (board[r][3] == BOT) score += 3;
        }
        // Evaluate windows
        score += evaluateAllWindows(BOT) - evaluateAllWindows(HUMAN) * 2;
        return score;
    }

    private int evaluateAllWindows(int player) {
        int score = 0;
        int[][] directions = {{0,1},{1,0},{1,1},{1,-1}};
        for (int[] d : directions) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int count = 0, empty = 0;
                    for (int i = 0; i < 4; i++) {
                        int nr = r + d[0]*i, nc = c + d[1]*i;
                        if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) { count = -1; break; }
                        if (board[nr][nc] == player) count++;
                        else if (board[nr][nc] == EMPTY) empty++;
                    }
                    if (count == 3 && empty == 1) score += 5;
                    else if (count == 2 && empty == 2) score += 2;
                }
            }
        }
        return score;
    }
}