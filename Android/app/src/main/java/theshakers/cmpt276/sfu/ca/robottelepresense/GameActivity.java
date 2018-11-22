package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by baesubin on 2018-11-07.
 */

// This Activity is for Hangman Game with Pepper
public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "GameActivity";
    private Context context = null;
    private ImageView hangImage = null;
    private TextView answerText = null;
    private TextView hintText = null;
    private TextView resultText = null;
    private Button aBtn = null, bBtn = null, cBtn = null, dBtn = null, eBtn = null, fBtn = null, gBtn = null, hBtn = null, iBtn = null,
        jBtn = null, kBtn = null, lBtn = null, mBtn = null, nBtn = null, oBtn = null, pBtn = null, qBtn = null, rBtn = null, sBtn = null,
        tBtn = null, uBtn = null, vBtn = null, wBtn = null, xBtn = null, yBtn = null, zBtn = null;
    private int countForWrong = 0;
    private int countForCorrect = 0;
    private String hintStr = "";
    private String answerStr = "";
    private String count = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        context = this;

        hangImage = (ImageView) findViewById(R.id.hang_img);
        answerText = (TextView) findViewById(R.id.answer_text);
        hintText = (TextView) findViewById(R.id.hint_text);
        resultText = (TextView) findViewById(R.id.result_text);

        aBtn = (Button) findViewById(R.id.btnA);
        bBtn = (Button) findViewById(R.id.btnB);
        cBtn = (Button) findViewById(R.id.btnC);
        dBtn = (Button) findViewById(R.id.btnD);
        eBtn = (Button) findViewById(R.id.btnE);
        fBtn = (Button) findViewById(R.id.btnF);
        gBtn = (Button) findViewById(R.id.btnG);
        hBtn = (Button) findViewById(R.id.btnH);
        iBtn = (Button) findViewById(R.id.btnI);
        jBtn = (Button) findViewById(R.id.btnJ);
        kBtn = (Button) findViewById(R.id.btnK);
        lBtn = (Button) findViewById(R.id.btnL);
        mBtn = (Button) findViewById(R.id.btnM);
        nBtn = (Button) findViewById(R.id.btnN);
        oBtn = (Button) findViewById(R.id.btnO);
        pBtn = (Button) findViewById(R.id.btnP);
        qBtn = (Button) findViewById(R.id.btnQ);
        rBtn = (Button) findViewById(R.id.btnR);
        sBtn = (Button) findViewById(R.id.btnS);
        tBtn = (Button) findViewById(R.id.btnT);
        uBtn = (Button) findViewById(R.id.btnU);
        vBtn = (Button) findViewById(R.id.btnV);
        wBtn = (Button) findViewById(R.id.btnW);
        xBtn = (Button) findViewById(R.id.btnX);
        yBtn = (Button) findViewById(R.id.btnY);
        zBtn = (Button) findViewById(R.id.btnZ);

        aBtn.setOnClickListener(this);
        bBtn.setOnClickListener(this);
        cBtn.setOnClickListener(this);
        dBtn.setOnClickListener(this);
        eBtn.setOnClickListener(this);
        fBtn.setOnClickListener(this);
        gBtn.setOnClickListener(this);
        hBtn.setOnClickListener(this);
        iBtn.setOnClickListener(this);
        jBtn.setOnClickListener(this);
        kBtn.setOnClickListener(this);
        lBtn.setOnClickListener(this);
        mBtn.setOnClickListener(this);
        nBtn.setOnClickListener(this);
        oBtn.setOnClickListener(this);
        pBtn.setOnClickListener(this);
        qBtn.setOnClickListener(this);
        rBtn.setOnClickListener(this);
        sBtn.setOnClickListener(this);
        tBtn.setOnClickListener(this);
        uBtn.setOnClickListener(this);
        vBtn.setOnClickListener(this);
        wBtn.setOnClickListener(this);
        xBtn.setOnClickListener(this);
        yBtn.setOnClickListener(this);
        zBtn.setOnClickListener(this);

        hintStr = "Animal";
        answerStr = "RABBIT";

        initGame();

    }


    private void initGame() {
        countForWrong = 0;
        countForCorrect = 0;
        changeHangMan();
        hintText.setText(hintStr);
        count = "";
        for(int i=0; i<answerStr.length(); i++)
            count += "_ ";
        answerText.setText(count);
    }

    private void changeHangMan() {
        switch (countForWrong) {
            case 0:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang1));
                break;
            case 1:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang2));
                break;
            case 2:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang3));
                break;
            case 3:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang4));
                break;
            case 4:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang5));
                break;
            case 5:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang6));
                break;
            case 6:
                hangImage.setImageDrawable(getDrawable(R.drawable.hang7));
                lostTheGame();
                break;
        }
    }

    private void checkResult(char character) {
        boolean isCorrectCharacter = false;

        for(int i=0; i<answerStr.length(); i++) {
            if(answerStr.charAt(i) == character) {
                isCorrectCharacter = true;
                char[] chars = count.toCharArray();
                chars[2*i] = character;
                count = String.valueOf(chars);
                answerText.setText(count);
                countForCorrect++;
            }
        }

        if(countForCorrect==answerStr.length())
            wonTheGame();

        if(!isCorrectCharacter) {
            countForWrong++;
            changeHangMan();
        }
    }
    private void wonTheGame() {
        disableAllButtons();
        resultText.setText("You are a winner");
    }

    private void lostTheGame() {
        disableAllButtons();
        resultText.setText("You are a loser");
    }

    private void disableAllButtons() {
        aBtn.setEnabled(false);
        bBtn.setEnabled(false);
        cBtn.setEnabled(false);
        dBtn.setEnabled(false);
        eBtn.setEnabled(false);
        fBtn.setEnabled(false);
        gBtn.setEnabled(false);
        hBtn.setEnabled(false);
        iBtn.setEnabled(false);
        jBtn.setEnabled(false);
        kBtn.setEnabled(false);
        lBtn.setEnabled(false);
        mBtn.setEnabled(false);
        nBtn.setEnabled(false);
        oBtn.setEnabled(false);
        pBtn.setEnabled(false);
        qBtn.setEnabled(false);
        rBtn.setEnabled(false);
        sBtn.setEnabled(false);
        tBtn.setEnabled(false);
        uBtn.setEnabled(false);
        vBtn.setEnabled(false);
        wBtn.setEnabled(false);
        xBtn.setEnabled(false);
        yBtn.setEnabled(false);
        zBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnA:
                checkResult('A');
                aBtn.setEnabled(false);
                break;
            case R.id.btnB:
                checkResult('B');
                bBtn.setEnabled(false);
                break;
            case R.id.btnC:
                checkResult('C');
                cBtn.setEnabled(false);
                break;
            case R.id.btnD:
                checkResult('D');
                dBtn.setEnabled(false);
                break;
            case R.id.btnE:
                checkResult('E');
                eBtn.setEnabled(false);
                break;
            case R.id.btnF:
                checkResult('F');
                fBtn.setEnabled(false);
                break;
            case R.id.btnG:
                checkResult('G');
                gBtn.setEnabled(false);
                break;
            case R.id.btnH:
                checkResult('H');
                hBtn.setEnabled(false);
                break;
            case R.id.btnI:
                checkResult('I');
                iBtn.setEnabled(false);
                break;
            case R.id.btnJ:
                checkResult('J');
                jBtn.setEnabled(false);
                break;
            case R.id.btnK:
                checkResult('K');
                kBtn.setEnabled(false);
                break;
            case R.id.btnL:
                checkResult('L');
                lBtn.setEnabled(false);
                break;
            case R.id.btnM:
                checkResult('M');
                mBtn.setEnabled(false);
                break;
            case R.id.btnN:
                checkResult('N');
                nBtn.setEnabled(false);
                break;
            case R.id.btnO:
                checkResult('O');
                oBtn.setEnabled(false);
                break;
            case R.id.btnP:
                checkResult('P');
                pBtn.setEnabled(false);
                break;
            case R.id.btnQ:
                checkResult('Q');
                qBtn.setEnabled(false);
                break;
            case R.id.btnR:
                checkResult('R');
                rBtn.setEnabled(false);
                break;
            case R.id.btnS:
                checkResult('S');
                sBtn.setEnabled(false);
                break;
            case R.id.btnT:
                checkResult('T');
                tBtn.setEnabled(false);
                break;
            case R.id.btnU:
                checkResult('U');
                uBtn.setEnabled(false);
                break;
            case R.id.btnV:
                checkResult('V');
                vBtn.setEnabled(false);
                break;
            case R.id.btnW:
                checkResult('W');
                wBtn.setEnabled(false);
                break;
            case R.id.btnX:
                checkResult('X');
                xBtn.setEnabled(false);
                break;
            case R.id.btnY:
                checkResult('Y');
                yBtn.setEnabled(false);
                break;
            case R.id.btnZ:
                checkResult('Z');
                zBtn.setEnabled(false);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_want_to_go_back));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
