package main.java.server ;
import java.net.ServerSocket ;
import java.net.Socket ;

public final class WebServer {

    public static void main(String argv[]) throws Exception {
        
        int port               = 8080 ;
        ServerSocket WebSocket = new ServerSocket(port) ;

        while (true) {
            // Listen for a TCP connection request.
            Socket connectionSocket = WebSocket.accept() ;
            // Contruct object to process HTTP request message.
            HttpRequest request     = new HttpRequest(connectionSocket) ;
            Thread thread           = new Thread(request) ;
            thread.start() ;
        }

    }

}