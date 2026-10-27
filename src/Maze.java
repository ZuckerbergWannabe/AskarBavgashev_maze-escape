import java.util.*;

/** Algorithms for building a maze and finding a minimum spanning tree. */
public class Maze {
    /** Break walls in input-file order, keeping only edges that join separate components. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k = 0; k < w.count() && count < chosen.length; k++)
            if (dsu.union(w.a()[k], w.b()[k])) chosen[count++] = k;
        return Arrays.copyOf(chosen, count);
    }

    /** Kruskal's minimum spanning tree. */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(k -> w.cost()[k]));
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                chosen[count++] = k;
                if (count == chosen.length) break;
            }
        }
        return Arrays.copyOf(chosen, count);
    }

    private record Edge(int to, int cost) {}

    /** Prim's minimum spanning tree cost, using a priority queue. */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n <= 1) return 0;
        @SuppressWarnings("unchecked") List<Edge>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i], b = w.b()[i], c = w.cost()[i];
            graph[a].add(new Edge(b, c)); graph[b].add(new Edge(a, c));
        }
        record Candidate(int cell, int cost) {}
        PriorityQueue<Candidate> pq = new PriorityQueue<>(Comparator.comparingInt(Candidate::cost));
        boolean[] used = new boolean[n];
        pq.add(new Candidate(0, 0));
        long total = 0;
        int visited = 0;
        while (!pq.isEmpty() && visited < n) {
            Candidate e = pq.poll();
            if (used[e.cell()]) continue;
            used[e.cell()] = true; visited++; total += e.cost();
            for (Edge next : graph[e.cell()]) if (!used[next.to()]) pq.add(new Candidate(next.to(), next.cost()));
        }
        if (visited != n) throw new IllegalArgumentException("The maze graph is disconnected");
        return total;
    }
}
