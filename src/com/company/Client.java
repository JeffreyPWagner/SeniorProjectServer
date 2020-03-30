package com.company;

import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The Client class holds the information needed to communicate with each connected client.
 */
public class Client {

    // A stream to receive results from the clients
    private DataInputStream dataInputStream;

    // A stream to sent data to clients for processing
    private ObjectOutputStream objectOutputStream;

    /**
     * The constructor takes in the TCP socket for the client and creates the streams.
     * @param socket the TCP socket for the client
     */
    public Client(Socket socket) {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the input stream
     * @return the input stream
     */
    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    /**
     * Getter for the output stream
     * @return the output stream
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
