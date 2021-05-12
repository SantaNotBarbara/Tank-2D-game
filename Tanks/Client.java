import java.net.*;
import java.io.*;
  
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Client extends Application{
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
  
    public void start(Stage stage){
        try{
            socket = new Socket("127.0.0.1", 5000);
            System.out.println("Connected");
  
            input  = new DataInputStream(System.in);
            out    = new DataOutputStream(socket.getOutputStream());
        }catch(UnknownHostException u){u.printStackTrace();}
        catch(IOException i){i.printStackTrace();}
  
        
        Scene scene = new Scene(new Pane(), 400, 400);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                try{
                    switch(keyEvent.getCode()){
                        case UP:
                            out.writeUTF("u");
                            break; 
                        case DOWN: 
                            out.writeUTF("d");
                            break; 
                        case RIGHT: 
                            out.writeUTF("r");
                            break; 
                        case LEFT: 
                            out.writeUTF("l");
                            break; 
                        case NUMPAD5: 
                            out.writeUTF("s");
                            break; 
                        case ESCAPE:
                            out.writeUTF("e");
                            input.close();
                            out.close();
                            socket.close();
                            break;
                    }
                }catch(IOException i){System.out.println(i);}
            }
        });
        stage.setScene(scene);
        stage.show();
    }
  
    public static void main(String args[]){
        launch();
    }
}