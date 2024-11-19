package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Engine implements Runnable, EntityResolver {

    //время одного кадра при частоте в 60 FPS
    private static final Long FRAME_TIME = 1000L / 60;
    private final Terminal terminal;
    private final Screen screen;
    private final List<Drawable> roots = new ArrayList<>();
    private final Camera camera;

    private Instant timestamp;

    public Engine() {
        try {
            this.terminal = new DefaultTerminalFactory(System.out, System.in, StandardCharsets.UTF_8).createTerminal();
            this.screen = new TerminalScreen(terminal);
            this.timestamp = Instant.now();
            Point rb = getTerminalSize();
            this.camera = new Camera(this, 0, 0, rb.getX(), rb.getY());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Point getTerminalSize() {
        try {
            return new Point(terminal.getTerminalSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            screen.startScreen();
            roots.stream().filter(root -> root instanceof Controller).map(root -> (Controller) root).filter(ctrl -> !ctrl.getSelection().isEmpty()).forEach(ctrl -> camera.showSelection(ctrl));
            KeyStroke key;
            do {
                key = screen.pollInput();
                screen.clear();
                screen.doResizeIfNecessary();
                Point size = getTerminalSize();
                screen.setCursorPosition(new TerminalPosition(size.getX(), 0));
                camera.setRightBottom(size.dec(1, 1));
                Frame rootFrame = camera.getFrame(screen);
                roots.forEach(root -> root.drawFrame(rootFrame));
                Optional.ofNullable(key).ifPresent(lastKey -> {
                    roots.stream().filter(root -> root instanceof Controller).map(root -> (Controller) root).forEach(ctrl -> {
                        ctrl.handleKey(lastKey);
                        camera.handleSelection(ctrl);
                    });
                });
                screen.refresh(Screen.RefreshType.DELTA);
                long dt = Instant.now().toEpochMilli() - timestamp.toEpochMilli();
                timestamp = Instant.now();
                handleEngineKey(key);
                Thread.sleep(Math.max(0, FRAME_TIME - dt)); //сколько-то времени ушло на кадр
            } while (key == null || !(key.getKeyType().equals(KeyType.Escape)));
            screen.stopScreen();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEngineKey(KeyStroke key) throws IOException {
        if (key == null)
            return;

        if (key.getKeyType().equals(KeyType.Character) && key.getCharacter().equals(' ')) {
            terminal.bell();
        }
    }

    public void addRoot(Drawable root) {
        this.roots.add(root);
    }

    @Override
    public Optional<? extends Entity> findEntity(UUID uuid) {
        return roots.stream().filter(root -> root instanceof EntityResolver).map(root -> (EntityResolver) root).flatMap(root -> root.findEntity(uuid).stream()).findFirst();
    }
}
