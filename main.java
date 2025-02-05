import java.util.*;

class KnightTour {
    private static final int[] MOVE_X = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] MOVE_Y = {1, 2, 2, 1, -1, -2, -2, -1};
    private int n;
    private int[][] board;
    private long startTime;
    private long timeLimit;
    private int nodesExpanded;

    public KnightTour(int n, long timeLimit) {
        this.n = n;
        this.board = new int[n][n];
        this.timeLimit = timeLimit * 1000; // Convert seconds to milliseconds
        for (int[] row : board) Arrays.fill(row, -1);
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n && board[x][y] == -1;
    }

    public boolean solve(int method, int startX, int startY) {
        startTime = System.currentTimeMillis();
        nodesExpanded = 0;
        board[startX][startY] = 0;

        switch (method) {
            case 1:
                return bfs(startX, startY);
            case 2:
                return dfs(startX, startY, 1);
            case 3:
                return dfsWithHeuristic(startX, startY, 1, "h1b");
            case 4:
                return dfsWithHeuristic(startX, startY, 1, "h2");
            default:
                return false;
        }
    }

    private boolean bfs(int startX, int startY) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY, 0});

        while (!queue.isEmpty()) {
            if (System.currentTimeMillis() - startTime > timeLimit) return false;

            int[] current = queue.poll();
            int x = current[0], y = current[1], move = current[2];
            nodesExpanded++;

            if (move == n * n - 1) return true;

            for (int i = 0; i < 8; i++) {
                int nextX = x + MOVE_X[i];
                int nextY = y + MOVE_Y[i];
                if (isValidMove(nextX, nextY)) {
                    board[nextX][nextY] = move + 1;
                    queue.add(new int[]{nextX, nextY, move + 1});
                }
            }
        }
        return false;
    }

    private boolean dfs(int x, int y, int move) {
        if (System.currentTimeMillis() - startTime > timeLimit) return false;
        if (move == n * n) return true;

        nodesExpanded++;

        for (int i = 0; i < 8; i++) {
            int nextX = x + MOVE_X[i];
            int nextY = y + MOVE_Y[i];
            if (isValidMove(nextX, nextY)) {
                board[nextX][nextY] = move;
                if (dfs(nextX, nextY, move + 1)) return true;
                board[nextX][nextY] = -1;
            }
        }
        return false;
    }

    private boolean dfsWithHeuristic(int x, int y, int move, String heuristic) {
        if (System.currentTimeMillis() - startTime > timeLimit) return false;
        if (move == n * n) return true;

        nodesExpanded++;

        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int nextX = x + MOVE_X[i];
            int nextY = y + MOVE_Y[i];
            if (isValidMove(nextX, nextY)) {
                int priority = countMoves(nextX, nextY);
                if (heuristic.equals("h2")) {
                    priority -= Math.min(nextX, nextY); // Prefer corners
                }
                moves.add(new int[]{nextX, nextY, priority});
            }
        }

        moves.sort(Comparator.comparingInt(a -> a[2]));

        for (int[] moveArr : moves) {
            int nextX = moveArr[0];
            int nextY = moveArr[1];
            board[nextX][nextY] = move;
            if (dfsWithHeuristic(nextX, nextY, move + 1, heuristic)) return true;
            board[nextX][nextY] = -1;
        }
        return false;
    }

    private int countMoves(int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int nextX = x + MOVE_X[i];
            int nextY = y + MOVE_Y[i];
            if (isValidMove(nextX, nextY)) count++;
        }
        return count;
    }

    public void printBoard() {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.printf("%2d ", cell);
            }
            System.out.println();
        }
    }

    public int getNodesExpanded() {
        return nodesExpanded;
    }
}

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter board size (n): ");
        int n = scanner.nextInt();
        System.out.print("Enter search method (1: BFS, 2: DFS, 3: DFS-h1b, 4: DFS-h2): ");
        int method = scanner.nextInt();
        System.out.print("Enter time limit (seconds): ");
        long timeLimit = scanner.nextLong();

        KnightTour kt = new KnightTour(n, timeLimit);
        boolean success = kt.solve(method, 0, 0);

        System.out.println(success ? "A solution found." : "No solution exists or Timeout.");
        kt.printBoard();
        System.out.println("Nodes expanded: " + kt.getNodesExpanded());
    }
}
