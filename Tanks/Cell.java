import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Cell {
    private boolean canBroken, bulletCanPass, tankCanPass, canHide;
    private int cellHP = 4;
    private Image image;

    public Cell(String imageSrc, boolean canBroken, boolean bulletCanPass, boolean tankCanPass, boolean canHide){
        try{
            this.image = new Image(imageSrc, Game.CELL_SIZE, Game.CELL_SIZE, true, true);
        }catch (Exception e) {e.printStackTrace();}
        this.canBroken = canBroken;
        this.bulletCanPass = bulletCanPass;
        this.tankCanPass = tankCanPass;
        this.canHide = canHide;
    }

    public int destroy(){
        if(canBroken && cellHP > 0)
            cellHP--;
        return cellHP;
    }

    public void draw(GraphicsContext gc, int x, int y, int size){
        // gc.drawImage(image, x*size, y*size, size, size);
        gc.setFill(new ImagePattern(image));
        gc.fillRect(x*size, y*size, size, size);
    }

    public boolean isCanBroken() {return canBroken;}
    public boolean isBulletCanPass() {return bulletCanPass;}
    public boolean isTankCanPass() {return tankCanPass;}
    public boolean isCanHide() {return canHide;}
    public int getCellHP() {return cellHP;}
}

