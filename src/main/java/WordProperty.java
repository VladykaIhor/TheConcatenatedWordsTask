import java.util.List;

public class WordProperty {
    private String word;
    private int subWordCount;
    private List<String> subWordList;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getSubWordCount() {
        return subWordCount;
    }

    public void setSubWordCount(int subWordCount) {
        this.subWordCount = subWordCount;
    }

    public List<String> getSubWordList() {
        return subWordList;
    }

    public void setSubWordList(List<String> subWordList) {
        this.subWordList = subWordList;
    }
}
