package com.example.chatapp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;

    public Client() {
    	//Create Frame
        frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        // Create TextArea
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        //Adding ChatArea to Frame
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        //Create Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        //Create MessageField
        messageField = new JTextField();
        //Add MessageField in input panel
        inputPanel.add(messageField, BorderLayout.CENTER);
        //Create Send Button
        JButton sendButton = new JButton("Send");
        //Add Button to input panel
        inputPanel.add(sendButton, BorderLayout.EAST);
        // Add all in frame
        frame.add(inputPanel, BorderLayout.SOUTH);
        // Put action when send button click
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Add a KeyListener to the messageField
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        frame.setVisible(true);
    }
    public void run() {
        try {
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

        } catch (IOException e) {
            shutdown();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    public void shutdown() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
        } catch (IOException e) {
            // Handle the exception
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String inMessage = in.readLine();
                    if (inMessage == null) {
                        shutdown();
                        break;
                    }
                    chatArea.append(inMessage + "\n");
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Client client = new Client();
                client.run();
            }
        });
    }
}
