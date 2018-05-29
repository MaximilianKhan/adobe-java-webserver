package main.java.server ;
import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class HttpRequest implements Runnable {

final static String CRLF = "\r\n" ;
Socket socket ;

// Constructor
public HttpRequest(Socket socket) throws Exception {
    this.socket = socket ;
}

// Run Function
public void run() {

    try {
        processRequest() ;
    } catch (Exception e) {
        System.out.println(e) ;
    }

}

// Process Request Function
private void processRequest() throws Exception {

    InputStream is      = socket.getInputStream() ;
    DataOutputStream os = new DataOutputStream(socket.getOutputStream()) ;

    // Setup input stream filters.
    BufferedReader br  = new BufferedReader(new InputStreamReader(is)) ;
    String requestLine = br.readLine() ;

    System.out.println() ;
    System.out.println(requestLine) ;

    // Obtain the IP address of the incoming connection.
    InetAddress incomingAddress = socket.getInetAddress() ;
    String ipString             = incomingAddress.getHostAddress() ;

    System.out.println("The incoming address is: " + ipString) ;

    // Extract the filename through using StringTokenizer.
    StringTokenizer tokens = new StringTokenizer(requestLine) ;
    tokens.nextToken() ; // Skip over the method, which should be GET.
    String fileName        = tokens.nextToken() ;

    // Prepend a "." so that the file request is within the current directory.
    fileName = "." + fileName ;
    String headerLine = null ;
    while ( (headerLine = br.readLine()).length() != 0 ) {
        System.out.println(headerLine) ;
    }

    // Open the requested file.
    FileInputStream fis = null ;
    boolean fileExists  = true ;

    try {
        fis = new FileInputStream(fileName) ;
    } catch (FileNotFoundException e) {
        fileExists = false ;
    }

    // Construct the response message.
    String statusLine      = null ;
    String contentTypeLine = null ;
    String entityBody      = null ;

    if ( fileExists ) {
        statusLine      = "HTTP/1.1 200 OK: " ;
        contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF ;
    } else {
        statusLine      = "HTTP/1.1 404 Not Found: " ;
        contentTypeLine = "Content-Type: text/html" + CRLF ;
        entityBody      = "<!doctype html><html lang='en'><head><title>NOT FOUND</title><meta charset='UTF-8'></head> <body><h1>NOT FOUND</h1></body> </html>" ;
    }

    // End of response message construction.
    os.writeBytes(statusLine) ;
    os.writeBytes(contentTypeLine) ;
    os.writeBytes(CRLF) ;

    if ( fileExists ) {
        sendBytes(fis, os) ;
        fis.close() ;
    } else {
        os.writeBytes(entityBody) ;
    }

    // Close streams and sockets.
    os.close() ;
    br.close() ;
    socket.close() ;

}

// Declare sendBytes() called in processRequest().
private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
    // Construct a 1K buffer to hold bytes on their way to the socket.
    byte[] buffer = new byte[1024] ;
    int bytes     = 0 ;

    // Copy requested file into the socket's output stream.
    while ( (bytes = fis.read(buffer)) != -1 ) {
        os.write(buffer, 0, bytes) ;
    }

}

private static String contentType(String fileName) {

    if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
        return "text/html" ;
    if (fileName.endsWith(".jpg"))
        return "text/jpg" ;
    if (fileName.endsWith(".gif"))
        return "text/gif" ;
    return "application/octet-stream" ;

}

}