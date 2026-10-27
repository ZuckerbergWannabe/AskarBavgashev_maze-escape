import java.util.*;

/** Iterative depth-first search in the maze built by Mission 1. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    private static final int[] DR = {-1, 0, 1, 0};
    private static final int[] DC = {0, 1, 0, -1};

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols(), exit = n - 1;
        int[][] graph = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : graph) if (neighbours.length == 1) deadEnds++;

        boolean[] seen = new boolean[n];
        int[] parent = new int[n]; Arrays.fill(parent, -1);
        int[] stackCell = new int[n], stackNext = new int[n];
        int top = 0, visited = 1;
        stackCell[0] = 0; stackNext[0] = 0; seen[0] = true;
        while (top >= 0 && !seen[exit]) {
            int u = stackCell[top];
            if (stackNext[top] >= 4) { top--; continue; }
            int direction = stackNext[top]++;
            int r = u / cols, c = u % cols;
            int nr = r + DR[direction], nc = c + DC[direction];
            if (nr < 0 || nr >= w.rows() || nc < 0 || nc >= cols) continue;
            int v = nr * cols + nc;
            if (seen[v] || !hasNeighbour(graph[u], v)) continue;
            seen[v] = true; visited++; parent[v] = u;
            top++; stackCell[top] = v; stackNext[top] = 0;
        }
        if (!seen[exit]) return new Result(new int[0], visited, deadEnds);
        int length = 1;
        for (int v = exit; v != 0; v = parent[v]) length++;
        int[] path = new int[length];
        int v = exit;
        for (int i = length - 1; i >= 0; i--) { path[i] = v; if (v != 0) v = parent[v]; }
        return new Result(path, visited, deadEnds);
    }

    private static boolean hasNeighbour(int[] neighbours, int target) {
        for (int v : neighbours) if (v == target) return true;
        return false;
    }
}
