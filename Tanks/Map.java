import javafx.scene.canvas.GraphicsContext;

import java.util.Scanner;

public class Map {
    private Cell[][] map;
    public boolean[][] canPass;
    private int mapSize;
    private Position playerPosition;
    private Game game;

    Map(Scanner scan, Game game) throws Exception{
        this.game = game;

        mapSize = scan.nextInt();
        if(mapSize == 0){
            throw new InvalidMapException("Map size can not be zero");
        }

        map = new Cell[mapSize][mapSize];
        canPass = new boolean[mapSize][mapSize];

        for(int i = 0; i < mapSize; i++){
            for(int j = 0; j < mapSize; j++){
                char a = scan.next().charAt(0);
                canPass[i][j] = true;

                if(a == 'P'){
                    playerPosition = new Position(j, i);
                    canPass[i][j] = false;
                }

                if(a != '0' && a != 'T' && a != 'B' && a != 'S' && a != 'W' && a != 'P')
                    throw new InvalidMapException("Not enough map elements or invalid input");
                switch(a){
                    case 'S':   map[i][j] = new SteelCell();    break;
                    case 'W':   map[i][j] = new WaterCell();    break;
                    case 'T':   map[i][j] = new TreeCell();     break;
                    case 'B':   map[i][j] = new BrickCell();    break;
                    default:    map[i][j] = new NormalCell(); break;
                }
                
            }
        }
    }

    public void draw(GraphicsContext gc){
        for(int i = 0; i < mapSize; i++)
            for(int j = 0; j < mapSize; j++)
                map[i][j].draw(gc, j, i, Game.CELL_SIZE);
    }

    public void destroyCell(int x, int y){
        if (isValidCoordinate(x, y)) {
            if (map[y][x] instanceof NormalCell || map[y][x] instanceof TreeCell) {
                game.killAt(new Position(x, y));
            }else if(map[y][x].destroy() == 0){
                canPass[y][x] = true;
                map[y][x] = new NormalCell();
            }
        }

        /*if(isValidCoordinate(x, y) && map[y][x].destroy() == 0) 
            map[y][x] = new NormalCell();*/
    }

    public boolean isValidCoordinate(int x, int y){
        return (x >= 0 && x < mapSize) && (y >= 0 && y < mapSize);
    }

    public Position getPlayerPosition(){ return playerPosition; }
    public int getMapSize(){    return mapSize; }
    public Cell getCellAt(int x, int y){    return map[y][x];}

    public void updateValueAt(int x, int y, boolean to){ canPass[y][x] = to; }

}
