package com.project.spaceattack;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import static com.project.spaceattack.Constants.*;

/**
 * Controller for the main application.
 */
public class GameController {
    /**
     * Stores lists of enemies: one for each row.
     */
    List<List<ShipSprite>> enemies = new ArrayList<>(DEFAULT_ARRAY_CAPACITY);
    List<SpaceAttackSprite> sprites = new ArrayList<>();
    List<Item> asteroids = new ArrayList<>(), hearths = new ArrayList<>(), shields = new ArrayList<>(), swords = new ArrayList<>();
    List<Item> shipMissiles = new ArrayList<>(), alienMissiles = new ArrayList<>();
    List<ShipSprite> enemiesRed = new ArrayList<>();
    int points = ZERO, boostMultiplier = DEFAULT_BOOST_MULTIPLIER;

    /**
     * Boolean values used for checks.
     */
    boolean playerCanShoot = true, isPaused = false, isWaiting = false, isDead = false, invulnerable = false;
    boolean checkShield = false, checkSword = false, checkMusic = true;

    /**
     * Long values used to check boosts' time.
     */
    long shieldStart, shieldPause, shieldRemaining, swordStart, swordPause, swordRemaining;
    int maxEnemiesRow = 4;
    double defaultEnemyYellowSpeed = 2, defaultEnemyRedSpeed = 4;
    double defaultMissileBlueSpeed = 10, defaultMissileGreenSpeed = 20, defaultItemSpeed = 7;
    double defaultProbabilityEnemy = 0.003, defaultProbabilityEnemyRed = 0.25;
    AnimationTimer animationTimer;
    Pane root;
    Scene scene;
    HashMap<String, Image> resources;
    ShipSprite playerShip;
    RandomGenerator randomGenerator = RandomGenerator.getDefault();
    Sound sound = new Sound();
    Label score, pause, healthLabel, gameOver;
    TextField yourScore;
    ImageView healthBar;
    String yourName;
    Stage primaryStage;

