import java.util.*;

public class PlagiarismDetectionSystem {

    // n-gram size
    static int N = 5;

    // n-gram -> set of document IDs
    HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // store documents
    HashMap<String, String> documents = new HashMap<>();


    // Add document to database
    public void addDocument(String docId, String text) {

        documents.put(docId, text);

        List<String> ngrams = generateNGrams(text);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }


    // Generate n-grams
    private List<String> generateNGrams(String text) {

        List<String> grams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }


    // Analyze new document
    public void analyzeDocument(String docId, String text) {

        List<String> grams = generateNGrams(text);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {

            if (ngramIndex.containsKey(gram)) {

                for (String doc : ngramIndex.get(gram)) {

                    matchCount.put(doc, matchCount.getOrDefault(doc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String doc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches + " matching n-grams with \"" + doc + "\"");
            System.out.println("Similarity: " + similarity + "%");

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }


    public static void main(String[] args) {

        PlagiarismDetectionSystem system = new PlagiarismDetectionSystem();

        system.addDocument("essay_089.txt",
                "Artificial intelligence is transforming the world through automation and machine learning");

        system.addDocument("essay_092.txt",
                "Artificial intelligence is transforming the world through automation and machine learning in modern technology");

        system.analyzeDocument("essay_123.txt",
                "Artificial intelligence is transforming the world through automation and machine learning");
    }
}
