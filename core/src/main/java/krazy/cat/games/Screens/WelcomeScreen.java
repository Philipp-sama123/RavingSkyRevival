package krazy.cat.games.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import krazy.cat.games.RavingSky;


public class WelcomeScreen implements Screen {

    public static final int VISUAL_HEIGHT = 800;
    public static final int VISUAL_WIDTH = 400;

    private Stage stage;
    private RavingSky ravingSkyGame;
    private SpriteBatch batch;
    private Texture background;
    private OrthographicCamera gameCamera;

    private FitViewport gameViewport;

    public WelcomeScreen(RavingSky ravingSkyGame) {
        this.ravingSkyGame = ravingSkyGame;
        batch = new SpriteBatch();
        background = new Texture("bg.png");

        //   gameCamera = new OrthographicCamera();

        //create a FitViewport to maintain virtual acpect ratio
        // gameViewport = new FitViewport(VISUAL_WIDTH, VISUAL_HEIGHT, gameCamera);
        //  gameViewport.apply();
        //  gameCamera.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);
        //   stage = new Stage(gameViewport);
    }

    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);
        //   gameCamera.update();

    }

    private void handleInput(float dt) {
        if (Gdx.input.isTouched()) {
            Gdx.app.log("TOUCH", "touched");
            ravingSkyGame.setScreen(new GameScreen(ravingSkyGame));
        }

    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        ravingSkyGame.batch.setProjectionMatrix(stage.getCamera().combined);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
//        gameViewport.update(width, height);
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
}
