package krazy.cat.games.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import krazy.cat.games.RavingSky;
import krazy.cat.games.Sprites.Item;


public class GameScreen implements Screen {
    private RavingSky ravingSkyGame;
    private SpriteBatch batch;

    private Texture background;
    private Texture[] mainCharacter;
    private Texture dizzyMainCharacter;

    private Texture mushroom;
    private Texture coin;
    private Texture bomb;

    private BitmapFont textToShow;

    private int mainCharacterState;
    private int pause = 0;
    private int mainCharacterY;

    private int mushroomCount;
    private int bombCount;
    private int coinCount;

    private int gameState = 0; // todo extract?!

    private int score = 0;

    private float gravity;
    private float velocity = 0;

    private ArrayList<Integer> coinXs = new ArrayList<Integer>();
    private ArrayList<Integer> coinYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    private Item coinItem;
    private Item bombItem;
    private ArrayList<Integer> bombXs = new ArrayList<Integer>();
    private ArrayList<Integer> bombYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();


    private ArrayList<Integer> mushroomXs = new ArrayList<Integer>();
    private ArrayList<Integer> mushroomYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> mushroomRectangles = new ArrayList<Rectangle>();

    private Random randomValue;

    private Rectangle mainCharacterRectangle;
    private Rectangle offScreenRectangle;

    private boolean isMushroomTouched;
    private boolean isMainCharacterAwesome;

