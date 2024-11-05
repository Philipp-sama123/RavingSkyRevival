package krazy.cat.games.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import krazy.cat.games.RavingSky;

public class Item {
    private ArrayList<Integer> itemXs = new ArrayList<Integer>();
    private ArrayList<Integer> itemYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> itemRectangles = new ArrayList<Rectangle>();

    private Texture itemTexture;

    private boolean isGoodItem;
    private boolean killPlayer;
    private boolean isMainCharacterAwesome;

    public int itemCount;

    private Random randomValue;


    public Item(Texture itemTexture, boolean isGoodItem) {
        randomValue = new Random();
        killPlayer = false;
        isMainCharacterAwesome = false;

        this.itemTexture = itemTexture;
        this.isGoodItem = isGoodItem;
    }

    public void makeItem() {//private
        float height = randomValue.nextFloat() * Gdx.graphics.getHeight();
        itemXs.add(Gdx.graphics.getWidth());
        itemYs.add((int) height);
    }

    public void spawnItem() {
        if (itemCount < RavingSky.COIN_SPAWN_FREQUENCY) {
            itemCount++;
        } else {
            itemCount = 0;
            makeItem();
        }
    }

    public void removeItems() {
        itemXs.clear();
        itemYs.clear();
        itemRectangles.clear();
        itemCount = 0;
        killPlayer = false;
    }

    public void itemTouched(int touchedItem) {//private
        Gdx.app.log("Item", "collision");
        itemRectangles.remove(touchedItem);
        itemXs.remove(touchedItem);
        itemYs.remove(touchedItem);
    }

    public void itemOutOfScreen(int itemOutOfScreenIndex) {//private
        itemRectangles.remove(itemOutOfScreenIndex);
        itemXs.remove(itemOutOfScreenIndex);
        itemYs.remove(itemOutOfScreenIndex);
    }

    public void drawItemOnScreen(boolean isMainCharacterAwesome, Batch batch) {
        itemRectangles.clear();
        this.isMainCharacterAwesome = isMainCharacterAwesome;
        for (int i = 0; i < itemXs.size(); i++) {

            batch.draw(itemTexture, itemXs.get(i), itemYs.get(i));

            setItemVelocity(isMainCharacterAwesome, i);

            itemRectangles.add(new Rectangle(
                itemXs.get(i),
                itemYs.get(i),
                itemTexture.getWidth(),
                itemTexture.getHeight()
            ));
        }
    }

    private void setItemVelocity(boolean isMainCharacterAwesome, int i) {
        this.isMainCharacterAwesome = isMainCharacterAwesome;
        if (isMainCharacterAwesome) {
            if (isGoodItem) {
                itemXs.set(i, itemXs.get(i) - RavingSky.COIN_VELOCITY_IN_PX * RavingSky.AWESOME_VELOCITY_MULTIPLIER_IN_PX);
            } else {
                itemXs.set(i, itemXs.get(i) - RavingSky.BOMB_VELOCITY_IN_PX * RavingSky.AWESOME_VELOCITY_MULTIPLIER_IN_PX);
            }
        } else {
            if (isGoodItem) {
                itemXs.set(i, itemXs.get(i) - RavingSky.COIN_VELOCITY_IN_PX);
            } else {
                itemXs.set(i, itemXs.get(i) - RavingSky.BOMB_VELOCITY_IN_PX);
            }
        }
    }


    public int checkForItemCollision(Rectangle mainCharacterRectangle, Rectangle offScreenRectangle, int score) {
        for (int i = 0; i < itemRectangles.size(); i++) {
            if (Intersector.overlaps(mainCharacterRectangle, itemRectangles.get(i))) {
                itemTouched(i);
                score++;
                if (isGoodItem || isMainCharacterAwesome)
                    break;
                else {
                    killPlayer = true;
                    break;
                }
            }
            if (Intersector.overlaps(offScreenRectangle, itemRectangles.get(i))) {
                itemOutOfScreen(i);
                break;
            }
        }
        return score;
    }

    public boolean isGoodItem() {
        return isGoodItem;
    }

    public boolean isKillPlayer() {
        return killPlayer;
    }

    public void updateItemState(boolean isMainCharacterAwesome) {
        this.isMainCharacterAwesome = isMainCharacterAwesome;
    }
}
