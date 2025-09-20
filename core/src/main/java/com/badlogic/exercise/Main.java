package com.badlogic.exercise;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music; // Import for background music

import java.util.Iterator;

public class Main implements ApplicationListener, GameLevel {
    // Game state variables
    private enum GameState {
        MAIN_MENU,
        PLAYING,
        PAUSED,
        GAME_OVER,
        CUTSCENE,
        DIALOG, // This state is for the existing dialogShow
        INTRO_DIALOG, // New state for DialogShowBegining
        PUZZLE
    }
    private GameState currentState = GameState.MAIN_MENU;


    // Textures
    Texture heart1, heart2, heart3;
    Texture pauseButtonTexture;
    Texture playAgainButtonTexture;
    Texture exitButtonTexture;
    private Texture coinIconTexture;
    private Texture mainMenuBackgroundTexture;
    private Texture playButtonTexture; // Texture for the main menu play button

    // Core components
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Display display;
    Character character;
    GoldBoxManager goldBoxManager;

    // Game variables
    float backgroundScrollX = 0, tileScrollX = 0;
    int lives = 3;
    private int coinScore;
    float invincibilityTime = 0;

    // Fonts
    BitmapFont scoreFont;
    BitmapFont uiFont;

    // Puzzle components
    private Puzzle puzzleGame;
    private Texture puzzleBackgroundFrameTexture;
    private Texture puzzleDraggableItemTexture;
    private static final float PUZZEL_WORLD_WIDTH = 600;
    private static final float PUZZEL_WORLD_HEIGHT = 800;
    private OrthographicCamera puzzleCamera;
    private Viewport puzzleViewport;
    private ShapeRenderer puzzleShapeRenderer;
    private SpriteBatch puzzleSpriteBatch;
    private boolean puzzleCompleted = false;


    // Dialog and Cutscenes
    private DialogShowBegining dialogShowBeginning; // New field for the intro dialog
    private DialogShow dialogShow; // Existing dialog, likely for end-level or other in-game dialogs
    private FlyCutsceneScreen flyCutsceneScreen;
    private EndLevelDialog endLevelDialog;

    // Screens
    private PlayAgainScreen playAgainScreen;

    // UI Elements
    private Rectangle playButtonBounds;
    private Rectangle exitButtonBounds; // Bounds for the new exit button

    private Music gameThemeMusic; // Field for the overall game theme music


