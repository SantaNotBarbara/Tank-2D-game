import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private Tank owner;
    private Position position;
    private Map map;
    private int speed;
    private char direction;
    private boolean isAlive = true;

    public Bullet(Tank player, Map map){
        owner = player;
        this.map = map;
        position = new Position(owner.getPosition().getX(), owner.getPosition().getY());
        direction = owner.getDirection();
    }

    public void updateBullet(GraphicsContext gc){
        int x = position.getX();
        int y = position.getY();
        switch (direction){
            case 'l':   goTo(x-1, y); break;
            case 'u':   goTo(x, y-1); break;
            case 'd':   goTo(x, y+1); break;
            case 'r':   goTo(x+1, y); break;
        }

        gc.setFill(Color.BLACK);
        double radius = Game.CELL_SIZE / 4;
        x = position.getX() * Game.CELL_SIZE + 3 * Game.CELL_SIZE / 8;
        y = position.getY() * Game.CELL_SIZE + 3 * Game.CELL_SIZE / 8;
        gc.fillOval(x , y, radius, radius);
    }

    private void goTo(int x, int y){


        if(!canMoveTo(x, y)){
            map.destroyCell(x, y);
            isAlive = false;
            owner.setFire();
            return;
        }
        position.setX(x);
        position.setY(y);
    }

    private boolean canMoveTo(int x, int y){
        if(!map.isValidCoordinate(x, y) || !map.canPass[y][x])    return false;
        
        Cell targetCell = map.getCellAt(x, y);
        if(targetCell.isBulletCanPass())
            return true;
        return false;
    }

    public void setMap(Map map){    this.map = map;}
    public boolean isAlive(){ return isAlive; }
    public Position getPosition(){  return position;}
}
