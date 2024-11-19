package ru.practicum.emojicon.model;

import ru.practicum.emojicon.engine.*;

import java.util.UUID;

public class EmojiWorldObject implements Boxed, Drawable, Controllable, Entity {

    private final EmojiObjectHolder parent;
    private final UUID id;
    private final EmojiObject inner;

    private int x;
    private int y;
    private int z; //order

    public EmojiWorldObject(EmojiObjectHolder parent, EmojiObject obj, Point position) {
        this.parent = parent;
        this.inner = obj;
        this.x = position.getX();
        this.y = position.getY();
        if(!parent.isFreeArea(getLeft(), getTop(), getRight(), getBottom()))
            throw new IllegalArgumentException();
        this.z = 0;
        this.id = UUID.randomUUID();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public void drawFrame(Frame frame) {
        inner.drawFrame(frame);
    }

    @Override
    public boolean move(Point step) {
        int nextLeft = x + step.getX();
        int nextTop = y + step.getY();
        int nextRight = nextLeft + inner.getWidth();
        int nextBottom = nextTop + inner.getHeight();
        if(parent.isFreeArea(nextLeft, nextTop, nextRight, nextBottom)){
            x = nextLeft;
            y = nextTop;
            return true;
        } else {
            return false;
        }
    }

    public int getWidth() {
        return inner.getWidth();
    }

    public int getHeight() {
        return inner.getHeight();
    }

    public int getBottom() {
        return getY() + getHeight() - 1;
    }

    public int getTop() {
        return getY();
    }

    public int getRight() {
        return getX() + getWidth() - 1;
    }

    public int getLeft() {
        return getX();
    }

    public EmojiObject getInner() {
        return inner;
    }
}
