package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by baesubin on 2018-11-07.
 */

// This Activity is for Hangman Game with Pepper
public class GameResultActivity extends AppCompatActivity {
    private final String TAG = "GameResultActivity";
    private Context context = null;
    private ImageView resultImage = null;
    private String victory = "";
    private MediaPlayer soundForTieTheGame = null;
    private MediaPlayer soundForWinTheGame = null;
    private MediaPlayer soundForLoseTheGame = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_result);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        victory = bundle.getString("victory");

        soundForTieTheGame = MediaPlayer.create(getApplicationContext(), R.raw.end_game);
        soundForWinTheGame = MediaPlayer.create(getApplicationContext(), R.raw.winnerbell);
        soundForLoseTheGame = MediaPlayer.create(getApplicationContext(), R.raw.loserbell);


        resultImage = (ImageView) findViewById(R.id.result_image);

        switch (victory) {
            case "2": // win
                resultImage.setImageDrawable(context.getDrawable(R.drawable.happy_android));
                soundForWinTheGame.start();
                break;
            case "1": // tie
                resultImage.setImageDrawable(context.getDrawable(R.drawable.tied_android));
                soundForTieTheGame.start();
                break;
            case "0": //lose
                resultImage.setImageDrawable(context.getDrawable(R.drawable.sad_android));
                soundForLoseTheGame.start();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(soundForWinTheGame.isPlaying())
            soundForWinTheGame.stop();
        if(soundForTieTheGame.isPlaying())
            soundForWinTheGame.stop();
        if(soundForLoseTheGame.isPlaying())
            soundForWinTheGame.stop();
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameResultActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_want_to_finish_the_game));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameResultActivity.this, MenuActivity.class);
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
