import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Server{
	int count = 1;
	int leftCounter = -999;  // reset value
	public ArrayList<ClientThread> ClientConnect = new ArrayList<ClientThread>();
	gameInfo moraGame = new gameInfo();
	TheServer server;
	private Consumer<Serializable> callback;
    public int port;
   
 
	Server(Consumer<Serializable> call, int portNum){
		callback = call;
		port = portNum;
		server = new TheServer();
		server.start();
	}
	
	public class TheServer extends Thread{
		
		public void runGame() {
			try(ServerSocket mysocket = new ServerSocket(port);){
			
		    while(true) {
				ClientThread count1 = new ClientThread(mysocket.accept(), count);
				ClientConnect.add(count1);
				
				// if-block to verify if there is 2 clients
				if(ClientConnect.size() == 2) {
					moraGame.samePlayers = true;
					callback.accept("Number of the Clients: " + ClientConnect.size() +	moraGame.printInfo());
				}
				count1.start();
				count++;
				
			    }
			}
			catch(Exception e) {
				callback.accept("Server socket launch failed to execute");
				}
			}		
		}
	
		class ClientThread extends Thread{
			
			Socket connection;
			int count;
			int arrayNum = 0;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
				this.arrayNum = count;
			}
			
			// clients listVIew update
			public void updateMultipleClients(String message) {
				for(int i = 0; i < ClientConnect.size(); i++) {
					ClientThread temp = ClientConnect.get(i);
					// catch for error is not do nothing
					try {
					 temp.out.reset();
					 temp.out.flush();
					 temp.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}
			
			// update client listview
			public void updateClient(String message, int givenClient) {
				ClientThread temp = ClientConnect.get(givenClient);
				try {
					 temp.out.reset();
					 temp.out.flush();
					 temp.out.writeObject(message);
					}
				catch(Exception e) {}
			}
			
			public void startGame(){
				// checking in and out connection
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams failed to execute");
				}
				
				// taking care of leaving server
				if(leftCounter == 1) {
					this.count = leftCounter;
				}
				
				else if (leftCounter == 2) {
					this.count = leftCounter;
				}
				
				updateMultipleClients("New player #" + count + " in the game");
				
				if(ClientConnect.size() == 2) {
					updateMultipleClients("Second player in the game, now choose and play.");
				}

				if(ClientConnect.size() != 2) {
					updateMultipleClients("requires two to play this game ");
				}
				
				leftCounter = -999;
				
				while(true) {
				    try {
				    	gameInfo morra = (gameInfo) in.readObject();
				    	
				    	// client 1
				    	if(count == 1) {
				    		moraGame.p1Predict = morra.clientPredictionChoose;
				    		moraGame.p1Fingers = morra.clientChoose;
				    		moraGame.player1intialSubmission = morra.clientToServerSent;
				    	}
				    	
				    	// client 2
				    	else if(count == 2) {
				    		moraGame.p2Predict  = morra.clientChoose;
				    		moraGame.p2Fingers = morra.clientChoose;
				    		moraGame.player2intialSubmission = morra.clientToServerSent;
				    	}
				    	
				    	// update listView, updateClients 
				    	if((ClientConnect.size() == 2) && (moraGame.player2intialSubmission) && (moraGame.player1intialSubmission)) {
				    		moraGame.calcualteWinProbabilty(moraGame.p1Fingers, moraGame.p2Fingers);
				    		callback.accept("Number of Players: " + ClientConnect.size() +
								moraGame.printInfo());
				    		
					    	updateMultipleClients("Player 1 had " + moraGame.p1Fingers + " fingers " + "& predicted " + moraGame.p1Predict);
					    	updateMultipleClients("Player 2 had " + moraGame.p2Fingers + " fingers " + "& predicted " + moraGame.p2Predict );
					    	updateMultipleClients("Player 1 Points is : " + moraGame.player1Points + " Player 2 Points is: " + moraGame.player2Points);
					    	
					    	// reset next game
					    	moraGame.player1intialSubmission = false;
					    	moraGame.player2intialSubmission = false;
				    	}
				    	
				   
				    	// winner 
				    	if(moraGame.player1Points == 2) {
				    		callback.accept("Gameover Has Ended.....\nPlayer 1 Won\n");
				    		updateMultipleClients("Gameover Has Ended.....\\nPlayer 1 Won\\n");
				    	}
				    	
				    	//announce who won
				    	if(moraGame.player2Points == 2) {
				    		callback.accept("Gameover Has Ended.....\\nPlayer 2 Won\\n");
				    		updateMultipleClients("Gameover Has Ended.....\\nPlayer 2 Won\\n");
				    	}
			    	}
				    catch(Exception e) {
				    	leftCounter = count;
				    	moraGame.resetGame();
				    	callback.accept("Player: " + count + " disconnected or failed to connect ... require 2 Players");
				    	if(count == 2) {
				    		updateMultipleClients("Player "+count+" has left the server!\n" + "So we are waiting for Player 2.");
				    	}
				    	else if(count == 1){
				    		updateMultipleClients("Player "+count+" has left the server!\n" + "So we are waiting for Player 1.");
				    	}
				    	ClientConnect.remove(this);
				    	break;
				    }
				}
			}
			
			
		}
}


	
	

	
