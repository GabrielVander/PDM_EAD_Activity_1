package com.example.pdmead1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdmead1.model.MatchState;
import com.example.pdmead1.model.PlayableTile;
import com.example.pdmead1.model.TileState;
import com.example.pdmead1.model.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    private TextView lblPlayerScore;
    private TextView lblAppScore;
    private List<PlayableTile> playableTiles;

    private Integer humanScore = 0;
    private Integer appScore = 0;

    private Turn turn = Turn.HUMAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playableTiles = new ArrayList<PlayableTile>() {
            {
                add(new PlayableTile(0, findViewById(R.id.playableTile0)));
                add(new PlayableTile(1, findViewById(R.id.playableTile1)));
                add(new PlayableTile(2, findViewById(R.id.playableTile2)));
                add(new PlayableTile(3, findViewById(R.id.playableTile3)));
                add(new PlayableTile(4, findViewById(R.id.playableTile4)));
                add(new PlayableTile(5, findViewById(R.id.playableTile5)));
                add(new PlayableTile(6, findViewById(R.id.playableTile6)));
                add(new PlayableTile(7, findViewById(R.id.playableTile7)));
                add(new PlayableTile(8, findViewById(R.id.playableTile8)));
            }
        };

        lblPlayerScore = findViewById(R.id.lblPlayerScore);
        lblAppScore = findViewById(R.id.lblAppScore);

        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnResetScore = findViewById(R.id.btnResetScore);

        playableTiles.forEach(tile -> tile.getTile().setOnClickListener(view -> playableTileClick(tile)));

        btnRestart.setOnClickListener(v -> restartMatch());
        btnResetScore.setOnClickListener(v -> resetScores());

    }

    private void playableTileClick(PlayableTile tile) {
        if (tile.getState().equals(TileState.EMPTY)) {
            tile.mark(R.drawable.x, TileState.HUMAN);
            endTurn();
        }
    }

    private void switchTurn() {
        turn = turn.equals(Turn.HUMAN) ? Turn.APP : Turn.HUMAN;
    }

    private void updateMatchState() {
        switch (getMatchState()) {
            case PLAYER_WON:
                humanScore++;
                finishMatch("The Player won");
                break;

            case APP_WON:
                appScore++;
                finishMatch("The App won");
                break;

            case DRAW:
                finishMatch("The game tied");
                break;

            case ONGOING:
                if(turn.equals(Turn.APP)) play();
        }
    }

    private MatchState getMatchState() {
        if(checkWin(TileState.HUMAN)) return MatchState.PLAYER_WON;
        else if(checkWin(TileState.APP)) return MatchState.APP_WON;
        else if(checkDraw()) return MatchState.DRAW;
        return MatchState.ONGOING;
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
        return checkSequence(tileState, Arrays.asList(0, 4, 8)) ||
                checkSequence(tileState, Arrays.asList(2, 4, 6));
    }

    private boolean checkColumns(TileState tileState) {
        return checkSequence(tileState, Arrays.asList(0, 3, 6)) ||
                checkSequence(tileState,Arrays.asList( 1, 4, 7)) ||
                checkSequence(tileState,Arrays.asList( 2, 5, 7));
    }

    private boolean checkRows(TileState state) {
        return checkSequence(state, Arrays.asList(0, 1, 2)) ||
                checkSequence(state, Arrays.asList(3, 4, 5)) ||
                checkSequence(state, Arrays.asList(6, 7, 8));
    }

    private boolean checkSequence(TileState state, List<Integer> sequence) {
        return sequence.stream().allMatch(index-> playableTiles.get(index).getState().equals(state));
    }

    private Boolean checkDraw() {
        return getEmptyTiles().isEmpty();
    }

    private void updateScores() {
        lblPlayerScore.setText(String.valueOf(humanScore));
        lblAppScore.setText(String.valueOf(appScore));
    }

    private void play() {
        getBestTile().mark(R.drawable.circle, TileState.APP);
        endTurn();
    }

    private PlayableTile getBestTile() {
        return getWinnableTiles(TileState.APP)
                .orElseGet(() -> getWinnableTiles(TileState.HUMAN)
                        .orElseGet(this::getPreferredTile));

    }

    private PlayableTile getPreferredTile() {
        final List<Integer> preferredIndexes = new ArrayList<Integer>(){{
            add(4);
            add(0);
            add(2);
            add(6);
            add(8);
            add(1);
            add(3);
            add(5);
            add(7);
        }};

        return getEmptyTiles()
                .stream()
                .filter(tile->preferredIndexes.contains(tile.getIndex()))
                .findFirst()
                .get();
    }

    private Optional<PlayableTile> getWinnableTiles(TileState state) {
        return getEmptyTiles()
                .stream()
                .filter(playableTile -> isWinnable(state, playableTile))
                .findAny();
    }

    private boolean isWinnable(TileState state, PlayableTile playableTile) {
        return isWinnableInSequence(state, getOtherTilesInSequence(playableTile, getTilesByRow(playableTile.getRow()))) ||
        isWinnableInSequence(state, getOtherTilesInSequence(playableTile, getTilesByCol(playableTile.getCol())));
    }

    private List<PlayableTile> getOtherTilesInSequence(PlayableTile playableTile, List<PlayableTile> sequence) {
        return sequence
                .stream()
                .filter(tile -> tile.getIndex() != playableTile.getIndex())
                .collect(Collectors.toList());
    }

    private boolean isWinnableInSequence(TileState state, List<PlayableTile> tilesByRow) {
        return checkSequence(state,
                tilesByRow
                    .stream()
                    .map(PlayableTile::getIndex)
                    .collect(Collectors.toList()));
    }

    private List<PlayableTile> getTilesByRow(int row) {
        return playableTiles.stream()
                .filter(tile -> tile.getRow() == row)
                .collect(Collectors.toList());
    }

    private List<PlayableTile> getTilesByCol(int col) {
        return playableTiles.stream()
                .filter(tile -> tile.getCol() == col)
                .collect(Collectors.toList());
    }

    private List<PlayableTile> getEmptyTiles() {
        return playableTiles.stream()
                .filter(tile -> tile.getState().equals(TileState.EMPTY))
                .collect(Collectors.toList());
    }

    private void endTurn() {
        switchTurn();
        updateMatchState();
    }

    private void resetScores() {
        humanScore = 0;
        appScore = 0;
        updateScores();
    }

    private void restartMatch() {
        playableTiles.forEach(playableTile-> {
            playableTile.mark(R.drawable.empty, TileState.EMPTY);
            playableTile.getTile().setOnClickListener(tile -> playableTileClick(playableTile));
        });
        turn = Turn.HUMAN;
    }

    private void stopListeners() {
        playableTiles.forEach(playableTile -> playableTile.getTile().setOnClickListener(null));
    }

}
