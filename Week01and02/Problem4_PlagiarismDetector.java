import java.util.*;

public class Problem4_PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // HashMap<ngram, Set<documentId>> - which documents contain this n-gram
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // Store documents
    private HashMap<String, String> documents = new HashMap<>();

    // Index a document into the n-gram map
    public void indexDocument(String docId, String content) {
        documents.put(docId, content);
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = i; j < i + N; j++) {
                ngram.append(words[j]).append(" ");
            }
            String key = ngram.toString().trim();
            ngramIndex.putIfAbsent(key, new HashSet<>());
            ngramIndex.get(key).add(docId);
        }
        System.out.println("Indexed: " + docId + " (" + (words.length - N + 1) + " n-grams extracted)");
    }

    // Extract n-grams from a document string
    private Set<String> extractNgrams(String content) {
        Set<String> ngrams = new HashSet<>();
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = i; j < i + N; j++) {
                ngram.append(words[j]).append(" ");
            }
            ngrams.add(ngram.toString().trim());
        }
        return ngrams;
    }

    // Analyze a document for plagiarism against all indexed documents
    public void analyzeDocument(String docId) {
        if (!documents.containsKey(docId)) {
            System.out.println("Document not found: " + docId);
            return;
        }

        System.out.println("\n=== Analyzing: " + docId + " ===");
        Set<String> targetNgrams = extractNgrams(documents.get(docId));
        System.out.println("Extracted " + targetNgrams.size() + " n-grams.");

        // Count matches per document
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String ngram : targetNgrams) {
            if (ngramIndex.containsKey(ngram)) {
                for (String otherDoc : ngramIndex.get(ngram)) {
                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        if (matchCount.isEmpty()) {
            System.out.println("No similarities found. Document appears original.");
            return;
        }

        // Report similarities
        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / targetNgrams.size();
            String verdict = similarity > 50 ? "⚠ PLAGIARISM DETECTED" :
                             similarity > 20 ? "⚡ SUSPICIOUS" : "✓ OK";
            System.out.printf("  Found %d matching n-grams with \"%s\" -> Similarity: %.1f%% [%s]%n",
                              entry.getValue(), entry.getKey(), similarity, verdict);
        }
    }

    public static void main(String[] args) {
        Problem4_PlagiarismDetector detector = new Problem4_PlagiarismDetector();

        System.out.println("=== Problem 4: Plagiarism Detection System ===\n");

        String essay1 = "The quick brown fox jumps over the lazy dog and then runs through the forest near the river bank where birds sing every morning";
        String essay2 = "The quick brown fox jumps over the lazy dog and then runs through the forest where many animals live and hunt for food";
        String essay3 = "Artificial intelligence is transforming the world of technology at a rapid pace and changing how businesses operate globally";
        String essay4 = "The quick brown fox jumps over the lazy dog and then runs through the forest near the river bank where birds sing every morning today"; // near copy of essay1

        detector.indexDocument("essay_001.txt", essay1);
        detector.indexDocument("essay_002.txt", essay2);
        detector.indexDocument("essay_003.txt", essay3);
        detector.indexDocument("essay_004.txt", essay4);

        System.out.println("\n--- Running plagiarism checks ---");
        detector.analyzeDocument("essay_002.txt");
        detector.analyzeDocument("essay_003.txt");
        detector.analyzeDocument("essay_004.txt");
    }
}
