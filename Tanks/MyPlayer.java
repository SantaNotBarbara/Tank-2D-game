import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MyPlayer implements Player{
    public Position position;
    private Map map;
    protected boolean isHidden = false;
    protected char direction = 'l'; 

    public MyPlayer(){
    }

    public MyPlayer(Position position){
        this.position = position;
    }

    @Override
    public void setMap(Map map) {this.map = map;}

    @Override
    public Position getPosition() {return position;}

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

    public char getDirection(){
        return this.direction;
    }
}

