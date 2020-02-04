import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class ConcatenatedWords {
    SortedSet<String> sortedWordSet;
    Map<String, WordProperty> subWordSearch;
    private Set<String> setOfWords;
    private RadixTree<VoidValue> wordRadixTree;
    private int minWordsLength = 0;

    public ConcatenatedWords() {
        this.sortedWordSet = new TreeSet<String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1.length() == o2.length()) {
                    return 1;
                } else return (o2.length() - o1.length());
            }
        });
        this.minWordsLength = MAX_VALUE;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Yngwa\\Desktop\\TheConcatenatedWordsChallenge\\WordsContainer.txt";
        ConcatenatedWords longestConcatWord = new ConcatenatedWords();
        longestConcatWord.readWordsFromFile(filePath);
        longestConcatWord.getLongestConcatWordList();
    }

    private void readWordsFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            /*FileInputStream fileInputStream = new FileInputStream(filePath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            */
            String word;
            while ((word = scanner.next()) != null) {
                if (word.length() > 0) {
                    sortedWordSet.add(word);
                    if (word.length() < minWordsLength) {
                        minWordsLength = word.length();
                    }
                }
            }
            setOfWords = new HashSet<String>(sortedWordSet.size());
            setOfWords.addAll(sortedWordSet);
            wordRadixTree = new ConcurrentRadixTree<VoidValue>(
                    new DefaultCharSequenceNodeFactory());
            for (String entry : sortedWordSet) {
                wordRadixTree.put(entry, VoidValue.SINGLETON);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfSubWords(String word) {
        String prefixSubWord;
        String remainWord;
        Set<CharSequence> prefixSubWordSet;
        int subWordCount = 0, remainWordCount = 0;
        WordProperty wordProperty = null;
        System.out.println("Input word: " + word);
        if (subWordSearch.containsKey(word)) {
            return subWordSearch.get(word).getSubWordCount();
        }
        prefixSubWord = word.substring(0, this.minWordsLength);
        prefixSubWordSet = this.wordRadixTree
                .getKeysStartingWith(prefixSubWord);
        if (!prefixSubWordSet.isEmpty()) {
            for (CharSequence subWord : prefixSubWordSet) {
                if (subWord.length() <= word.length()
                        && subWord.equals(word.substring(0,
                        subWord.length()))) {
                    subWordCount = subWordCount + 1;
                    if (subWord.length() == word.length()) {
                        return 0;
                    }
                    remainWord = word.substring(subWord.length()
                    );
                    remainWordCount = this.getNumberOfSubWords(remainWord);
                    subWordCount = subWordCount + remainWordCount;
                    if (remainWordCount == 0) {
                        if (this.setOfWords.contains(remainWord)) {
                            subWordCount = subWordCount + 1;
                            wordProperty = new WordProperty();
                            wordProperty.setWord(word);
                            wordProperty.setSubWordCount(subWordCount);
                            List<String> subWordList = new ArrayList<String>();
                            subWordList.add(subWord.toString());
                            subWordList.add(remainWord);
                            wordProperty.setSubWordList(subWordList);
                            this.subWordSearch.put(word, wordProperty);
                            return subWordCount;
                        } else {
                            subWordCount = 0;
                        }
                    } else if (remainWordCount > 0) {
                        // Update sub-word lookup table
                        wordProperty = new WordProperty();
                        wordProperty.setWord(word);
                        wordProperty.setSubWordCount(subWordCount);
                        List<String> subWordList = new ArrayList<String>();
                        subWordList.add(subWord.toString());
                        subWordList.addAll(this.subWordSearch.get(
                                remainWord).getSubWordList());
                        wordProperty.setSubWordList(subWordList);
                        this.subWordSearch.put(word, wordProperty);
                        return subWordCount;
                    }
                }
            }
        }
        return 0;
    }

    public List<WordProperty> getLongestConcatWordList() {
        List<WordProperty> wordPropertyList = new ArrayList<WordProperty>();
        WordProperty wordProperty = null;
        int subWordCount;
        for (String word : this.sortedWordSet) {
            subWordCount = this.getNumberOfSubWords(word);
            if (subWordCount > 1) {
                wordProperty = this.subWordSearch.get(word);
                System.out.println("Word: " + word + "\tSub-Word Count: "
                        + subWordCount + "\tSub-Word List: "
                        + wordProperty.getSubWordList());
                wordPropertyList.add(wordProperty);
                break;
            }
        }
        return wordPropertyList;
    }
}

