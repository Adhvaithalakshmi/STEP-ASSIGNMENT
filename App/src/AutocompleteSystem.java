import java.util.*;

public class AutocompleteSystem {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
        String query = null;
    }

    // Global frequency table
    static Map<String, Integer> frequencyMap = new HashMap<>();

    static TrieNode root = new TrieNode();

    // Insert query into Trie
    public static void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        node.query = query;

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Search suggestions for prefix
    public static List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();
            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, results);

        // Top 10 suggestions by frequency
        PriorityQueue<String> pq =
                new PriorityQueue<>(
                        (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
                );

        for (String q : results) {
            pq.offer(q);
            if (pq.size() > 10)
                pq.poll();
        }

        List<String> suggestions = new ArrayList<>();

        while (!pq.isEmpty())
            suggestions.add(pq.poll());

        Collections.reverse(suggestions);

        return suggestions;
    }

    // DFS traversal to collect queries
    private static void dfs(TrieNode node, List<String> results) {

        if (node.isEnd)
            results.add(node.query);

        for (TrieNode child : node.children.values()) {
            dfs(child, results);
        }
    }

    // Update frequency for searched query
    public static void updateFrequency(String query) {
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Display suggestions
    public static void showSuggestions(String prefix) {

        List<String> suggestions = search(prefix);

        System.out.println("\nSuggestions for \"" + prefix + "\"");

        int rank = 1;

        for (String s : suggestions) {
            System.out.println(
                    rank++ + ". " + s +
                            " (" + frequencyMap.get(s) + " searches)"
            );
        }
    }

    public static void main(String[] args) {

        // sample queries
        insert("java tutorial");
        insert("javascript");
        insert("java download");
        insert("java 21 features");
        insert("java tutorial");
        insert("java tutorial");
        insert("java download");
        insert("java interview questions");
        insert("java streams");
        insert("java collections");

        showSuggestions("jav");

        // simulate trending query
        updateFrequency("java 21 features");
        updateFrequency("java 21 features");
        updateFrequency("java 21 features");

        showSuggestions("java");
    }
}