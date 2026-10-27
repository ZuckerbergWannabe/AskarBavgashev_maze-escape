/** Disjoint-set union with union by size and path compression. */
public class DisjointSet {
    private final int[] parent;
    private final int[] size;
    private int sets;

    public DisjointSet(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        parent = new int[n];
        size = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    /** The root of the set that contains x. */
    public int find(int x) {
        if (x < 0 || x >= parent.length) throw new IndexOutOfBoundsException("cell: " + x);
        int root = x;
        while (root != parent[root]) root = parent[root];
        while (x != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    /** Joins two sets; false means they were already connected. */
    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        size[ra] += size[rb];
        sets--;
        return true;
    }

    public boolean connected(int a, int b) { return find(a) == find(b); }
    public int count() { return sets; }
}
