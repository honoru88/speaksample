package hy.kr.speaksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import hy.kr.speaksample.obj.TextObj;


public class MainActivity extends Activity implements RecognitionListener, CompoundButton.OnCheckedChangeListener {

    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";

    //private String EXAM_STR = "want you see all those animals you should fell better";
    private String EXAM_STR = "show me the money";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutSet();
        speechSet();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    /**
     * 결과값
     *
     * @param results
     */
    @Override
    public void onResults(Bundle results) {

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";

        for (String result : matches) //text에 남기기
            text += result + "\n";

        returnedText.setText(text);

        if (strToArrayCompare(EXAM_STR, matches)) {//리스트중 같은 문장이 있나
            Toast.makeText(this, "100점", Toast.LENGTH_LONG).show();
        } else {//문장이 없다면 비교
            ArrayList<TextObj> listTexObj = getListTextObj(matches);
            setScoreTextObj(listTexObj);// EXAM_STR를 기준으로 점수 넣기
            Toast.makeText(this, bestScore(listTexObj) + "점", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }


    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }


    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }


    private void layoutSet() {
        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

        progressBar.setVisibility(View.INVISIBLE);
        toggleButton.setOnCheckedChangeListener(this);

    }

    private void speechSet() {

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 8000);
        recognizerIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        recognizerIntent.putExtra("android.speech.extra.GET_AUDIO", true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.toggleButton1:

                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    speech.startListening(recognizerIntent);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();

                }
                break;
        }
    }

    /**
     * 문자열 - 배열[문자열] 일치한게 있는지 판단
     *
     * @param inputString
     * @param items
     * @return
     */
    private boolean strToArrayCompare(String inputString, ArrayList<String> items) {
        for (int i = 0; i < items.size(); i++) {
            if (inputString.contains(items.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 문자열 - 배열[문자열] 일치한게 있는지 판단
     *
     * @param inputString
     * @param items
     * @return
     */
    private boolean strToArrayCompare(String inputString, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (inputString.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 배열[문자열]->배열[TextObj]
     *
     * @param list
     * @return
     */
    private ArrayList<TextObj> getListTextObj(ArrayList<String> list) {
        ArrayList<TextObj> listText = new ArrayList<>();

        for (String result : list) {
            TextObj obj = new TextObj(result);
            listText.add(obj);
        }

        return listText;
    }

    /**
     * 예제와 배열[TextObj] 텍스트 비교해서 배열[TextObj] score 에 값 넣기
     *
     * @param listTexObj
     */
    private void setScoreTextObj(ArrayList<TextObj> listTexObj) {
        String[] examArray = EXAM_STR.split(" ");
        for (TextObj result : listTexObj) {//점수넣기
            int score = compareWords(examArray, result.getStrWords());
            result.setScore(score);//단어비교&점수넣기
            Log.i("score", "점수:" + score);
        }
    }

    /**
     * 단어 비교 ex) [I] [am] [a] [boy] =?[I] [am] [a] [girl]
     *
     * @param examArray 예문 문자열 배열
     * @param textArray 말한거 문자열 배열
     * @return (맞는문장수/전체문장수)*100
     */
    private int compareWords(String[] examArray, String[] textArray) {

        int count = 0;
        for (int i = 0; i < examArray.length; i++) {//예제 기준으로
            if (strToArrayCompare(examArray[i], textArray)) {//예문과 말한거 문자열 비교
                count++;
            }
        }
        if (count == 0) {
            return 0;
        } else {

            double result = ((double) count / (double) examArray.length) * 100;
            return (int) result;
        }

    }

    /**
     * 알파벳 비교 ex) [I] [a][m] [a] [b][o][y] =?[I] [a][m] [a] [g][i][r][l]
     *
     * @param examArray 예문 문자열 배열
     * @param textArray 말한거 문자열 배열
     * @param score     점수
     */
    private void compareAlphabet(String[] examArray, String[] textArray, int score) {
    }

    private int bestScore(ArrayList<TextObj> testObjArray) {

        int maxScore = 0;//점수
        int maxCount = 0;
        for (int i = 0; i < testObjArray.size(); i++) {
            if (maxScore < testObjArray.get(i).getScore()) {
                maxScore = testObjArray.get(i).getScore();
                maxCount = i;
            }
        }

        return testObjArray.get(maxCount).getScore();
    }


}