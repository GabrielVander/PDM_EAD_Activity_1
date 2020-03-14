package com.example.pdmead1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdmead1.model.MatchState;
import com.example.pdmead1.model.TileState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    private TextView lblPlayerScore;
    private TextView lblAppScore;
    private List<ImageView> playableTiles;

    List<TileState> board = new ArrayList<TileState>() {
        {
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
            add(TileState.EMPTY);
        }
    };

    private Integer playerScore = 0;
    private Integer appScore = 0;

    private Boolean isPlayersTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playableTiles = new ArrayList<ImageView>() {
            {
                add(findViewById( R.id.playableTile0));
                add(findViewById( R.id.playableTile1 ));
                add(findViewById( R.id.playableTile2));
                add(findViewById( R.id.playableTile3 ));
                add(findViewById( R.id.playableTile4));
                add(findViewById( R.id.playableTile5));
                add(findViewById( R.id.playableTile6));
                add(findViewById( R.id.playableTile7));
                add(findViewById( R.id.playableTile8));
            }
        };
        lblPlayerScore = findViewById(R.id.lblPlayerScore);
        lblAppScore = findViewById(R.id.lblAppScore);

        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnResetScore = findViewById(R.id.btnResetScore);

        playableTiles.forEach(tile -> tile.setOnClickListener(view -> playableTileClick((ImageView) view)));

        btnRestart.setOnClickListener(v -> restartMatch());
        btnResetScore.setOnClickListener(v -> resetScores());

    }

    private void playableTileClick(ImageView tile) {
        int tileIndex = playableTiles.indexOf(tile);

        if (board.get(tileIndex).equals(TileState.EMPTY)) {
            tile.setImageResource(R.drawable.x);
            board.set(tileIndex, TileState.PLAYER);

            endTurn();
        }
    }

    private void switchTurn() {
        isPlayersTurn = !isPlayersTurn;
    }

    private void updateMatchState() {
        switch (getMatchState()) {
            case PLAYER_WON:
                playerScore++;
                finishMatch("The Player won");
                break;

            case APP_WON:
                appScore++;
                finishMatch("The App won");
                break;

            case DRAW:
                finishMatch("The game tied");
                break;

            case APP_TURN:
                play();
        }
    }

    private MatchState getMatchState() {
        if(checkWin(TileState.PLAYER)) return MatchState.PLAYER_WON;
        else if(checkWin(TileState.APP)) return MatchState.APP_WON;
        else if(checkDraw()) return MatchState.DRAW;
        return isPlayersTurn ? MatchState.PLAYER_TURN : MatchState.APP_TURN;
    }

    private void finishMatch(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        updateScores();
        stopListeners();
    }

    private Boolean checkWin(TileState tileState) {
        return checkRows(tileState) ||
        checkColumns(tileState) ||
        checkDiagonals(tileState);
    }

    private boolean checkDiagonals(TileState tileState) {
        return checkSequence(tileState, 0, 4, 8) ||
                checkSequence(tileState, 2, 4, 6);
    }

    private boolean checkColumns(TileState tileState) {
        return checkSequence(tileState, 0, 3, 6) ||
                checkSequence(tileState, 1, 4, 7) ||
                checkSequence(tileState, 2, 5, 7);
    }

    private boolean checkRows(TileState tileState) {
        return checkSequence(tileState, 0, 1, 2) ||
                checkSequence(tileState, 3, 4, 5) ||
                checkSequence(tileState, 6, 7, 8);
    }

    private boolean checkSequence(TileState tileState, int firstIndex, int secondIndex, int thirdIndex) {
        return board.get(firstIndex) == tileState &&
                board.get(secondIndex) == tileState &&
                board.get(thirdIndex) == tileState;
    }

    private Boolean checkDraw() {
        for (TileState tileState : board) {
            if(tileState == TileState.EMPTY) return false;
        }
        return true;
    }

    private void updateScores() {
        lblPlayerScore.setText(String.valueOf(playerScore));
        lblAppScore.setText(String.valueOf(appScore));
    }

    private void play() {
        int randomIndex = new Random().nextInt(9);

        while (!board.get(randomIndex).equals(TileState.EMPTY)) randomIndex = new Random().nextInt(9);

        playableTiles.get(randomIndex).setImageResource(R.drawable.circle);
        board.set(randomIndex, TileState.APP);

        endTurn();
    }

    private void endTurn() {
        switchTurn();
        updateMatchState();
    }

    private void resetScores() {
        playerScore = 0;
        appScore = 0;
        updateScores();
    }

    private void restartMatch() {
        playableTiles.forEach(playableTile-> {
            playableTile.setImageResource(R.drawable.empty);
            playableTile.setOnClickListener(tile -> playableTileClick((ImageView) tile));
        });
        Collections.fill(board, TileState.EMPTY);
        isPlayersTurn = true;
    }

    private void stopListeners() {
        playableTiles.forEach(playableTile -> playableTile.setOnClickListener(null));
    }

}
