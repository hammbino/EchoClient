import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient {
    private String userName = "Anonymous";
    private String hostName = "localhost";
    private int portNumber = 4688;
    private PrintWriter out;
    private JFrame frame;
    private JTextField textField = new JTextField(30);
    private JTextArea messageArea = new JTextArea(8, 40);

    private EchoClient() {
        // Layout GUI
        JPanel displayPanel = new JPanel();
        displayPanel.add(new JScrollPane(messageArea));
        JButton disconnectButton = new JButton("Disconnect");
        JPanel inputPanel = new JPanel();
        inputPanel.add(textField);
        inputPanel.add(disconnectButton);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        frame = new JFrame("Chat Client");
        frame.add(displayPanel);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.pack();

        // Add Listeners
        textField.addActionListener(event -> {
            String userOutput = textField.getText();
            out.println(userOutput);
            writeToMessageArea(userOutput);
//            messageArea.append("me " + userOutput + "\n");
            textField.setText("");
        });

        disconnectButton.addActionListener(event -> {
            out.println("disconnect " + userName);
            System.exit(0);
        });
    }

    private synchronized void writeToMessageArea(String s) {
        messageArea.append(s + "\n");
    }

    private void run(String args[]) throws IOException {
        // Make connection and initialize streams
        if (args.length > 0) {
            hostName = args[0];
            if(args.length > 1) {
                portNumber = Integer.parseInt(args[1]);
            }
        }

        Socket echoSocket = new Socket( hostName, portNumber );
        Scanner in = new Scanner(new InputStreamReader(echoSocket.getInputStream()));
        out = new PrintWriter( echoSocket.getOutputStream(), true );
        out.println (  "connect " + userName );

        Thread serverInput = new Thread() {
            public void run() {
                String serverInputString;
                while ( in.hasNextLine() ) {
                    serverInputString = in.nextLine();
                    writeToMessageArea(serverInputString);
//                    messageArea.append(serverInputString + "\n");
                }
            }
        };
        serverInput.start();
    }

    public static void main(String[] args) throws IOException {
        EventQueue.invokeLater(() -> {
            EchoClient client = new EchoClient();
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setLocationByPlatform(true);
            client.frame.setVisible(true);
            try {
                client.run(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}