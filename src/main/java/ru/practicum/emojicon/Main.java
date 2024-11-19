package ru.practicum.emojicon;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.emojicon.engine.Engine;
import ru.practicum.emojicon.model.EmojiCat;
import ru.practicum.emojicon.model.EmojiWorld;
import ru.practicum.emojicon.ui.EmojiHelp;
import ru.practicum.emojicon.ui.EmojiTicker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        EmojiWorld world = new EmojiWorld();
        UUID objectId = world.addObject(new EmojiCat("Tom"), world.getFreeArea().getCorners().get(0));
        world.setSelection(objectId);
        Engine engine = new Engine();
        engine.addRoot(world);
        engine.addRoot(new EmojiTicker());
        engine.addRoot(new EmojiHelp(engine, world));
        engine.run();
    }

    public static void demo(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new DefaultTerminalFactory(System.out, System.in, StandardCharsets.UTF_8).createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        TerminalSize size = screen.getTerminalSize();
        screen.startScreen();
        log.info("screen started {}x{}", size.getColumns(), size.getRows());
        KeyStroke key;
        List<Emoji> emojiList = new ArrayList<>(EmojiManager.getAll());
        int emojiCode = 0;
        do {
            screen.clear();
            int row = 1;
            int col = 0;
            Emoji emoji = emojiList.get(emojiCode);
            try {
                TextCharacter[] chars = TextCharacter.fromString(emoji.getUnicode());
                screen.setCharacter(0, 0,TextCharacter.fromString(Integer.toString(chars.length))[0]);
                for(int c = 0; c < chars.length; c++){
                    screen.setCharacter(col, row, chars[0].withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT).withForegroundColor(TextColor.ANSI.BLACK));
                    col += c;
                }
                log.info("emoji {}", emoji.getAliases());
            } catch (IllegalArgumentException e){
                log.error("illegal symbol {}", emoji.getAliases());
            }
            Thread.sleep(25);
            screen.refresh();
            key = screen.pollInput();
            emojiCode++;
        } while ((key == null || !key.getKeyType().equals(KeyType.Escape)) && emojiCode < emojiList.size());
        screen.stopScreen();
        terminal.close();
    }
}

/* found emoji with cats
0 = {Emoji@3510} "Emoji{description='smiling cat face with open mouth', supportsFitzpatrick=false, aliases=[smiley_cat], tags=[], unicode='😺', htmlDec='&#128570;', htmlHex='&#x1f63a;'}"
1 = {Emoji@3511} "Emoji{description='grinning cat face with smiling eyes', supportsFitzpatrick=false, aliases=[smile_cat], tags=[], unicode='😸', htmlDec='&#128568;', htmlHex='&#x1f638;'}"
2 = {Emoji@3512} "Emoji{description='smiling cat face with heart-shaped eyes', supportsFitzpatrick=false, aliases=[heart_eyes_cat], tags=[], unicode='😻', htmlDec='&#128571;', htmlHex='&#x1f63b;'}"
3 = {Emoji@3513} "Emoji{description='kissing cat face with closed eyes', supportsFitzpatrick=false, aliases=[kissing_cat], tags=[], unicode='😽', htmlDec='&#128573;', htmlHex='&#x1f63d;'}"
4 = {Emoji@3514} "Emoji{description='cat face with wry smile', supportsFitzpatrick=false, aliases=[smirk_cat], tags=[], unicode='😼', htmlDec='&#128572;', htmlHex='&#x1f63c;'}"
5 = {Emoji@3515} "Emoji{description='weary cat face', supportsFitzpatrick=false, aliases=[scream_cat], tags=[horror], unicode='🙀', htmlDec='&#128576;', htmlHex='&#x1f640;'}"
6 = {Emoji@3516} "Emoji{description='crying cat face', supportsFitzpatrick=false, aliases=[crying_cat_face], tags=[sad, tear], unicode='😿', htmlDec='&#128575;', htmlHex='&#x1f63f;'}"
7 = {Emoji@3517} "Emoji{description='cat face with tears of joy', supportsFitzpatrick=false, aliases=[joy_cat], tags=[], unicode='😹', htmlDec='&#128569;', htmlHex='&#x1f639;'}"
8 = {Emoji@3518} "Emoji{description='pouting cat face', supportsFitzpatrick=false, aliases=[pouting_cat], tags=[], unicode='😾', htmlDec='&#128574;', htmlHex='&#x1f63e;'}"
9 = {Emoji@3594} "Emoji{description='cat face', supportsFitzpatrick=false, aliases=[cat], tags=[pet], unicode='🐱', htmlDec='&#128049;', htmlHex='&#x1f431;'}"
10 = {Emoji@3595} "Emoji{description='cat', supportsFitzpatrick=false, aliases=[cat2], tags=[], unicode='🐈', htmlDec='&#128008;', htmlHex='&#x1f408;'}"
 */
