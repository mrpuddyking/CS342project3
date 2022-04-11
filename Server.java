import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

    int count = 1;
    int port;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;


    Server( int portnumber, Consumer<Serializable> call){

        port = portnumber;
        callback = call;
        server = new TheServer();
        server.start();
    }


    public class TheServer extends Thread{

        public void run() {

            try(ServerSocket mysocket = new ServerSocket(port);){
                System.out.println("Server is waiting for a client!");


                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    if ( clients.size() < 2) {
                        callback.accept("client has connected to server: " + "client #" + count);
                        clients.add(c);
                        c.start();
                        count++;
                    } else {
                        callback.accept("Someone else tried to join but the max is two players.");
                    }
                }
            }
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }


    class ClientThread extends Thread{


        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        public void updateClients(String message) {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(message);
                }
                catch(Exception e) {}
            }
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }


            updateClients("new client on server: client #" + count);

            if ( clients.size() < 2){
                updateClients("You're the only one connected. Game will not be started");
            }
            else if ( clients.size() == 2){
                updateClients("Game will be started.");
            }
            else{
                updateClients("Too many players");
//					updateClients("Client #"+count+" has been removed due to excessive numbers!");
//					clients.remove(this);
                throw new RuntimeException();
            }

            while(true) {
                try {
                    String data = in.readObject().toString();
                    String pguess = data.substring(0,1);
                    String ppick = data.substring(2,3);

                    callback.accept("client: " + count + " guessed " + pguess);
                    callback.accept("client: " + count + " picked " + ppick);

                }
                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    updateClients("Client #"+count+" has left the server!");
                    clients.remove(this);
                    break;
                }
            }
        }//end of run


    }//end of client thread
}






