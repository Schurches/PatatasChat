package com.example.steven.patataschat.Entities;

/**
 * Created by steven on 12/01/2018.
 */

public class IEmotionButton {

    private String IEmotion_title;
    private int IEmotion_color;
    private int IEmotion_icon;
    private boolean IEmotion_text_color_white;

    public IEmotionButton(){

    }

    public IEmotionButton(String IEmotion_title, int IEmotion_color, int IEmotion_icon, boolean isWhite) {
        this.IEmotion_title = IEmotion_title;
        this.IEmotion_color = IEmotion_color;
        this.IEmotion_icon = IEmotion_icon;
        this.IEmotion_text_color_white = isWhite;
    }

    public boolean isIEmotion_text_color_white() {
        return IEmotion_text_color_white;
    }

    public void setIEmotion_text_color_white(boolean IEmotion_text_color_white) {
        this.IEmotion_text_color_white = IEmotion_text_color_white;
    }

    public String getIEmotion_title() {
        return IEmotion_title;
    }

    public void setIEmotion_title(String IEmotion_title) {
        this.IEmotion_title = IEmotion_title;
    }

    public int getIEmotion_color() {
        return IEmotion_color;
    }

    public void setIEmotion_color(int IEmotion_color) {
        this.IEmotion_color = IEmotion_color;
    }

    public int getIEmotion_icon() {
        return IEmotion_icon;
    }

    public void setIEmotion_icon(int IEmotion_icon) {
        this.IEmotion_icon = IEmotion_icon;
    }
}