    @Override
    public void create() {
        // Load all textures
        heart1 = new Texture("1hearts.png");
        heart2 = new Texture("2hearts.png");
        heart3 = new Texture("3hearts.png");
        playAgainButtonTexture = new Texture("PlayAgainButton.png");
        pauseButtonTexture = new Texture("pause.png");
        exitButtonTexture = new Texture("Exit1.png");
        coinIconTexture = new Texture("coin.png");
        mainMenuBackgroundTexture = new Texture("Background6.png"); // Main menu background
        playButtonTexture = new Texture("PlayButton.png"); // Main menu play button

        try {
            puzzleBackgroundFrameTexture = new Texture(Gdx.files.internal("FRame.png"));
        } catch (Exception e) {
            Gdx.app.error("Main", "Error loading puzzle background texture: FRame.png", e);
            Pixmap pixmap = new Pixmap((int)PUZZEL_WORLD_WIDTH, (int) PUZZEL_WORLD_HEIGHT, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.LIGHT_GRAY);
            pixmap.fill();
            puzzleBackgroundFrameTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        try {
            puzzleDraggableItemTexture = new Texture(Gdx.files.internal("HelloWorldButton.png"));
        } catch (Exception e) {
            Gdx.app.error("Main", "Error loading puzzle draggable item texture: HelloWorldButton.png", e);
            Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.ORANGE);
            pixmap.fill();
            puzzleDraggableItemTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        // Initialize core components
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5); // Assuming 8 units wide, 5 units tall world

        // Define bounds for the main menu buttons
        float buttonWidth = 1.0f;
        float buttonHeight = 1.0f;
        float buttonSpacing = 0.2f;

        // --- MODIFICATION START ---
        // Adjust play button position to be significantly lower
        playButtonBounds = new Rectangle(
            viewport.getWorldWidth() / 2 - buttonWidth / 2,
            viewport.getWorldHeight() / 2 - 1.0f, // Moved lower by 1.0f units (was 0.5f)
            buttonWidth,
            buttonHeight
        );

        // Increase exit button size and adjust its position relative to the play button
        float exitButtonWidth = 1.2f; // Keep increased size
        float exitButtonHeight = 1.2f; // Keep increased size
        exitButtonBounds = new Rectangle(
            viewport.getWorldWidth() / 2 - exitButtonWidth / 2, // Center the larger button
            playButtonBounds.y - exitButtonHeight - buttonSpacing, // Positioned below the play button with new size consideration
            exitButtonWidth,
            exitButtonHeight
        );
        // --- MODIFICATION END ---


        // Initialize fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("mario-font.ttf"));

        FreeTypeFontParameter scoreParam = new FreeTypeFontParameter();
        scoreParam.size = 25;
        scoreParam.color = Color.GREEN;
        scoreParam.borderColor = Color.WHITE;
        scoreParam.borderWidth = 0.5f;
        scoreParam.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        scoreFont = generator.generateFont(scoreParam);
        scoreFont.getData().setScale(0.023f);

        FreeTypeFontParameter uiParam = new FreeTypeFontParameter();
        uiParam.size = 30;
        uiParam.color = Color.GREEN;
        uiParam.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.:,!?()[]'\"%";
        uiFont = generator.generateFont(uiParam);
        uiFont.getData().setScale(0.03f);

        generator.dispose();

        // Initialize puzzle components
        puzzleCamera = new OrthographicCamera();
        puzzleViewport = new FitViewport(PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT, puzzleCamera);
        puzzleShapeRenderer = new ShapeRenderer();
        puzzleSpriteBatch = new SpriteBatch();

        // Initialize game objects
        display = new Display();
        character = new Character();
        goldBoxManager = new GoldBoxManager();

        // Initialize dialogs and screens
        dialogShowBeginning = new DialogShowBegining(puzzleSpriteBatch, PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT); // Initialize the new intro dialog
        dialogShow = new DialogShow(puzzleSpriteBatch, PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT);
        endLevelDialog = new EndLevelDialog(puzzleSpriteBatch, PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT);
        puzzleGame = new Puzzle(PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT, puzzleDraggableItemTexture, puzzleBackgroundFrameTexture);
        puzzleGame.initializeRenderers(puzzleSpriteBatch, puzzleShapeRenderer);
        playAgainScreen = new PlayAgainScreen(spriteBatch, viewport, uiFont, playAgainButtonTexture, exitButtonTexture);

        // Load the overall game theme music
        try {
            gameThemeMusic = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
            gameThemeMusic.setLooping(true);
            gameThemeMusic.setVolume(0.15f);
        } catch (Exception e) {
            Gdx.app.error("Main", "Error loading theme.mp3", e);
            gameThemeMusic = null;
        }

        // Set up initial game state and input processor
        resetGameState();
        Gdx.input.setInputProcessor(new GameInputProcessor());
    }

