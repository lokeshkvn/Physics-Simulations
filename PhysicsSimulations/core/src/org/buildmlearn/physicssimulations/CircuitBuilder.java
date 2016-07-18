package org.buildmlearn.physicssimulations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Locale;

import javax.sound.sampled.Line;
import javax.xml.soap.Text;


public class CircuitBuilder extends SimulationType {

    private Skin skin;
    private TextureAtlas atlas;

    private Stage stage;

    private Table table;

    private Texture bulbTexture;
    private Texture inductorTexture;
    private Texture batteryTexture;
    private Texture switchTexture;
    private Texture resistorTexture;

    ShapeRenderer shapeRenderer;

    ImageButton selectedButton;
    int selectedTexture;
    Array<Texture> textures = new Array<Texture>();
    float W;
    float H;

    @Override
    public void create() {

        shapeRenderer = new ShapeRenderer();

        atlas = new TextureAtlas(Gdx.files.internal("data/ui-blue.atlas"));

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        skin.addRegions(atlas);

        BitmapFont font = new BitmapFont(Gdx.files.internal("data/arial_30_bold.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

        stage = new Stage(new ScreenViewport());

        W = Gdx.graphics.getWidth();
        H = Gdx.graphics.getHeight();

        final Label inductorLabel = new Label("Inductor", labelStyle);
        Label switchLabel   = new Label("Switch", labelStyle);
        Label batteryLabel  = new Label("Battery", labelStyle);
        Label bulbLabel     = new Label("Light Bulb", labelStyle);
        Label resistorLabel = new Label("Resistor", labelStyle);

        final Label inductorValue = new Label("2 H", skin);
        final Label batteryValue = new Label("9 V", skin);
        final Label resistorValue = new Label("5 Ω", skin);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.knob = skin.getDrawable("knob_03");
        sliderStyle.background = skin.getDrawable("slider_back_hor");

        final Slider inductorSlider = new Slider(1, 5, 1, false, sliderStyle);
        inductorSlider.setValue(2);
        inductorSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                inductorValue.setText(String.format(Locale.US, "%.0f H", inductorSlider.getValue()));
            }
        });

        final Slider batterySlider = new Slider(1, 20, 1, false, sliderStyle);
        batterySlider.setValue(9);
        batterySlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                batteryValue.setText(String.format(Locale.US, "%.0f V", batterySlider.getValue()));
            }
        });

        final Slider resistorSlider = new Slider(1, 10, 1, false, sliderStyle);
        resistorSlider.setValue(5);
        resistorSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                resistorValue.setText(String.format(Locale.US, "%.0f Ω", resistorSlider.getValue()));
            }
        });

        inductorTexture = new Texture(Gdx.files.internal("circuit/inductor.png"), true);
        inductorTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        Texture inductorPressed = new Texture(Gdx.files.internal("circuit/inductor_pressed.png"), true);
        final ImageButton inductorImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(inductorTexture)),
                new TextureRegionDrawable(new TextureRegion(inductorTexture)), new TextureRegionDrawable(new TextureRegion(inductorPressed)));
        inductorImage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedButton.setChecked(false);
                selectedButton = inductorImage;
                selectedTexture = 0;
            }
        });
        selectedButton = inductorImage;
        selectedTexture = 0;

        switchTexture = new Texture(Gdx.files.internal("circuit/switch_off.png"), true);
        switchTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        Texture switchPressed = new Texture(Gdx.files.internal("circuit/switch_off_pressed.png"), true);
        final ImageButton switchImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(switchTexture)),
                new TextureRegionDrawable(new TextureRegion(switchTexture)), new TextureRegionDrawable(new TextureRegion(switchPressed)));
        switchImage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedButton.setChecked(false);
                selectedButton = switchImage;
                selectedTexture = 1;
            }
        });


        batteryTexture = new Texture(Gdx.files.internal("circuit/battery.png"), true);
        batteryTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        Texture batteryPressed= new Texture(Gdx.files.internal("circuit/battery_pressed.png"), true);
        final ImageButton batteryImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(batteryTexture)),
                new TextureRegionDrawable(new TextureRegion(batteryTexture)), new TextureRegionDrawable(new TextureRegion(batteryPressed)));
        batteryImage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedButton.setChecked(false);
                selectedButton = batteryImage;
                selectedTexture = 2;
            }
        });

        bulbTexture = new Texture(Gdx.files.internal("circuit/bulby.png"), true);
        bulbTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        Texture bulbPressed = new Texture(Gdx.files.internal("circuit/bulby_pressed.png"), true);
        final ImageButton bulbImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(bulbTexture)),
                new TextureRegionDrawable(new TextureRegion(bulbTexture)), new TextureRegionDrawable(new TextureRegion(bulbPressed)));
        bulbImage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedButton.setChecked(false);
                selectedButton = bulbImage;
                selectedTexture = 3;
            }
        });

        resistorTexture = new Texture(Gdx.files.internal("circuit/resistor.png"), true);
        resistorTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        Texture resistorPressed = new Texture(Gdx.files.internal("circuit/resistor_pressed.png"), true);
        final ImageButton resistorImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(resistorTexture)),
                new TextureRegionDrawable(new TextureRegion(resistorTexture)), new TextureRegionDrawable(new TextureRegion(resistorPressed)));
        resistorImage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedButton.setChecked(false);
                selectedButton = resistorImage;
                selectedTexture = 4;
            }
        });

        Texture inductorRotated= new Texture(Gdx.files.internal("circuit/inductor_rotated.png"), true);
        Texture switchRotated = new Texture(Gdx.files.internal("circuit/switch_off_rotated.png"), true);
        Texture batteryRotated= new Texture(Gdx.files.internal("circuit/battery_rotated.png"), true);
        Texture bulbRotated = new Texture(Gdx.files.internal("circuit/bulby_rotated.png"), true);
        Texture resistorRotated = new Texture(Gdx.files.internal("circuit/resistor_rotated.png"), true);

        textures.addAll(inductorTexture, switchTexture, batteryTexture, bulbTexture, resistorTexture,
                inductorRotated, switchRotated, batteryRotated, bulbRotated, resistorRotated);

        Table t1 = new Table();
        t1.add(inductorValue).center().expandX();
        t1.row();
        t1.add(inductorSlider);

        Table t2 = new Table();
        t2.add(batteryValue).center();
        t2.row();
        t2.add(batterySlider);

        Table t3 = new Table();
        t3.add(resistorValue).center();
        t3.row();
        t3.add(resistorSlider);

        table = new Table();
        table.center().bottom();
