package hy.kr.speaksample.obj;

/**
 * Created by lim2621 on 2015-06-17.
 */
public class TextObj {
    private int Score = 0; //점수
    private String[] strWords = null;  //문장을 배열로


    /**
     * 문자열->배열[문자열]
     *
     * @param result
     */
    public TextObj(String result) {
        strWords = result.split(" ");
    }

    public String[] getStrWords() {
        return strWords;
    }

    public void setStrWords(String[] strWords) {
        this.strWords = strWords;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
}