    public GameScreen(RavingSky ravingSkyGame) {
        gravity = 5f;

        this.ravingSkyGame = ravingSkyGame;
        batch = new SpriteBatch();
        background = new Texture("bg.png");

        createMainCharacter();

        offScreenRectangle = new Rectangle(-Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //coin = new Texture("coin.png");
        coinItem = new Item(new Texture("coin.png"), true);
        bombItem = new Item(new Texture("bomb.png"), false);

        mushroom = new Texture("mushroom.png");
        dizzyMainCharacter = new Texture("dizzy-1.png");
        randomValue = new Random();

        createTextToShow();
        isMushroomTouched = false;

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 1) {

            coinItem.spawnItem();
            bombItem.spawnItem();
            coinItem.updateItemState(isMainCharacterAwesome);
            bombItem.updateItemState(isMainCharacterAwesome);
            spawnMushroom();

            makePlayer();

            if (isMainCharacterAwesome) {
                makeMainCharacterAwesome();
                //make timeout to make mainChar for a while awesome then
                //  isMushroomTouched = false; // think about it
            } else {
                gravity = 5f;
            }

            coinItem.drawItemOnScreen(isMainCharacterAwesome, batch);
            bombItem.drawItemOnScreen(isMainCharacterAwesome, batch);

            mushroomRectangles.clear();
            for (int i = 0; i < mushroomXs.size(); i++) {
                batch.draw(mushroom, mushroomXs.get(i), mushroomYs.get(i));

                mushroomXs.set(i, mushroomXs.get(i) - RavingSky.MUSHROOM_VELOCITY_IN_PX);

                mushroomRectangles.add(new Rectangle(
                    mushroomXs.get(i),
                    mushroomYs.get(i),
                    mushroom.getWidth(),
                    mushroom.getHeight()
                ));
            }


            if (Gdx.input.justTouched()) {
                velocity = -RavingSky.PLAYER_JUMP_HEIGHT;
            }
            if (pause < 2) {
                pause++;
            } else {
                pause = 0;
                if (mainCharacterState < 3) {
                    mainCharacterState++;
                } else {
                    mainCharacterState = 0;
                }
            }

            velocity += gravity;
            mainCharacterY -= velocity;

            if (mainCharacterY <= 0) {
                mainCharacterY = 0;
            }

//improve!!
            if (coinItem.isGoodItem()) {
                score = coinItem.checkForItemCollision(mainCharacterRectangle, offScreenRectangle, score);
                bombItem.checkForItemCollision(mainCharacterRectangle, offScreenRectangle, score);
            } else {
                gameState = 2;
                coinItem.checkForItemCollision(mainCharacterRectangle, offScreenRectangle, score);
                bombItem.checkForItemCollision(mainCharacterRectangle, offScreenRectangle, score);
            }

            if (bombItem.isKillPlayer()) {
                gameState = 2;
            }

            for (int i = 0; i < mushroomRectangles.size(); i++) {
                if (Intersector.overlaps(mainCharacterRectangle, mushroomRectangles.get(i))) {
                    Gdx.app.log("Mushroom", "collision");

                    isMushroomTouched = true; // is mushroom touched function is diffuse .. will get refactored so boolean is for future

                    mushroomTouched(i);
                    break;
                }
                if (Intersector.overlaps(offScreenRectangle, mushroomRectangles.get(i))) {
                    mushroomOutOfScreen(i);
                    break;
                }
            }

        } else if (gameState == 2) {
            if (Gdx.input.justTouched()) {
                restartGame();
            }
        }

        if (gameState == 2) {
            batch.draw(
                dizzyMainCharacter,
                Gdx.graphics.getWidth() / 2 - dizzyMainCharacter.getWidth() / 2,
                mainCharacterY
            );
        } else {
            batch.draw(
                mainCharacter[mainCharacterState],
                Gdx.graphics.getWidth() / 2 - mainCharacter[mainCharacterState].getWidth() / 2,
                mainCharacterY
            );
        }
        textToShow.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    private void makeMainCharacterAwesome() {
        gravity = 3f;
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    private void makePlayer() {
        mainCharacterRectangle = new Rectangle(
            Gdx.graphics.getWidth() / 2 - mainCharacter[mainCharacterState].getWidth() / 2,
            mainCharacterY,
            mainCharacter[mainCharacterState].getWidth(),
            mainCharacter[mainCharacterState].getHeight()
        );

    }

    private void makeMushroom() {
        float height = randomValue.nextFloat() * Gdx.graphics.getHeight();
        mushroomXs.add(Gdx.graphics.getWidth());
        mushroomYs.add((int) height);
    }

    private void createMainCharacter() { // mainChar class ?!
        mainCharacter = new Texture[4];
        mainCharacter[0] = new Texture("frame-1.png");
        mainCharacter[1] = new Texture("frame-2.png");
        mainCharacter[2] = new Texture("frame-3.png");
        mainCharacter[3] = new Texture("frame-4.png");
        mainCharacterY = Gdx.graphics.getHeight() / 2;
    }

    private void createTextToShow() { // HUD class ?!
        textToShow = new BitmapFont();
        textToShow.setColor(Color.WHITE);
        textToShow.getData().setScale(10);

    }

    private void spawnMushroom() {
        if (mushroomCount < RavingSky.MUSHROOM_SPAWN_FREQUENCY) {
            mushroomCount++;
        } else {
            mushroomCount = 0;
            makeMushroom();
        }
    }

    private void mushroomOutOfScreen(int mushroomOutOfScreenIndex) {
        mushroomRectangles.remove(mushroomOutOfScreenIndex);
        mushroomXs.remove(mushroomOutOfScreenIndex);
        mushroomYs.remove(mushroomOutOfScreenIndex);
    }

    private void mushroomTouched(int touchedMushroom) {
        Gdx.app.log("Mushroom", "collision");
        isMainCharacterAwesome = true;
        mushroomRectangles.remove(touchedMushroom);
        mushroomXs.remove(touchedMushroom);
        mushroomYs.remove(touchedMushroom);
    }

    private void restartGame() {
        score = 0;
        gameState = 1;
        velocity = 0;
        coinItem.removeItems();
        bombItem.removeItems();
        mainCharacterY = Gdx.graphics.getHeight() / 2;
        isMainCharacterAwesome =false;
    }

}
