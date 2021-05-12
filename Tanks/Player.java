public interface Player {
    Position getPosition();
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();
    void setMap(Map map);

}
