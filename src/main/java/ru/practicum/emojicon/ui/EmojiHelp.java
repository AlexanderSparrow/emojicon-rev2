package ru.practicum.emojicon.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import ru.practicum.emojicon.engine.Drawable;
import ru.practicum.emojicon.engine.Engine;
import ru.practicum.emojicon.engine.Frame;
import ru.practicum.emojicon.engine.RootFrame;
import ru.practicum.emojicon.model.EmojiCat;
import ru.practicum.emojicon.model.EmojiWorld;
import ru.practicum.emojicon.model.EmojiWorldObject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmojiHelp implements Drawable {


    private final Engine engine;
    private final EmojiWorld world;

    public EmojiHelp(Engine engine, EmojiWorld world) {
        this.engine = engine;
        this.world = world;
    }

    @Override
    public void drawFrame(Frame someFrame) {
        RootFrame frame = RootFrame.extend(someFrame);
        TextGraphics text = frame.getScreen().newTextGraphics();
        text.setBackgroundColor(TextColor.ANSI.BLACK);
        text.setForegroundColor(TextColor.ANSI.WHITE);
        String arrows = Stream.of(EmojiManager.getForAlias("arrow_left"),
                        EmojiManager.getForAlias("arrow_right"),
                        EmojiManager.getForAlias("arrow_up"),
                        EmojiManager.getForAlias("arrow_down"))
                .map(Emoji::getUnicode)
                .collect(Collectors.joining(""));
        text.putString(0, frame.getBottom(), " " + arrows + " [Ходить]");
        String escape = " Esc [Выход] ";
        text.putString(frame.getRight() - escape.length() + 1, frame.getBottom(), escape);

        List<UUID> selection = world.getSelection();
        if (selection.size() == 1) {
            world.findEntity(selection.get(0)).filter(entity -> entity instanceof EmojiWorldObject).map(entity -> (EmojiWorldObject) entity).filter(obj -> obj.getInner() instanceof EmojiCat).ifPresent(catObj -> {
                String pos = catObj.getX() + ":" + catObj.getY();
                String posText = " [" + pos + "] ";
                text.putString(frame.getRight() / 2 - posText.length() / 2, frame.getBottom(), posText);
            });
        }
    }
}
