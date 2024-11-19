package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.vdurmont.emoji.Emoji;

import java.util.function.BiFunction;

//корневой фрейм работает с устройством, всегда рисует от 0 до края консоли
public class RootFrame extends AbstractFrame {

    Screen screen;
    private BiFunction<Integer, Integer, TextColor> transparentColorFn;

    public RootFrame(Screen screen, int right, int bottom) {
        super(0, 0, right, bottom);
        this.screen = screen;
    }

    public static RootFrame extend(Frame frame) {
        return (RootFrame) frame.getRoot();
    }

    @Override
    public TextColor getTransparentColor() {
        return transparentColorFn != null ? transparentColorFn.apply(getPosX(), getPosY()) : TextColor.ANSI.BLACK;
    }

    //set position for painting
    public void setPosition(int x, int y) {
        if (x < 0 || x > getRight() || y < 0 || y > getBottom())
            throw new IllegalArgumentException("position out of bounds");

        setPosX(x);
        setPosY(y);
    }



    //paint it with background color
    public Point paint(){
        screen.setCharacter(getPosX(), getPosY(), TextCharacter.DEFAULT_CHARACTER.withCharacter(' ').withBackgroundColor(getRealFillColor()));
        return new Point(1, 1);
    }

    //draw single character or
    public Point draw(Character character){
        screen.setCharacter(getPosX(), getPosY(), TextCharacter.DEFAULT_CHARACTER.withCharacter(character).withForegroundColor(getColor()).withBackgroundColor(getRealFillColor()));
        return new Point(1, 1);
    }

    //draw emoji
    //some emoji take more than one symbol
    public Point draw(Emoji emoji){
        TextCharacter[] chars = TextCharacter.fromString(emoji.getUnicode());
        for(int c = 0; c < chars.length; c++){
            screen.setCharacter(getPosX(), getPosY(), chars[c].withForegroundColor(getColor()).withBackgroundColor(getRealFillColor()));
        }
        return new Point(chars.length, 1);
    }

    @Override
    public Frame getRoot() {
        return this;
    }

    public void setTransparentColorFn(BiFunction<Integer, Integer, TextColor> transparentColorFn) {
        this.transparentColorFn = transparentColorFn;
    }
    public Screen getScreen() {
        return screen;
    }
}
