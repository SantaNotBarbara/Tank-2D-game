import java.net.*;
import java.io.*;
import javafx.stage.Stage;
import javafx.application.Application;

public class Server{
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;

    Server(String[] args){
         try{
            server = new ServerSocket(5000);
            socket = server.accept();
            Tank player = new Tank();
            
            new Thread() {
                @Override
                public void run() {
                    Application.launch(Game.class);
                }
            }.start();
            System.out.println("Client accepted");
            Game game = Game.waitForGame();
            game.setMultiplayer(player);


            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            char line = 'l';
            while (true){
                try{
                    String s = in.readUTF();
                    line = s.charAt(0);
                    if(line == 'e'){
                        break;
                    }else if (line == 's') {
                        game.addBullet(new Bullet(player, game.map));
                    }else{
                        player.setDirection(line);
                    }
                   
                }
                catch(IOException i){/*i.printStackTrace();
                    break;*/}
            }
            System.out.println("Closing connection");
  
            socket.close();
            in.close();
        }
        catch(Exception i){   i.printStackTrace(); }
    }

    public static void main(String args[]){
       Server server = new Server(args);
    }
}