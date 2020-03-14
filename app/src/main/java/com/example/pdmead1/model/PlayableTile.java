package com.example.pdmead1.model;

import android.widget.ImageView;

import com.example.pdmead1.R;

public class PlayableTile {
    private int index;
    private TileState state = TileState.EMPTY;
    private ImageView tile;

    public PlayableTile(int index, ImageView tile) {
        this.index = index;
        this.tile = tile;
    }

    public void mark(int imageId, TileState state){
        this.tile.setImageResource(imageId);
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TileState getState() {
        return state;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    public ImageView getTile() {
        return tile;
    }

    public void setTile(ImageView tile) {
        this.tile = tile;
    }

    public int getRow(){
        return index / 3;
    }

    public int getCol() {
        return index % 3;
    }
}