//        table.setFillParent(true);

        table.add(t1);
        table.add();
        table.add(t2);
        table.add();
        table.add(t3);


        table.row().padTop(10);
        table.add(inductorImage);
        table.add(switchImage);
        table.add(batteryImage);
        table.add(bulbImage);
        table.add(resistorImage);

        table.row().padTop(10);
        table.add(inductorLabel);
        table.add(switchLabel);
        table.add(batteryLabel);
        table.add(bulbLabel);
        table.add(resistorLabel);

        //GRID
        Table grid = new Table();
        grid.center().top().padTop(10);
        grid.setFillParent(true);
        for (int i = 0; i < 5; i++) {
            grid.add();
            Button button = new Button(skin);
            final Cell c = grid.add(button).width(100).height(50);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("CV","AC");
                    c.setActor(newActor(selectedTexture, c, false));
                }
            });
        }
        grid.row();
        for (int i = 0; i < 6; i++) {
            Button button = new Button(skin);
            final Cell c = grid.add(button).width(50).height(100);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("CV","AC");
                    c.setActor(newActor(selectedTexture, c, true));
                }
            });
            grid.add();
        }
        grid.row();
        for (int i = 0; i < 5; i++) {
            grid.add();
            Button button = new Button(skin);
            final Cell c = grid.add(button).width(100).height(50);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("CV","AC");
                    c.setActor(newActor(selectedTexture, c, false));
                }
            });
        }
        grid.row();
        for (int i = 0; i < 6; i++) {
            Button button = new Button(skin);
            final Cell c = grid.add(button).width(50).height(100);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("CV","AC");
                    c.setActor(newActor(selectedTexture, c, true));
                }
            });
            grid.add();
        }
        grid.row();
        for (int i = 0; i < 5; i++) {
            grid.add();
            Button button = new Button(skin);
            final Cell c = grid.add(button).width(100).height(50);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("CV","AC");
                    c.setActor(newActor(selectedTexture, c, false));
                }
            });
        }

//        stage.setDebugAll(true);
        stage.addActor(table);
        stage.addActor(grid);

        Gdx.input.setInputProcessor(stage);
    }

    Actor newActor(int index, final Cell c, final boolean rotated) {
        Texture t = textures.get(rotated ? index+5 : index);
        ImageButton actor = new ImageButton(new TextureRegionDrawable(new TextureRegion(t)),
                new TextureRegionDrawable(new TextureRegion(t)), new TextureRegionDrawable(new TextureRegion(t)));
        c.width(100).height(50);

        if (rotated) {
            c.width(50).height(100);
//            actor.setTransform(true);
//            actor.setPosition(0,0);
//            actor.setOrigin(100/2, 50/2);
//            actor.rotateBy(90);
        }
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                c.setActor(newActor(selectedTexture, c, rotated));
            }
        });
        return actor;
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        this.table.setFillParent(true);
        this.table.invalidate();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);

        stage.act(delta);
        stage.draw();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLACK);
//        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();

        inductorTexture.dispose();
        switchTexture.dispose();
        batteryTexture.dispose();
        bulbTexture.dispose();
        resistorTexture.dispose();
    }

}