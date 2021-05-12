import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

import java.util.*;
import java.net.*;
import java.io.*;

public class Game extends Application {
    private Scene scene;
    private Stage stage;
    protected Map map;
    private static String mapSrc = "Map.txt";
    private ArrayList<BotPlayer> bots = new ArrayList<>();
    private Tank player;
    private Tank client;
    private boolean isMultiplayer = false;

    private List<Bullet> bulletList = new LinkedList<>();
    public static int CELL_SIZE = 32;

    public static final CountDownLatch latch = new CountDownLatch(1);
    public static Game game = null;

    private GraphicsContext gc;

    public static Game waitForGame() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return game;
    }


    public Game() {
        setGame(this);
    }
    public static void setGame(Game game0) {
        game = game0;
        latch.countDown();
    }

    private void initialize() throws Exception{

        Pane root = new Pane();
        scene = new Scene(root);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch(keyEvent.getCode()){
                    case W:
                        player.setDirection('u');
                        break; 
                    case S: 
                        player.setDirection('d');
                        break; 
                    case D: 
                        player.setDirection('r');
                        break; 
                    case A: 
                        player.setDirection('l');
                        break; 
                    case SPACE: 
                        addBullet(new Bullet(player, map));
                        break; 
                }
            }
        });

        try(Scanner scan = new Scanner(new File(Game.mapSrc))){
            map = new Map(scan, this);
        }
        int mapSize = map.getMapSize();

        player = new Tank(map.getPlayerPosition());
        player.setMap(map);
        if (isMultiplayer) {
            client.setMap(map, true);    
        }
        

        Canvas canvas = new Canvas(mapSize*Game.CELL_SIZE, mapSize*Game.CELL_SIZE);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add( canvas );
    }

    public void addBullet(Bullet bullet){
        bulletList.add(bullet);
    }

    public void setMap(String s){
        Game.mapSrc = s;
    }

    public void addPlayer(Tank player){
        player.setMap(map);
    }

    public void start(Stage stage) throws Exception {

        this.stage = stage;
        initialize();

        AnimationTimer timer = new AnimationTimer() {
            private long last = 0;
            private long last_bot = 0;
            public void handle(long now){
                if(now - last >= 100_000_000){
                    if (!player.isAlive()) {
                        stage.close();
                        System.out.println("Player is DEAD!");
                        this.stop();
                    }

                    map.draw(gc);
                    player.update(gc);
                    if (isMultiplayer) {
                        if (!client.isAlive()) {
                            stage.close();
                            System.out.println("Client is DEAD!");
                            this.stop();
                        }
                        client.update(gc);
                    }

                    for (BotPlayer bot : bots) {
                        if (bot.isAlive()) 
                            bot.update(gc);
                    }
                    
                    //update bullets
                    for(Bullet bullet : bulletList){
                        bullet.updateBullet(gc);
                        if(!bullet.isAlive())
                            bulletList.remove(bullet);
                    }

                    last = now;
                }
                if(now - last_bot >= 10_000_000_000l){
                    BotPlayer bot = new BotPlayer(map, game);
                    bots.add(bot);
                    last_bot = now;
                }
            }
        };
        timer.start();

        stage.setTitle("Tanker - Project 3" );
        stage.setScene(scene);
        stage.show();
    }

    public void killAt(Position pos){
        if (pos.equals(player.getPosition())) {
            player.getHit();
        }else if (isMultiplayer && pos.equals(client.getPosition())) {
            client.getHit();
        }else{
            for (BotPlayer bot : bots) 
                if (bot.isAlive() && pos.equals(bot.getPosition())) 
                    bot.getHit();
            
        }
        
    }

    public Position getTankPosition(){
        return player.getPosition();
    }
    public Tank getTank(){
        return player;
    }
    
    public static void main(String[] args) {
        mapSrc = args[0];
        launch(args);
    }

    public void setMultiplayer(Tank tank){
        client = tank;
        isMultiplayer = true;
    }
}
