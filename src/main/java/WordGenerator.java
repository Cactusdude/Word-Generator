import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * WordGenerator
 *
 * @author Evan Moseman (evan.moseman@corp.aol.com)
 * @version 1.0
 * @since 12/18/17
 */
public class WordGenerator {
    private final Set<String> wordList = new HashSet<>();

    private static final String[] VOWELS = {"a", "e", "i", "o", "u", "y"};

    public static final String WORD_LIST_URL   = "https://github.com/dwyl/english-words/blob/master/words_alpha.txt?raw=true";
    public static final String LOCAL_WORD_LIST = "words.txt";

    private static final HashSet<String> subWordSet  = new HashSet<>();
    private static final List<String>    subWordList = new ArrayList<>();

    private static final int LARGEST_WORD = 10;

    public static void main(String[] args)
        throws IOException {
        new WordGenerator().start();
    }

    public WordGenerator() {
    }

    public void start()
        throws IOException {
        loadWords();
        parse();

        createNewWords();
    }

    SecureRandom sr = new SecureRandom();

    private void createNewWords() {

        for (int i = 0; i < 100; i++) {
            int wordSize = sr.nextInt(LARGEST_WORD) + 1;

            StringBuffer sb = new StringBuffer();

            for (int j = 0; j < wordSize; j++) {
                if (sr.nextFloat() < 0.33) {
                    sb.append(randomVowel());
                }
                else {
                    sb.append(randomSubWord());
                }
            }

            if (wordList.contains(sb.toString())) {
                System.out.println("Found real word: " + sb);
            }

            System.out.println("New word: " + sb);
        }
    }

    private String randomVowel() {
        return VOWELS[sr.nextInt(VOWELS.length)];
    }

    private String randomSubWord() {
        return subWordList.get(sr.nextInt(subWordList.size()));
    }

    private void parse() {
        wordList.forEach(w -> {
            String[] parts = w.split("[aeiouy]");
            Arrays.stream(parts).filter(s -> !s.isEmpty()).filter(s -> s.matches("[a-z]")).forEach(s -> {
                if (subWordSet.add(s)) {
                    subWordList.add(s);
                }
            });

//            String splitParts = Arrays.stream(parts).collect(Collectors.joining(" "));
//            System.out.println(" Parts: " + splitParts);

        });

        System.out.println("Created " + subWordSet.size() + " sub words!");
    }

    private void loadWords()
        throws IOException {

        Path localWordListPath = FileSystems.getDefault().getPath(LOCAL_WORD_LIST);
        if (!Files.exists(localWordListPath)) {
            URL url = new URL(WORD_LIST_URL);

            try (Scanner scanner = new Scanner(url.openConnection().getInputStream())) {
                while (scanner.hasNextLine()) {
                    wordList.add(scanner.nextLine());
                }
            }

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(LOCAL_WORD_LIST))) {
                wordList.forEach(w -> {
                    try {
                        writer.write(w);
                        writer.append('\n');
                    }
                    catch (IOException e) {
                    }
                });
            }
        }
        else {
            File file = new File(LOCAL_WORD_LIST);

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    wordList.add(scanner.nextLine());
                }
            }
        }
        System.out.println("Loaded " + wordList.size() + " words!");
    }
}
