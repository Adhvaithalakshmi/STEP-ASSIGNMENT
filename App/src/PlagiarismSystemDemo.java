import java.util.*;

class PlagiarismDetector {

    // n-gram size
    private int N = 5;

    // n-gram -> documents mapping
    private Map<String, Set<String>> ngramIndex = new HashMap<>();

    // document -> its ngrams
    private Map<String, List<String>> documentNgrams = new HashMap<>();

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex
                    .computeIfAbsent(gram, k -> new HashSet<>())
                    .add(docId);
        }
    }

    // Analyze a new document
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = ngramIndex.get(gram);

            if (docs != null) {
                for (String d : docs) {
                    matchCount.put(d, matchCount.getOrDefault(d, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = ((double) matches / ngrams.size()) * 100;

            System.out.println(
                    "Found " + matches + " matching n-grams with \"" +
                            otherDoc + "\""
            );

            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 60)
                System.out.println(" (PLAGIARISM DETECTED)");
            else if (similarity > 10)
                System.out.println(" (Suspicious)");
            else
                System.out.println();
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        List<String> grams = new ArrayList<>();

        String[] words = text
                .toLowerCase()
                .replaceAll("[^a-z ]", "")
                .split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }
}

public class PlagiarismSystemDemo {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // Existing essays in database
        String essay1 = "Artificial intelligence is transforming the world with machine learning algorithms";
        String essay2 = "Machine learning algorithms are transforming artificial intelligence applications";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        // New submission
        String newEssay =
                "Artificial intelligence is transforming the world using machine learning algorithms";

        System.out.println("Analyzing essay_123.txt");
        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}