import java.util.*;

/** Strongly connected components and shortest routes in their condensation DAG. */
public class Zones {
    /** Kosaraju's algorithm with iterative DFS in both passes. */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s), in = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] order = new int[n]; int orderSize = 0;
        int[] stackCell = new int[n], stackNext = new int[n];
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0; stackCell[0] = start; stackNext[0] = 0; seen[start] = true;
            while (top >= 0) {
                int u = stackCell[top];
                if (stackNext[top] < out[u].length) {
                    int edge = out[u][stackNext[top]++]; int v = s.to()[edge];
                    if (!seen[v]) { seen[v] = true; top++; stackCell[top] = v; stackNext[top] = 0; }
                } else { order[orderSize++] = u; top--; }
            }
        }
        int[] zone = new int[n]; Arrays.fill(zone, -1);
        int zoneCount = 0;
        int[] stack = new int[n];
        for (int oi = orderSize - 1; oi >= 0; oi--) {
            int start = order[oi];
            if (zone[start] != -1) continue;
            int top = 0; stack[top] = start; zone[start] = zoneCount;
            while (top >= 0) {
                int u = stack[top--];
                for (int edge : in[u]) {
                    int v = s.from()[edge];
                    if (zone[v] == -1) { zone[v] = zoneCount; stack[++top] = v; }
                }
            }
            zoneCount++;
        }
        return zone;
    }

    /** Returns longest chain length in zones and cheapest route cost from start zone to exit zone. */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        @SuppressWarnings("unchecked") Map<Integer, Integer>[] cheapest = new Map[zoneCount];
        for (int z = 0; z < zoneCount; z++) cheapest[z] = new HashMap<>();
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a == b) continue;
            cheapest[a].merge(b, s.time()[i], Math::min);
        }
        int[] longest = new int[zoneCount]; Arrays.fill(longest, 1);
        long inf = Long.MAX_VALUE / 4;
        long[] distance = new long[zoneCount]; Arrays.fill(distance, inf);
        int start = zoneOf[0], target = zoneOf[s.n() - 1];
        distance[start] = 0;
        // Kosaraju's component numbering is a topological order: all cross-zone edges go forward.
        for (int a = 0; a < zoneCount; a++) {
            for (Map.Entry<Integer, Integer> edge : cheapest[a].entrySet()) {
                int b = edge.getKey();
                longest[b] = Math.max(longest[b], longest[a] + 1);
                if (distance[a] != inf) distance[b] = Math.min(distance[b], distance[a] + edge.getValue());
            }
        }
        int stages = 0;
        for (int value : longest) stages = Math.max(stages, value);
        return new long[]{stages, distance[target] == inf ? -1 : distance[target]};
    }
}
