package lab6.client;

import lab6.common.network.NetworkException;
import lab6.common.network.PacketAssembler;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Handles UDP network communication with packet fragmentation/reassembly support
 */
public class NetworkManager implements AutoCloseable {
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_PACKET_DATA_SIZE = BUFFER_SIZE - 8;

    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private final PacketAssembler packetAssembler;
    private final SocketAddress serverSocketAddress;

    /**
     * Initializes network communication with target server
     *
     * @param serverPort    Target server port
     * @param serverAddress Target server IP address
     * @throws SocketException If socket creation fails
     */
    public NetworkManager(int serverPort, InetAddress serverAddress) throws SocketException {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.packetAssembler = new PacketAssembler();
        this.serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);

        this.socket = new DatagramSocket();

        this.socket.setSoTimeout(800);
    }

    /**
     * Sends a single UDP packet
     *
     * @param packet Packet to send
     * @throws NetworkException If sending fails
     */
    private void sendPacket(DatagramPacket packet) throws NetworkException {
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            throw new NetworkException("Packet sending failed");
        }
    }

    /**
     * Receives a single UDP packet
     *
     * @return Received packet
     * @throws NetworkException If receiving fails or timeout occurs
     */
    private DatagramPacket receivePacket() throws NetworkException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return packet;
        } catch (SocketTimeoutException e) {
            throw new NetworkException("Server response timeout");
        } catch (IOException e) {
            throw new NetworkException("Packet receiving failed");
        }
    }

    /**
     * Receives complete message from server
     *
     * @return Received data bytes
     * @throws NetworkException If communication fails
     */
    public byte[] receiveData() throws NetworkException {
        while (true) {
            DatagramPacket packet = receivePacket();
            ByteBuffer buffer = ByteBuffer.wrap(packet.getData());

            packetAssembler.addPacket(serverSocketAddress, buffer);

            Map<SocketAddress, byte[]> messages = packetAssembler.getCompletedMessages();
            if (!messages.isEmpty()) {
                byte[] fullMessage = messages.get(serverSocketAddress);
                packetAssembler.clearCompletedMessages();
                return fullMessage;
            }
        }
    }

    /**
     * Sends data to specified address
     *
     * @param data    Data to send
     * @param address Target address
     * @param port    Target port
     * @throws NetworkException If sending fails
     */
    public void sendData(byte[] data, InetAddress address, int port) throws NetworkException {
        int totalPackets = (int) Math.ceil((double) data.length / MAX_PACKET_DATA_SIZE);

        for (int packetNumber = 0; packetNumber < totalPackets; packetNumber++) {
            int offset = packetNumber * MAX_PACKET_DATA_SIZE;
            int length = Math.min(MAX_PACKET_DATA_SIZE, data.length - offset);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            buffer.putInt(packetNumber + 1);
            buffer.putInt(totalPackets);

            buffer.put(data, offset, length);
            buffer.flip();

            byte[] packetData = new byte[buffer.remaining()];
            buffer.get(packetData);
            DatagramPacket packet = new DatagramPacket(packetData, packetData.length, address, port);
            sendPacket(packet);
        }
    }

    /**
     * Sends data to preconfigured server address
     *
     * @param data Data to send
     * @throws NetworkException If sending fails
     */
    public void sendData(byte[] data) throws NetworkException {
        sendData(data, this.serverAddress, this.serverPort);
    }

    /**
     * Closes network socket and releases resources
     */
    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}