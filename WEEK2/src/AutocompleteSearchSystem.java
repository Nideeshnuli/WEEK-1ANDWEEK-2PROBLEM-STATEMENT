import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSearchSystem {

    TrieNode root = new TrieNode();

    // query -> frequency
    HashMap<String, Integer> queryFrequency = new HashMap<>();


    // Insert query into trie
    public void insertQuery(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;

        queryFrequency.put(query,
                queryFrequency.getOrDefault(query, 0) + 1);
    }


    // Search prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return Collections.emptyList();
            }

            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, prefix, results);

        // Get top 10 suggestions
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        queryFrequency.get(a) - queryFrequency.get(b));

        for (String q : results) {

            pq.add(q);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> top = new ArrayList<>(pq);
        top.sort((a, b) ->
                queryFrequency.get(b) - queryFrequency.get(a));

        return top;
    }


    // DFS to collect queries
    private void dfs(TrieNode node, String current,
                     List<String> results) {

        if (node.isEnd) {
            results.add(current);
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), current + c, results);
        }
    }


    // Update search frequency
    public void updateFrequency(String query) {

        queryFrequency.put(query,
                queryFrequency.getOrDefault(query, 0) + 1);
    }


    // Simple typo suggestion
    public List<String> suggestCorrection(String word) {

        List<String> suggestions = new ArrayList<>();

        for (String query : queryFrequency.keySet()) {

            if (query.startsWith(word.substring(0, 1))) {
                suggestions.add(query);
            }

            if (suggestions.size() == 5)
                break;
        }

        return suggestions;
    }


    public static void main(String[] args) {

        AutocompleteSearchSystem system =
                new AutocompleteSearchSystem();

        system.insertQuery("java tutorial");
        system.insertQuery("javascript");
        system.insertQuery("java download");
        system.insertQuery("java tutorial");
        system.insertQuery("java 21 features");
        system.insertQuery("java tutorial");

        System.out.println("Search suggestions for 'jav':");

        List<String> results = system.search("jav");

        int rank = 1;

        for (String r : results) {

            System.out.println(rank + ". " + r
                    + " (" + system.queryFrequency.get(r)
                    + " searches)");

            rank++;
        }

        system.updateFrequency("java 21 features");

        System.out.println("\nUpdated frequency of 'java 21 features': "
                + system.queryFrequency.get("java 21 features"));
    }
}