    private void resetGameState() {
        lives = 3;
        coinScore = 0;
        invincibilityTime = 0;
        backgroundScrollX = 0;
        tileScrollX = 0;
        puzzleCompleted = false;

        character.reset();
        display.reset();
        goldBoxManager.reset(display.getTrees());
        puzzleGame.resetGame();
        dialogShow.resetSequenceFinished();
        dialogShowBeginning.resetSequenceFinished();

        if (flyCutsceneScreen != null) {
            flyCutsceneScreen.dispose();
            flyCutsceneScreen = null;
        }

        playAgainScreen.resetTrigger();

        if (gameThemeMusic != null && !gameThemeMusic.isPlaying()) {
            gameThemeMusic.play();
        }

        if (currentState != GameState.MAIN_MENU) {
            currentState = GameState.PLAYING;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
            puzzleViewport.update(width, height, true);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        switch (currentState) {
            case MAIN_MENU:
                renderMainMenu();
                if (gameThemeMusic != null && gameThemeMusic.isPlaying()) {
                    gameThemeMusic.stop();
                }
                break;
            case INTRO_DIALOG:
                renderIntroDialog();
                break;
            case PLAYING:
                logic();
                draw();
                if (gameThemeMusic != null && !gameThemeMusic.isPlaying()) {
                    gameThemeMusic.play();
                }
                break;
            case PAUSED:
                draw();
                break;
            case GAME_OVER:
                renderGameOver();
                if (gameThemeMusic != null && gameThemeMusic.isPlaying()) {
                    gameThemeMusic.stop();
                }
                break;
            case CUTSCENE:
                renderCutscene();
                break;
            case DIALOG:
                renderDialog();
                break;
            case PUZZLE:
                renderPuzzle();
                break;
        }
    }

    private void renderMainMenu() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(mainMenuBackgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        spriteBatch.draw(playButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        spriteBatch.draw(exitButtonTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);

        spriteBatch.end();
    }

    private void renderGameOver() {
        playAgainScreen.render();
        if (playAgainScreen.isPlayAgainTriggered()) {
            resetGameState();
        } else if (playAgainScreen.isExitGameTriggered()) {
            Gdx.app.exit();
        }
    }

    private void renderIntroDialog() {
        puzzleViewport.apply();
        puzzleSpriteBatch.setProjectionMatrix(puzzleViewport.getCamera().combined);

        dialogShowBeginning.updateDialogSequence(Gdx.graphics.getDeltaTime());
        dialogShowBeginning.drawDialogSequence();

        if (dialogShowBeginning.isSequenceFinished()) {
            currentState = GameState.PLAYING;
            dialogShowBeginning.resetSequenceFinished();
            Gdx.app.log("Main", "Intro dialog sequence finished, transitioning to Playing state.");
        }
    }

    private void renderCutscene() {
        if (flyCutsceneScreen == null) {
            flyCutsceneScreen = new FlyCutsceneScreen(this);
        }
        flyCutsceneScreen.render(Gdx.graphics.getDeltaTime());
        if (flyCutsceneScreen.isFinished()) {
            flyCutsceneScreen.dispose();
            flyCutsceneScreen = null;
            dialogShow.triggerDialogSequence();
            currentState = GameState.DIALOG;
        }
    }

    private void renderDialog() {
        puzzleViewport.apply();
        puzzleSpriteBatch.setProjectionMatrix(puzzleViewport.getCamera().combined);

        dialogShow.updateDialogSequence(Gdx.graphics.getDeltaTime());
        dialogShow.drawDialogSequence();

        if (dialogShow.isSequenceFinished()) {
            if (!puzzleCompleted) {
                currentState = GameState.PUZZLE;
                dialogShow.resetSequenceFinished();
                character.velocity.set(0, 0);
                Gdx.app.log("Main", "Dialog sequence finished, transitioning to Puzzle Game.");
            }
        }
    }

    private void renderPuzzle() {
        if (endLevelDialog.isActive()) {
            endLevelDialog.update(Gdx.graphics.getDeltaTime());
            endLevelDialog.render();

            if (endLevelDialog.isFinished()) {
                if (endLevelDialog.isContinueClicked()) {
                    puzzleCompleted = true;
                    endLevelDialog.reset();
                    currentState = GameState.PLAYING;
                    Gdx.app.log("Main", "Continue button clicked. Returning to gameplay.");
                } else if (endLevelDialog.isExitClicked()) {
                    Gdx.app.exit();
                }
            }
            return;
        }

        puzzleViewport.apply();
        puzzleCamera.update();
        puzzleShapeRenderer.setProjectionMatrix(puzzleCamera.combined);
        puzzleSpriteBatch.setProjectionMatrix(puzzleCamera.combined);

        puzzleSpriteBatch.begin();
        puzzleSpriteBatch.draw(puzzleBackgroundFrameTexture, 0, 0, PUZZEL_WORLD_WIDTH, PUZZEL_WORLD_HEIGHT);
        puzzleSpriteBatch.end();

        puzzleGame.update(Gdx.graphics.getDeltaTime());
        puzzleGame.render();

        if (!puzzleCompleted && puzzleGame.isCompleted()) {
            puzzleCompleted = true;
            endLevelDialog.trigger();
        }
    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        if (invincibilityTime > 0) invincibilityTime -= delta;

        display.update(tileScrollX);
        character.handleInput();
        character.update(delta, this);
        goldBoxManager.update(tileScrollX);

        for (Display.Tree t : display.getTrees()) {
            t.bounds.set(t.position.x - tileScrollX + 0.05f, t.position.y + 0.05f, 0.4f, 0.4f);
            if (invincibilityTime <= 0 && character.bounds.overlaps(t.bounds)) {
                lives--;
                invincibilityTime = 1.5f;
                if (lives <= 0) {
                    currentState = GameState.GAME_OVER;
                    character.velocity.set(0, 0);
                }
                break;
            }
        }

        for (Display.Coin coin : display.getCoins()) {
            if (!coin.isCollected()) {
                Rectangle coinBounds = coin.getBounds();
                Rectangle coinScreenBounds = new Rectangle(
                    coinBounds.x - tileScrollX,
                    coinBounds.y,
                    coinBounds.width,
                    coinBounds.height
                );

                if (character.bounds.overlaps(coinScreenBounds)) {
                    coin.collect();
                    coinScore++;
                }
            }
        }

        Iterator<GoldBoxManager.GoldBox> iterator = goldBoxManager.getGoldBoxes().iterator();
        while (iterator.hasNext()) {
            GoldBoxManager.GoldBox box = iterator.next();
            if (character.bounds.overlaps(box.bounds)) {
                Gdx.app.log("Main", "Gold box collected! Showing fly cutscene.");
                currentState = GameState.CUTSCENE;
                iterator.remove();
                character.velocity.set(0, 0);
                break;
            }
        }
    }

    private void draw() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        display.draw(spriteBatch, viewport, backgroundScrollX, tileScrollX, null);
        goldBoxManager.draw(spriteBatch, tileScrollX);
        character.draw(spriteBatch, invincibilityTime);

        float ww = viewport.getWorldWidth(), wh = viewport.getWorldHeight();

        // --- Top-right corner UI layout ---
        float uiRightX = ww - 1.2f;
        float heartY = wh - 0.7f;
        float pauseY = heartY - 0.6f;
        float coinY = pauseY - 0.6f;

        // Draw heart (based on lives)
        Texture currentHeart = null;
        if (lives == 3) currentHeart = heart1;
        else if (lives == 2) currentHeart = heart2;
        else if (lives == 1) currentHeart = heart3;
        if (currentHeart != null) spriteBatch.draw(currentHeart, uiRightX, heartY, 0.5f, 0.5f);

        // Pause button
        spriteBatch.draw(pauseButtonTexture, uiRightX, pauseY, 0.5f, 0.5f);

        // Coin icon (score text is removed)
        spriteBatch.draw(coinIconTexture, uiRightX, coinY, 0.5f, 0.5f);

        if (currentState == GameState.PAUSED) {
            uiFont.draw(spriteBatch, "Paused", ww / 2 - 0.5f, wh / 2);
        }

        spriteBatch.end();
    }

    private void handlePauseButtonTouch(Vector2 pos) {
        float uiRightX = viewport.getWorldWidth() - 1.2f;
        float pauseY = viewport.getWorldHeight() - 0.7f - 0.6f;
        Rectangle bounds = new Rectangle(uiRightX, pauseY, 0.5f, 0.5f);
        if (bounds.contains(pos)) {
            if (currentState == GameState.PLAYING) {
                currentState = GameState.PAUSED;
                if (gameThemeMusic != null && gameThemeMusic.isPlaying()) {
                    gameThemeMusic.pause();
                }
            } else if (currentState == GameState.PAUSED) {
                currentState = GameState.PLAYING;
                if (gameThemeMusic != null && !gameThemeMusic.isPlaying()) {
                    gameThemeMusic.play();
                }
            }
        }
    }

    @Override
    public void pause() {
        if (gameThemeMusic != null && gameThemeMusic.isPlaying()) {
            gameThemeMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (currentState == GameState.PLAYING && gameThemeMusic != null && !gameThemeMusic.isPlaying()) {
            gameThemeMusic.play();
        }
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (scoreFont != null) scoreFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (puzzleSpriteBatch != null) puzzleSpriteBatch.dispose();
        if (puzzleShapeRenderer != null) puzzleShapeRenderer.dispose();
        if (heart1 != null) heart1.dispose();
        if (heart2 != null) heart2.dispose();
        if (heart3 != null) heart3.dispose();
        if (pauseButtonTexture != null) pauseButtonTexture.dispose();
        if (playAgainButtonTexture != null) playAgainButtonTexture.dispose();
        if (exitButtonTexture != null) exitButtonTexture.dispose();
        if (coinIconTexture != null) coinIconTexture.dispose();
        if (puzzleBackgroundFrameTexture != null) puzzleBackgroundFrameTexture.dispose();
        if (puzzleDraggableItemTexture != null) puzzleDraggableItemTexture.dispose();
        if (mainMenuBackgroundTexture != null) mainMenuBackgroundTexture.dispose();
        if (playButtonTexture != null) playButtonTexture.dispose();
        if (display != null) display.dispose();
        if (character != null) character.dispose();
        if (goldBoxManager != null) goldBoxManager.dispose();
        if (dialogShowBeginning != null) dialogShowBeginning.dispose();
        if (dialogShow != null) dialogShow.dispose();
        if (endLevelDialog != null) endLevelDialog.dispose();
        if (flyCutsceneScreen != null) flyCutsceneScreen.dispose();
        if (playAgainScreen != null) playAgainScreen.dispose();
        if (gameThemeMusic != null) gameThemeMusic.dispose();
    }

    // --- GameLevel Interface Methods ---
    @Override
    public float getTileScrollX() { return tileScrollX; }
    @Override
    public void setTileScrollX(float value) { tileScrollX = value; }

    @Override
    public float getBackgroundScrollX() { return backgroundScrollX; }
    @Override
    public void setBackgroundScrollX(float value) { backgroundScrollX = value; }

    private class GameInputProcessor extends InputAdapter {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (currentState == GameState.MAIN_MENU) {
                Vector2 touchPos = new Vector2(screenX, screenY);
                viewport.unproject(touchPos);

                if (playButtonBounds.contains(touchPos)) {
                    currentState = GameState.INTRO_DIALOG;
                    dialogShowBeginning.triggerDialogSequence();
                    if (gameThemeMusic != null) {
                        gameThemeMusic.play();
                    }
                    Gdx.app.log("Main", "Play button pressed, transitioning to Intro Dialog.");
                } else if (exitButtonBounds.contains(touchPos)) {
                    Gdx.app.exit();
                }
                return true;
            }

            if (currentState == GameState.INTRO_DIALOG) {
                return dialogShowBeginning.handleDialogInput(true, false);
            }

            if (currentState == GameState.DIALOG) {
                return dialogShow.handleDialogInput(true, false);
            }

            if (currentState == GameState.PUZZLE) {
                if (endLevelDialog.isActive()) {
                    Vector3 worldCoords = puzzleCamera.unproject(new Vector3(screenX, screenY, 0));
                    Vector2 touchPos = new Vector2(worldCoords.x, worldCoords.y);
                    if (endLevelDialog.handleInput(true, false, touchPos)) {
                        return true;
                    }
                }
                Vector3 worldCoords = puzzleCamera.unproject(new Vector3(screenX, screenY, 0));
                return puzzleGame.handleTouchDown(worldCoords.x, worldCoords.y);
            }

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (currentState == GameState.GAME_OVER) {
                return playAgainScreen.handleTouchUp(screenX, screenY);
            }

            if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
                Vector2 pos = new Vector2(screenX, screenY);
                viewport.unproject(pos);
                handlePauseButtonTouch(pos);
                return true;
            }

            if (currentState == GameState.PUZZLE) {
                if (endLevelDialog.isActive()) {
                    Vector3 worldCoords = puzzleCamera.unproject(new Vector3(screenX, screenY, 0));
                    Vector2 touchPos = new Vector2(worldCoords.x, worldCoords.y);
                    if (endLevelDialog.handleInput(true, false, touchPos)) {
                        return true;
                    }
                }
                Vector3 worldCoords = puzzleCamera.unproject(new Vector3(screenX, screenY, 0));
                return puzzleGame.handleTouchUp(worldCoords.x, worldCoords.y);
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (currentState == GameState.PUZZLE) {
                Vector3 worldCoords = puzzleCamera.unproject(new Vector3(screenX, screenY, 0));
                return puzzleGame.handleTouchDragged(worldCoords.x, worldCoords.y);
            }
            return false;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (currentState == GameState.GAME_OVER) {
                return playAgainScreen.handleKeyDown(keycode);
            }

            if (currentState == GameState.INTRO_DIALOG) {
                if (keycode == Input.Keys.ENTER) {
                    return dialogShowBeginning.handleDialogInput(false, true);
                }
            }

            if (currentState == GameState.DIALOG) {
                if (keycode == Input.Keys.ENTER) {
                    return dialogShow.handleDialogInput(false, true);
                }
            }

            if (currentState == GameState.PUZZLE) {
                if (endLevelDialog.isActive()) {
                    if (keycode == Input.Keys.ENTER) {
                        return endLevelDialog.handleInput(false, true, null);
                    }
                }
                return puzzleGame.handleKeyPress(keycode);
            }

            return false;
        }
    }
}
