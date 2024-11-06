import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SMTPServer {
    private static final int SMTP_PORT = 25; // SMTP 默認端口

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SMTP_PORT)) {
            System.out.println("SMTP Server is running and waiting for connections...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
             
            // 發送 SMTP 歡迎訊息
            out.println("220 Welcome to Simple SMTP Server");

            String clientMessage;
            boolean quit = false;

            while ((clientMessage = in.readLine()) != null && !quit) {
                System.out.println("Received: " + clientMessage);

                if (clientMessage.startsWith("HELO")) {
                    out.println("250 Hello " + clientMessage.substring(5));
                } else if (clientMessage.startsWith("MAIL FROM:")) {
                    out.println("250 OK");
                } else if (clientMessage.startsWith("RCPT TO:")) {
                    out.println("250 OK");
                } else if (clientMessage.startsWith("DATA")) {
                    out.println("354 End data with <CR><LF>.<CR><LF>");
                    StringBuilder emailData = new StringBuilder();
                    String line;
                    while (!(line = in.readLine()).equals(".")) {
                        emailData.append(line).append("\n");
                    }
                    System.out.println("Email received: \n" + emailData.toString());
                    out.println("250 OK, email received");
                } else if (clientMessage.equals("QUIT")) {
                    out.println("221 Bye");
                    quit = true;
                } else {
                    out.println("500 Command not recognized");
                }
            }

            clientSocket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