    /**
     * Scheduler that can schedule commands to run after a given delay, or to execute periodically:
     * used to handle boosts.
     */
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Task to put the shield boost on the player.
     */
    Runnable shieldOn = new Runnable() {
        @Override
        public void run() {
            invulnerable = true;
            resources.replace("PlayerShip", new Image(Objects.requireNonNull(getClass().getResourceAsStream
                    ("images" + "/playerShipFrontShield.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShip-Left", new Image(Objects.requireNonNull(getClass().getResourceAsStream
                    ("images" + "/playerShip-LeftShield.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShip-Right", new Image(Objects.requireNonNull(getClass().getResourceAsStream
                    ("images" + "/playerShip-RightShield.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShipDamaged1", new Image(Objects.requireNonNull(getClass().getResourceAsStream
                    ("images" + "/playerShipDamaged1Shield.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShipDamaged2", new Image(Objects.requireNonNull(getClass().getResourceAsStream
                    ("images" + "/playerShipDamaged2Shield.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
        }
    };

    /**
     * Task to take the shield boost off the player.
     */
    Runnable shieldOff = new Runnable() {
        @Override
        public void run() {
            invulnerable = false;
            resources.replace("PlayerShip", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/playerShipFront.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShip-Left", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/playerShip-Left.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShip-Right", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/playerShip-Right.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShipDamaged1", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/playerShipDamaged1.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
            resources.replace("PlayerShipDamaged2", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/playerShipDamaged2.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
        }
    };

    /**
     * Task to put the sword boost on the player.
     */
    Runnable boostOn = new Runnable() {
        @Override
        public void run() {
            boostMultiplier = 2;
            resources.replace("Missile", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/missile1Boost.png")), DEFAULT_MISSILE_WIDTH, DEFAULT_MISSILE_HEIGHT, false, true));
        }
    };

    /**
     * Task to take the sword boost off the player.
     */
    Runnable boostOff = new Runnable() {
        @Override
        public void run() {
            boostMultiplier = 1;
            resources.replace("Missile", new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/missile1.png")), DEFAULT_MISSILE_WIDTH, DEFAULT_MISSILE_HEIGHT, false, true));
        }
    };
    public void start(Stage stage, double difficulty){
        //  Sets the primary stage to the current stage.
        primaryStage = stage;
        primaryStage.setAlwaysOnTop(true);
        //  Loads resources with the specific method.
        loadResources();
        //  Creates the elements of the game screen.
        root = new Pane();
        root.setPrefSize(750,750);
        //  Modify the background.
        Background background = new Background(new BackgroundImage(resources.get("Image"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT));
        root.setBackground(background);
        //  Adds screen elements.
        healthLabel = new Label("HEALTH: ");
        healthLabel.setFont(Font.font("System",20));
        healthLabel.setTextFill(Paint.valueOf("#19cf31"));
        healthLabel.setLayoutX(1370);
        healthLabel.setLayoutY(15);
        root.getChildren().add(healthLabel);

        healthBar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/like32.png"))));
        healthBar.setLayoutX(1450);
        healthBar.setLayoutY(20);
        healthBar.setFitWidth(60);
        healthBar.setFitHeight(20);
        root.getChildren().add(healthBar);

        score = new Label("SCORE: " + points);
        score.setLayoutY(15);
        score.setFont(Font.font("System",20));
        score.setTextFill(Paint.valueOf("#19cf31"));
        root.getChildren().add(score);
        scene = new Scene(root);
        scene.setCursor(Cursor.NONE);

        //  Key pressed and released handler.
        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnKeyReleased(this::keyReleased);
        //  Sets properties of the stage.
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setTitle("SPACE ATTACK!");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1550);
        primaryStage.setHeight(830);
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons" + "/arcade-game.png"))));
        primaryStage.show();
        //  Sets game features based on the difficulty.
        defaultEnemyYellowSpeed *= difficulty;
        defaultEnemyRedSpeed *= difficulty;
        defaultItemSpeed *= difficulty;
        defaultProbabilityEnemy *= difficulty;
        defaultProbabilityEnemyRed *= difficulty;
        maxEnemiesRow *= difficulty;
        // Initialize elements.
        initialize();
        initializeTimer();
    }

    /**
     * Load resources,
     * stored in an HashMap.
     */
    private void loadResources() {
        resources = new HashMap<>();
        resources.put("PlayerShip", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/playerShipFront.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
        resources.put("PlayerShipDamaged1", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/playerShipDamaged1.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
        resources.put("PlayerShipDamaged2", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/playerShipDamaged2.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT, false, true));
        resources.put("Missile", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/missile1.png")), DEFAULT_MISSILE_WIDTH, DEFAULT_MISSILE_HEIGHT, false, true));
        resources.put("Missile2", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/missile2.png")), DEFAULT_MISSILE_WIDTH, DEFAULT_MISSILE_HEIGHT, false, true));
        resources.put("Missile3", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/missile3.png")), DEFAULT_MISSILE_WIDTH, DEFAULT_MISSILE_HEIGHT, false, true));
        resources.put("Image", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/background.gif")), BORDER_WIDTH, BORDER_HEIGHT,false, false));
        resources.put("PlayerShip-Left", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/playerShip-Left.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT,false, true));
        resources.put("PlayerShip-Right", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/playerShip-Right.png")), DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT,false, true));
        resources.put("EnemyShip1", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/enemyShip1.png")), DEFAULT_ENEMY1_SIZE, DEFAULT_ENEMY1_SIZE,false, true));
        resources.put("EnemyShip2", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/enemyShip2.png")), DEFAULT_ENEMY2_SIZE, DEFAULT_ENEMY2_SIZE,false, true));
        resources.put("EnemyShip2Damaged", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/enemyShip2Damaged.png")), DEFAULT_ENEMY2_SIZE, DEFAULT_ENEMY2_SIZE,false, true));
        resources.put("Asteroid", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/asteroid.png")), DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT,false, true));
        resources.put("Shield", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/shield.png")), DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT,false, true));
        resources.put("Sword", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/sword.png")), DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT,false, true));
        resources.put("Like", new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/like.png")), DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT,false, true));
    }

    /**
     * Initialize player and enemy lists.
     */
    private void initialize() {
        playerShip =    new ShipSprite(new ImageView(resources.get("PlayerShip")),new PVector(STARTING_X, STARTING_Y),
                        new PVector(ZERO, ZERO), "PlayerShip", DEFAULT_PLAYER_HEALTH, 0);
        playerShip.setVisible(false);
        root.getChildren().add(playerShip);

        for(int i = 0; i < DEFAULT_ARRAY_CAPACITY; i++) {
            enemies.add(new ArrayList<>());
        }
    }

    /**
     * Call the method handle on every frame.
     */
    void initializeTimer() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                mainLoop();
            }
        };
        animationTimer.start();
    }

    /**
     * The main loop of the game.
     */
    private void mainLoop() {
        if(!isPaused && checkMusic) {
            fillLists();
            UpdateDisplay();
            checkPlayerBounds();
            checkPlayerSprite();
            fillRows();
            checkEnemiesSprite();
            generateItems();
            checkEnemiesBounds();
            checkEnemiesY();
            enemiesShoot();
            ItemManager();
            contactManager();
            dyingHandler();
            //  Remove entities out of bounds.
            root.getChildren().removeIf(sprite -> (sprite.getTranslateY() > BORDER_DOWN + 100));
            //  Remove dead entities.
            root.getChildren().removeIf(x -> (x instanceof SpaceAttackSprite) && (!((SpaceAttackSprite) x).isAlive()));
        }
    }

    /**
     * Pause handler.
     */
    void pause() {
        shieldPause = System.nanoTime();
        swordPause = System.nanoTime();
        isPaused = true;
        pause = new Label("PAUSE\nPress R to resume the game");
        pause.setTextAlignment(TextAlignment.CENTER);
        pause.setLayoutX(365);
        pause.setLayoutY(300);
        pause.setFont(Font.font("System", 64));
        pause.setTextFill(Paint.valueOf("#19cf31"));
        root.getChildren().add(pause);
        if(invulnerable) {
            checkShield = true;
            shieldRemaining = shieldRemaining - (shieldPause - shieldStart);
        }
        if(boostMultiplier == 2) {
            checkSword = true;
            swordRemaining = swordRemaining - (swordPause - swordStart);
        }
    }

    /**
     * Resume handler.
     */
    void resume() {
        shieldStart = System.nanoTime();
        swordStart = System.nanoTime();
        isPaused = false;
        root.getChildren().remove(pause);
        if(checkShield) {
            checkShield = false;
            scheduler.execute(shieldOn);
            scheduler.schedule(shieldOff, shieldRemaining, TimeUnit.NANOSECONDS);
        }
        if(checkSword) {
            checkSword = false;
            scheduler.execute(boostOn);
            scheduler.schedule(boostOff, swordRemaining, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Key pressed handler.
     * @param event the key pressed
     */
    void keyPressed(KeyEvent event) {
        if(playerShip.isAlive && !isPaused) {
            //  Player movement.
            if (event.getCode() == KeyCode.D && checkRightBound(playerShip)) {
                moveRight();
            }
            if (event.getCode() == KeyCode.A && checkLeftBound(playerShip)) {
                moveLeft();
            }
            if (event.getCode() == KeyCode.W && checkUpBound(playerShip)) {
                moveUp();
            }
            if (event.getCode() == KeyCode.S && checkDownBound(playerShip)) {
                moveDown();
            }
            //  Player shooting.
            if (event.getCode() == KeyCode.SPACE && playerCanShoot) {
                shootMissile(playerShip, new ImageView(resources.get("Missile")), -DEFAULT_MISSILE_SPEED, DEFAULT_MISSILE_DAMAGE, "Missile");
                sound.musicShoot();
                playerCanShoot = false;
            }
            //  Points spending.
            if(points >= DEFAULT_ITEM_COST) {
                Item item;
                if(event.getCode() == KeyCode.DIGIT1) {
                    item = new Item(new ImageView(resources.get("Like")), new PVector(playerShip.getLocation().x, STARTING_ENEMY_Y),
                            new PVector(ZERO, defaultItemSpeed), "Like", DEFAULT_HEALING);
                    item.setVisible(false);
                    root.getChildren().add(item);
                    updateScore(points - DEFAULT_ITEM_COST);
                }
                if(event.getCode() == KeyCode.DIGIT2) {
                    item = new Item(new ImageView(resources.get("Shield")), new PVector(playerShip.getLocation().x, STARTING_ENEMY_Y),
                            new PVector(ZERO, defaultItemSpeed), "Shield", ZERO);
                    item.setVisible(false);
                    root.getChildren().add(item);
                    updateScore(points - DEFAULT_ITEM_COST);
                }
                if(event.getCode() == KeyCode.DIGIT3) {
                    item = new Item(new ImageView(resources.get("Sword")), new PVector(playerShip.getLocation().x, STARTING_ENEMY_Y),
                            new PVector(ZERO, defaultItemSpeed), "Sword", ZERO);
                    item.setVisible(false);
                    root.getChildren().add(item);
                    updateScore(points - DEFAULT_ITEM_COST);
                }
            }
        }
        //  Pause & resume.
        if(playerShip.isAlive && !isWaiting) {
            if (event.getCode() == KeyCode.P && !isPaused) {
                pause();
            }
            if (event.getCode() == KeyCode.R && isPaused) {
                resume();
            }
        }
        //  Exit game.
        if(event.getCode() == KeyCode.ESCAPE) {
            checkMusic = false;
            MenuApplication.music.mediaPlayer.setMute(false);
            Stage stage = (Stage)root.getScene().getWindow();
            stage.close();
        }
        //  Game over.
        if(!playerShip.isAlive && !isWaiting) {
            //  New game.
            if (event.getCode() == KeyCode.N) {
                MenuApplication.music.mediaPlayer.setMute(false);
                newGame();
            }
            //  End game.
            if (event.getCode() == KeyCode.Q) {
                scene.setCursor(Cursor.DEFAULT);
                endGame();
            }
        }
        //  Save score.
        if(event.getCode() == KeyCode.ENTER && !playerShip.isAlive && isWaiting) {
            isWaiting = false;
            yourName = yourScore.getText();
            if(yourName.contains(DELIMITER)) {
                sound.musicError();
                yourScore.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username can't contain the character " + DELIMITER);
                alert.initOwner(primaryStage);
                alert.initModality(Modality.NONE);
                alert.initStyle(StageStyle.TRANSPARENT);
                alert.setTitle("ERROR"); alert.setHeaderText("Username error");
                alert.showAndWait();
                endGame();
            }
            else if(yourName.isEmpty()) {
                sound.musicError();
                yourScore.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username can't be empty");
                alert.initOwner(primaryStage);
                alert.initModality(Modality.NONE);
                alert.initStyle(StageStyle.TRANSPARENT);
                alert.setTitle("ERROR"); alert.setHeaderText("Username error");
                alert.showAndWait();
                endGame();
            }
            else {
                MenuApplication.music.mediaPlayer.setMute(false);
                try {
                    Files.write(Paths.get("src/main/resources/com/project/spaceattack/data/scores.txt"),
                            (yourName + ";" + points + ";").getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Key released handler.
     * @param event the key that has been released
     */
    void keyReleased(KeyEvent event) {
        //  Player movement.
        if((event.getCode() == KeyCode.D) || (event.getCode() == KeyCode.A)) {
            playerShip.setVelocity(new PVector(ZERO, playerShip.getVelocity().y));
        }
        if((event.getCode() == KeyCode.W) || (event.getCode() == KeyCode.S)) {
            playerShip.setVelocity(new PVector(playerShip.getVelocity().x, ZERO));
        }
        //  Player shooting.
        if(event.getCode() == KeyCode.SPACE) {
            playerCanShoot = true;
        }
    }

    /**
     * Fill rows with enemies.
     */
    void fillRows() {
        for(int i = 0; i < enemies.size(); i++) {
            if(enemies.get(i).size() < maxEnemiesRow && randomGenerator.nextDouble() < defaultProbabilityEnemy) {
                if(randomGenerator.nextDouble() < defaultProbabilityEnemyRed) {
                    generateEnemies(enemies.get(i), defaultEnemyRedSpeed, defaultEnemyRedSpeed, "EnemyShip2",
                            new ImageView(resources.get("EnemyShip2")), DEFAULT_ENEMY2_HEALTH, i + 1);
                } else {
                    generateEnemies(enemies.get(i), defaultEnemyYellowSpeed, defaultEnemyYellowSpeed, "EnemyShip1",
                            new ImageView(resources.get("EnemyShip1")), DEFAULT_ENEMY1_HEALTH, i + 1);
                }
            }
        }
    }

    /**
     * Initialize a new enemy.
     * @param list the list to which the enemy belongs to
     * @param velocityX the x coordinate of the enemy's velocity
     * @param velocityY the y coordinate of the enemy's velocity
     * @param description the description of the enemy
     * @param sprite the image of the enemy
     * @param health the base health of the enemy
     * @param row the row to which the enemy belongs to
     */
    void generateEnemies(List<ShipSprite> list, double velocityX, double velocityY, String description, ImageView sprite, int health, int row) {
        boolean startSpeed = randomGenerator.nextBoolean();
        ShipSprite enemy = new ShipSprite(sprite, new PVector(randomGenerator.nextInt(BORDER_LEFT, BORDER_RIGHT), STARTING_ENEMY_Y),
                                            new PVector(ZERO, ZERO), description, health, row);
        enemy.setVisible(false);
        root.getChildren().add(enemy);
        list.add(enemy);
        if(startSpeed) {
            enemy.setVelocity(new PVector(velocityX, velocityY));
        }
        else {
            enemy.setVelocity(new PVector(-velocityX, velocityY));
        }
    }

    /**
     * Initialize a new item.
     */
    void generateItems() {
        int damage = 0;
        String description = "";
        Item item;
        ImageView sprite = new ImageView();
        if(randomGenerator.nextDouble() < DEFAULT_PROBABILITY) {
            double probability = randomGenerator.nextDouble();
            if(probability < 0.9) {
                description = "Asteroid";
                damage = DEFAULT_COLLISION_DAMAGE;
                sprite = new ImageView(resources.get("Asteroid"));
            }
            else if(probability >= 0.9 && probability < 0.925) {
                description = "Shield";
                sprite = new ImageView(resources.get("Shield"));
            }
            else if(probability >= 0.925 && probability < 0.95) {
                description = "Sword";
                sprite = new ImageView(resources.get("Sword"));
            }
            else if(probability >= 0.95){
                description = "Like";
                damage = DEFAULT_HEALING;
                sprite = new ImageView(resources.get("Like"));
            }
            item = new Item(sprite, new PVector(randomGenerator.nextInt(BORDER_LEFT, BORDER_RIGHT), STARTING_ENEMY_Y),
                    new PVector(ZERO, defaultItemSpeed), description, damage);
            item.setVisible(false);
            root.getChildren().add(item);
        }
    }

    /**
     * Initialize a new missile.
     * @param ship the ship that is shooting
     * @param imageView the image of the missile
     * @param missileSpeed the speed of the missile
     * @param missileDamage the damage that is caused by the missile
     * @param description the description of the missile
     */
    void shootMissile(ShipSprite ship, ImageView imageView, double missileSpeed, int missileDamage, String description) {
        Item missile = new Item(  imageView, new PVector(ship.getTranslateX() + 25, ship.getTranslateY() - 10),
                new PVector(ZERO, missileSpeed), description, missileDamage);
        if(ship.getDescription().equals("PlayerShip")) {
            missile.setDamage(missile.getDamage() * boostMultiplier);
            missile.setVelocity(new PVector(ZERO, missile.getVelocity().y * boostMultiplier));
        }
        missile.setVisible(false);
        root.getChildren().add(missile);
    }

    /**
     * Check if the enemy has to shoot.
     */
    void enemiesShoot() {
        for (List<ShipSprite> enemy : enemies) {
            if(enemy.size() > 0 && randomGenerator.nextDouble() < DEFAULT_PROBABILITY) {
                ShipSprite shooter = enemy.get(randomGenerator.nextInt(enemy.size()));
                shootMissile(shooter, getMissileImageview(shooter), getMissileSpeed(shooter), getMissileDamage(shooter), "Alien_missile");
            }
        }
    }

    /**
     * Check a sprite to load the right missile image.
     * @param sprite the sprite that needs to be checked
     * @return the image of the missile
     */
    ImageView getMissileImageview(ShipSprite sprite) {
        if(sprite.getDescription().equals("EnemyShip1")) {
            return new ImageView(resources.get("Missile2"));
        }
        else {
            return new ImageView(resources.get("Missile3"));
        }
    }

    /**
     * Check a sprite to load the right missile speed.
     * @param sprite the sprite that needs to be checked
     * @return the speed of the missile
     */
    double getMissileSpeed(ShipSprite sprite) {
        if(sprite.getDescription().equals("EnemyShip1")) {
            return defaultMissileBlueSpeed;
        }
        else {
            return defaultMissileGreenSpeed;
        }
    }

    /**
     * Check a sprite to load the right missile damage.
     * @param sprite the sprite that needs to be checked
     * @return the damage that is caused by the missile
     */
    int getMissileDamage(ShipSprite sprite) {
        if(sprite.getDescription().equals("EnemyShip1")) {
            return DEFAULT_MISSILE1_DAMAGE;
        }
        else {
            return DEFAULT_MISSILE2_DAMAGE;
        }
    }

    /**
     * Check the collision between items and sprites.
     */
    void ItemManager() {
        if(playerShip.isAlive) {
            //  Alien missiles damage the player.
            alienMissiles.stream().filter(m -> m.intersects(playerShip)).forEach(m -> doDamagePlayer(m, m.getDamage()));
            //  Asteroids damage the player.
            asteroids.stream().filter(a -> a.intersects(playerShip)).forEach(a -> doDamagePlayer(a, a.getDamage()));
            //  Hearths heal the player.
            hearths.stream().filter(h -> h.intersects(playerShip)).forEach(h -> {
                sound.musicPowerUp();
                doDamagePlayer(h, h.getDamage());
            });
            //  Shields give invulnerability boosts to the player.
            shields.stream().filter(s -> s.intersects(playerShip)).forEach(s -> {
                sound.musicPowerUp();
                s.setAlive(false);
                scheduler.execute(shieldOn);
                shieldRemaining = TimeUnit.SECONDS.toNanos(DEFAULT_BOOST_LENGTH);
                shieldStart = System.nanoTime();
                scheduler.schedule(shieldOff, shieldRemaining, TimeUnit.NANOSECONDS);
            });
            //  Swords give damage boosts to the player.
            swords.stream().filter(s -> s.intersects(playerShip)).forEach(s -> {
                sound.musicPowerUp();
                s.setAlive(false);
                scheduler.execute(boostOn);
                swordRemaining = TimeUnit.SECONDS.toNanos(DEFAULT_BOOST_LENGTH);
                swordStart = System.nanoTime();
                scheduler.schedule(boostOff, swordRemaining, TimeUnit.NANOSECONDS);
            });
            //  Player missiles damage asteroids.
            shipMissiles.forEach(m -> asteroids.stream().filter(a -> a.intersects(m)).forEach(a -> {
                m.setAlive(false);
                a.setAlive(false);
                updateScore(points + (DEFAULT_POINT_GAIN - DEFAULT_POINT_REDUCTION));
            }));
            //  Player missiles damage aliens.
            for (List<ShipSprite> enemy : enemies) {
                shipMissiles.forEach(m -> enemy.stream().filter(e -> e.intersects(m)).forEach(e -> {
                    m.setAlive(false);
                    e.setHealth(e.getHealth() - m.getDamage());
                    if(e.health <= 0) {
                        newExplosion(e.getLocation());
                        e.setAlive(false);
                    }
                    updateScore(points + DEFAULT_POINT_GAIN);
                }));
            }
        }
    }

    /**
     * Check collision between player and aliens.
     */
    void contactManager() {
        if(playerShip.isAlive) {
            for (List<ShipSprite> enemy : enemies) {
                enemy.stream().filter(e -> e.intersects(playerShip)).forEach(e -> {
                    newExplosion(e.getLocation());
                    doDamagePlayer(e, DEFAULT_COLLISION_DAMAGE);
                });
            }
        }
    }

    /**
     * Handles player damage.
     * @param from the sprite that hits the player
     * @param damage the damage to be applied to the player
     */
    void doDamagePlayer(SpaceAttackSprite from, int damage) {
        from.setAlive(false);
        if(!invulnerable) {
            playerShip.setHealth(playerShip.getHealth() - damage);
        }
        if (playerShip.getHealth() <= ZERO) {
            newExplosion(playerShip.getLocation());
            playerShip.setAlive(false);
        } else {
            if (playerShip.getHealth() > DEFAULT_PLAYER_HEALTH) {
                playerShip.setHealth(DEFAULT_PLAYER_HEALTH);
            }
            updateHealthBar(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    "images" + "/like" + playerShip.health + "2.png"))));
        }
    }

    /**
     * Check for player death and shows the game over.
     */
    void dyingHandler() {
        if (!playerShip.isAlive() && !isDead) {
            MenuApplication.music.mediaPlayer.setMute(true);
            sound.musicGameOver();
            healthBar.setVisible(false);
            isDead = true;
            gameOver = new Label("GAME OVER\nPress N to play a new game\nPress Q to save your score");
            gameOver.setTextAlignment(TextAlignment.CENTER);
            gameOver.setLayoutX(365);
            gameOver.setLayoutY(300);
            gameOver.setFont(Font.font("System", 64));
            gameOver.setTextFill(Paint.valueOf("#19cf31"));
            root.getChildren().add(gameOver);
        }
    }

    /**
     * New game handler.
     */
    void newGame() {
        isDead = false;
        root.getChildren().remove(gameOver);
        resetGame();
    }

    /**
     * End game handler.
     */
    void endGame() {
        isWaiting = true; isPaused = true;
        primaryStage.setFullScreen(false);
        root.getChildren().remove(gameOver);
        Label scoreText = new Label("Save your score " + points);
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.setLayoutX(440);
        scoreText.setLayoutY(300);
        scoreText.setFont(Font.font("System", 64));
        scoreText.setTextFill(Paint.valueOf("#19cf31"));
        root.getChildren().add(scoreText);
        yourScore = new TextField();
        yourScore.setPromptText("Your name");
        yourScore.setFocusTraversable(false);
        yourScore.setLayoutX(365);
        yourScore.setLayoutY(400);
        yourScore.setAlignment(Pos.CENTER);
        yourScore.setFont(Font.font("System", 64));
        yourScore.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        yourScore.setStyle("-fx-text-inner-color: #19cf31; -fx-prompt-text-fill: #ffffff;");
        root.getChildren().add(yourScore);
    }

    /**
     * Reset game elements to prepare a new game.
     */
    void resetGame() {
        points = 0;
        enemies = new ArrayList<>(DEFAULT_ARRAY_CAPACITY);
        updateScore(0);
        updateHealthBar(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images" + "/like32.png"))));
        root.getChildren().removeIf(x -> (x instanceof SpaceAttackSprite));
        scheduler.execute(shieldOff);
        scheduler.execute(boostOff);
        initialize();
    }

    /**
     * Initialize a new explosion.
     * @param location the location of the explosion
     */
    void newExplosion(PVector location) {
        SpaceAttackSprite explosion = new SpaceAttackSprite(
                new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "images" + "/explosion.gif")))), location);
        explosion.setVisible(false);
        root.getChildren().add(explosion);
    }

    /**
     * Set velocity for the player moving right.
     */
    void moveRight() {
            playerShip.setVelocity(new PVector(DEFAULT_PLAYER_SPEED, ZERO));
    }

    /**
     * Set velocity for the player moving left.
     */
    void moveLeft() {
            playerShip.setVelocity(new PVector(-DEFAULT_PLAYER_SPEED, ZERO));
    }

    /**
     * Set velocity for the player moving up.
     */
    void moveUp() {
            playerShip.setVelocity(new PVector(ZERO, -DEFAULT_PLAYER_SPEED));
    }

    /**
     * Set velocity for the player moving down.
     */
    void moveDown() {
            playerShip.setVelocity(new PVector(ZERO, DEFAULT_PLAYER_SPEED));
    }

    /**
     * Check Y enemy position,
     * each enemy has to get to its row.
     */
    void checkEnemiesY() {
        int i = 0;
        for (List<ShipSprite> enemy : enemies) {
            int index = i;
            enemy.stream().filter(e -> e.getLocation().y >= ENEMY_ROWS[index]).toList()
                    .forEach(e -> e.setVelocity(new PVector(e.getVelocity().x, ZERO)));
            i++;
        }
    }

    /**
     * Check if enemies are out of left and right bound.
     */
    void checkEnemiesBounds() {
        for (List<ShipSprite> enemy : enemies) {
            enemy.stream().filter(e -> !checkLeftBound(e) || !checkRightBound(e)).toList()
                    .forEach(e -> e.setVelocity(new PVector(e.getVelocity().x * INVERT, e.getVelocity().y)));
        }
    }

    /**
     * Check if the player is out of bounds.
     */
    void checkPlayerBounds() {
        if (!checkDownBound(playerShip)) {
            playerShip.setVelocity(new PVector(playerShip.getVelocity().x, ZERO));
        }
        if(!checkLeftBound(playerShip)) {
            playerShip.setVelocity(new PVector(ZERO, playerShip.getVelocity().y));
        }
        if(!checkRightBound(playerShip)){
            playerShip.setVelocity(new PVector(ZERO, playerShip.getVelocity().y));
        }
        if(!checkUpBound(playerShip)){
            playerShip.setVelocity(new PVector(playerShip.getVelocity().x, ZERO));
        }
    }

    /**
     * Check right bound.
     * @param sprite the sprite whose bound is to be checked
     * @return true if the sprite is in the bound, false otherwise
     */
    boolean checkRightBound(ShipSprite sprite) {
        return !(sprite.getLocation().x >= BORDER_RIGHT);
    }

    /**
     * Check left bound.
     * @param sprite the sprite whose bound is to be checked
     * @return true if the sprite is in the bound, false otherwise
     */
    boolean checkLeftBound(ShipSprite sprite) {
        return !(sprite.getLocation().x <= BORDER_LEFT);
    }

    /**
     * Check up bound.
     * @param sprite the sprite whose bound is to be checked
     * @return true if the sprite is in the bound, false otherwise
     */
    boolean checkUpBound(ShipSprite sprite) {
        return !(sprite.getLocation().y <= BORDER_UP);
    }

    /**
     * Check down bound.
     * @param sprite the sprite whose bound is to be checked
     * @return true if the sprite is in the bound, false otherwise
     */
    boolean checkDownBound(ShipSprite sprite) {
        return !(sprite.getLocation().y >= BORDER_DOWN);
    }

    /**
     * Collect aliens in the correct list depending on their row.
     * @param row the row of the aliens
     * @return the filled list
     */
    List<ShipSprite> sprites(int row) {
        return root.getChildren().stream().filter(x -> x instanceof ShipSprite)
                .map(x -> (ShipSprite) x).filter(x -> x.getRow() == row).collect(Collectors.toList());
    }

    /**
     * Update the health bar based on the player health.
     * @param image the image that has to be set
     */
    void updateHealthBar(Image image) {
        healthBar.setImage(image);
        healthBar.setVisible(true);
    }

    /**
     * Update the score based on the player points.
     * @param newScore the score that has to be set
     */
    void updateScore(int newScore) {
        points = newScore;
        score.setText("SCORE: " + points);
    }

    /**
     * Fill sprites lists.
     */
    void fillLists() {
        sprites = sprites();
        shipMissiles = spritesItems("Missile");
        alienMissiles = spritesItems("Alien_missile");
        asteroids = spritesItems("Asteroid");
        hearths = spritesItems("Like");
        shields = spritesItems("Shield");
        swords = spritesItems("Sword");
        enemiesRed = root.getChildren().stream().filter(x -> x instanceof ShipSprite)
                .map(x -> (ShipSprite) x).filter(x -> x.getDescription().equals("EnemyShip2")).collect(Collectors.toList());
        for(int i = 0; i < enemies.size(); i++) {
            enemies.set(i, sprites(i + 1));
        }
    }
    /**
     * Collect sprites in a list.
     * @return the filled list
     */
    List<SpaceAttackSprite> sprites() {
        return root.getChildren().stream().filter(x -> x instanceof SpaceAttackSprite).map(x -> (SpaceAttackSprite) x).collect(Collectors.toList());
    }

    /**
     * Collect items in the correct list depending on their description.
     * @param description the description of the item
     * @return the filled list
     */
    List<Item> spritesItems(String description) {
        return root.getChildren().stream().filter(x -> x instanceof Item)
                .map(x -> (Item) x).filter(x -> x.getDescription().equals(description)).collect(Collectors.toList());
    }

    /**
     * Update and display sprites.
     */
    void UpdateDisplay() {
        sprites.forEach(Sprite::update);
        sprites.forEach(Sprite::display);
        sprites.forEach(s -> s.setVisible(true));
    }

    /**
     * Check if the player sprite needs to change.
     */
    void checkPlayerSprite() {
        if(playerShip.getVelocity().x == ZERO) {
            if(playerShip.getHealth() == DEFAULT_PLAYER_HEALTH) {
                playerShip.view.setImage(resources.get("PlayerShip"));
            }
            if(playerShip.getHealth() == 2) {
                playerShip.view.setImage(resources.get("PlayerShipDamaged1"));
            }
            if(playerShip.getHealth() == 1) {
                playerShip.view.setImage(resources.get("PlayerShipDamaged2"));
            }
        }
        if(playerShip.getVelocity().x > ZERO) {
            playerShip.view.setImage(resources.get("PlayerShip-Right"));
        }
        if(playerShip.getVelocity().x < ZERO) {
            playerShip.view.setImage(resources.get("PlayerShip-Left"));
        }
    }

    /**
     * Check if the red enemies sprites need to change.
     */
    void checkEnemiesSprite() {
        enemiesRed.forEach(e -> {
            if(e.getHealth() == 1) {
                e.view.setImage(resources.get("EnemyShip2Damaged"));
            }
        });
    }
}
