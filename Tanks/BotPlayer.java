import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.util.*;

public class BotPlayer extends Tank implements Player {
    private int speed = 2;
    private int curState = 0;
    private Position position;
    private Game game;
    private Map map;
    private int botLife = 5;
    private boolean alive = true; 
    private Image imgUp, imgDown, imgLeft, imgRight;
    private boolean canFire = true;
    private int fireDelay = 10;
    

    public BotPlayer(Map map, Game game) {
        this.game = game;
        setMap(map);
        try{
            imgUp = new Image("img/bot_up.png", Game.CELL_SIZE, Game.CELL_SIZE, true, true);
            imgDown = new Image("img/bot_down.png", Game.CELL_SIZE, Game.CELL_SIZE, true, true);
            imgLeft = new Image("img/bot_left.png", Game.CELL_SIZE, Game.CELL_SIZE, true, true);
            imgRight = new Image("img/bot_right.png", Game.CELL_SIZE, Game.CELL_SIZE, true, true);
        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void setMap(Map map) {
        this.map = map;
        
        boolean check = false;
        while(!check) {
            int x = (int)(Math.random() * map.getMapSize());
            int y = (int)(Math.random() * map.getMapSize());
            if (map.getCellAt(x, y) instanceof NormalCell && map.canPass[y][x]) {
                this.position = new Position(x, y);
                map.canPass[y][x] = false;
                check = true;
            }
        }
    }

    @Override
    public Position getPosition() {return position;}

    public void getHit(){
        if (alive) {
            botLife--; 
            if (botLife == 0){
                int x = position.getX();
                int y = position.getY();
                map.updateValueAt(x,y, true);
                alive = false;
            }
        }
    }
    public boolean isAlive(){return alive;}

     @Override
    public void moveLeft() {
        int oldX = position.getX();
        int oldY = position.getY();

        int x = position.getX()-1;
        int y = position.getY();
        direction = 'l';
        if(canMove(x, y)){   
            position.setX(x);
            position.setY(y);

            map.updateValueAt(oldX,oldY, true);
            map.updateValueAt(x,y, false);
        }
    }

    @Override
    public void moveUp() {
        int oldX = position.getX();
        int oldY = position.getY();

        int y = position.getY()-1;
        int x = position.getX();
        direction = 'u';
        if(canMove(x, y)){   
            position.setX(x);
            position.setY(y);

            map.updateValueAt(oldX,oldY, true);
            map.updateValueAt(x,y, false);
        }
    }

    @Override
    public void moveRight() {
        int oldX = position.getX();
        int oldY = position.getY();

        int x = position.getX()+1;
        int y = position.getY();
        direction = 'r';
        if(canMove(x, y)){   
            position.setX(x);
            position.setY(y);

            map.updateValueAt(oldX,oldY, true);
            map.updateValueAt(x,y, false);
        }
        
    }

    @Override
    public void moveDown() {
        int oldX = position.getX();
        int oldY = position.getY();
        
        int y = position.getY()+1;
        int x = position.getX();
        direction = 'd';
        if(canMove(x, y)){   
            position.setX(x);
            position.setY(y);

            map.updateValueAt(oldX,oldY, true);
            map.updateValueAt(x,y, false);
        }
        
    }

    private boolean canMove(int x, int y) {
        if(!map.isValidCoordinate(x, y))
            return false;
        if(map.getCellAt(x, y).isTankCanPass() && map.canPass[y][x]){
            isHidden = map.getCellAt(x, y).isCanHide();
            return true;
        }
        return false;
    }

    public void update(GraphicsContext gc){
        if (fireDelay == 0) {
            fireDelay = 10;
            canFire = true;
        }

        int py = game.getTankPosition().getY();
        int px = game.getTankPosition().getX();

        if (canFire && 
            ((direction == 'l' && position.getY() == py && position.getX() > px) ||
            (direction == 'r' && position.getY() == py && position.getX() < px) ||
            (direction == 'u' && position.getX() == px && position.getY() > py) ||
            (direction == 'd' && position.getX() == px && position.getY() < py) )){
            game.addBullet(new Bullet(this, map));
            canFire = false;
        }
        else{
            fireDelay--;
            if (curState == speed) {
                if (!game.getTank().isHidden) 
                    findPath(); 
                curState=0;
            }else {
                curState++;
            }
            
        }

        if(!super.isHidden){ 
            int x = position.getX() * Game.CELL_SIZE;
            int y = position.getY() * Game.CELL_SIZE;

            switch (super.direction) {
                case 'l':
                    gc.drawImage(imgLeft, x, y, Game.CELL_SIZE, Game.CELL_SIZE);
                    break;
                case 'd':
                    gc.drawImage(imgDown, x, y, Game.CELL_SIZE, Game.CELL_SIZE);
                    break;
                case 'r':
                    gc.drawImage(imgRight, x, y, Game.CELL_SIZE, Game.CELL_SIZE);
                    break;
                case 'u':
                    gc.drawImage(imgUp, x, y, Game.CELL_SIZE, Game.CELL_SIZE);
                    break;
            }
        }
    }

    private void findPath(){
        int size = map.getMapSize();

        int[][] path = new int[size+2][size+2];
        int sx=0, sy=0;

        for (int[] row : path) 
            Arrays.fill(row, 100);
        
        boolean found = false;
        int st = -1;
        /*mark player*/
        sy = game.getTankPosition().getY()+1;
        sx = game.getTankPosition().getX()+1;
        path[sy][sx]=1;
        /*mark bot*/
        sy = position.getY()+1;
        sx = position.getX()+1;

        int t=0; 
        while(!found && t<size*size) {
            for (int y=1; y<size+1 && !found; y++) 
                for (int x=1; x<size+1 && !found; x++) {
                    if(x == sx && y == sy && path[y][x] == 1){
                        found = true;
                        st = path[y][x];
                        break;
                    }
                    if(!map.getCellAt(x-1, y-1).isTankCanPass()  || path[y][x] < 100) continue;
                    path[y][x] = Math.min(Math.min(path[y-1][x], path[y+1][x]), Math.min(path[y][x-1], path[y][x+1])) + 1;
                    if(x == sx && y == sy && path[y][x] < 100){
                        found =true;
                        st = path[y][x];
                        break;
                    }
                }

            t++;
        }
        if (found) {
            if(sx>1 && path[sy][sx-1] == st-1)  
                moveLeft();
            else if(sx<size && path[sy][sx+1] == st-1) 
                moveRight();
            else if(sy>1 && path[sy-1][sx] == st-1) 
                moveUp();
            else if(sy<size && path[sy+1][sx] == st-1) 
                moveDown();
        }
    }

}
