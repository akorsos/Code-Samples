import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public final class Server {
    private final int serverPort;
    private ServerSocket socket;
    private DataOutputStream toClientStream;
    private BufferedReader fromClientStream;
    private StringBuffer stringBuffer;

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Creates a socket + binds to the desired server-side port #.
     *
     * @throws {@link IOException} if the port is already in use.
     */
    public void bind() throws IOException {
        socket = new ServerSocket(serverPort);
        System.out.println("Server bound and listening to port " + serverPort);
    }

    /**
     * Waits for a client to connect, and then sets up stream objects for communication
     * in both directions.
     *
     * @return {@code true} if the connection is successfully established.
     * @throws {@link IOException} if the server fails to accept the connection.
     */
    public boolean acceptFromClient() throws IOException {
        Socket clientSocket;
        try {
            clientSocket = socket.accept();
        } catch (SecurityException e) {
            System.out.println("The security manager intervened; your config is very wrong. " + e);
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Probably an invalid port number. " + e);
            return false;
        }

        toClientStream = new DataOutputStream(clientSocket.getOutputStream());
        fromClientStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return true;
    }

    public void echoLoop() throws IOException {
        while (true){
            stringBuffer = new StringBuffer();
            String line = new String();
            while ((line = fromClientStream.readLine()) != null && (line.length() != 0)) {
                System.out.println(line);
                stringBuffer.append(line + "\r\n");
            }
            if(stringBuffer.length() != 0){
                Request request = new Request(stringBuffer.toString());
                System.out.print("Request received: " + request.toString());
                Response response = new Response(request);
                System.out.print("Sending response: " + response.toString());
                byte[] responseBytes = response.convertResponse();
                toClientStream.write(responseBytes);

                System.out.println("\nResponse has been sent. Continuing to listen for more requests.");
            }
        }

    }

    public static void main(String argv[]) {
        Map<String, String> flags = Utils.parseCmdlineFlags(argv);
        if (!flags.containsKey("--serverPort")) {
            System.out.println("usage: Server --serverPort=12345");
            System.exit(-1);
        }

        int serverPort = -1;
        try {
            serverPort = Integer.parseInt(flags.get("--serverPort"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number! Must be an integer.");
            System.exit(-1);
        }

        Server server = new Server(serverPort);
        try {
            server.bind();
            if (server.acceptFromClient()) {
                server.echoLoop();
            } else {
                System.out.println("Error accepting client connection.");
            }
        } catch (IOException e) {
            System.out.println("Error communicating with client. aborting. Details: " + e);
        }
    }

//    private static boolean incompleteRequest(StringBuffer stringBuffer) {
//        if (stringBuffer.length() < 4) {
//            return true;
//        }
//
//        return !stringBuffer.substring(stringBuffer.length() - 4, stringBuffer.length()).equals("\r\n\r\n");
//    }
}

