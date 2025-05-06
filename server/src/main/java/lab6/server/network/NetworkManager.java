package lab6.server.network;

import lab6.common.network.NetworkException;
import lab6.common.network.PacketAssembler;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

public class NetworkManager {
    private final DatagramChannel serverChannel;
    private final PacketAssembler packetAssembler;
    private final int packetDataSize;
    private final ByteBuffer buffer;

    public NetworkManager(DatagramChannel serverChannel) throws IOException {
        this.serverChannel = serverChannel;
        this.packetAssembler = new PacketAssembler();
        int BUFFER_SIZE = 1024;
        this.packetDataSize = BUFFER_SIZE - 8;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

        serverChannel.configureBlocking(false);
    }

    private void sendPacket(SocketAddress address, ByteBuffer buffer) throws NetworkException {
        try {
            int sendBytes = serverChannel.send(buffer, address);
            if (sendBytes == 0) {
                throw new NetworkException("Server didn't send anything!");
            }
        } catch (IOException e) {
            throw new NetworkException("Failed sending packet!");
        }
    }

    public void sendData(byte[] data, SocketAddress address) throws NetworkException {
        int packetsCount = (int) Math.ceil((double) data.length / packetDataSize);
        for (int number = 0; number < packetsCount; number++) {
            buffer.clear();
            buffer.putInt(number + 1);
            buffer.putInt(packetsCount);
            buffer.put(data, number * packetDataSize, Math.min(packetDataSize, data.length - number * packetDataSize));
            buffer.flip();
            sendPacket(address, buffer);
        }
    }

    public Map<SocketAddress, byte[]> readFromChannel(DatagramChannel channel) throws IOException, NetworkException {
        packetAssembler.clearCompletedMessages();
        while (true) {
            buffer.clear();
            SocketAddress clientAddress = channel.receive(buffer);
            if (clientAddress == null) break;
            buffer.flip();
            packetAssembler.addPacket(clientAddress, buffer);
        }
        return packetAssembler.getCompletedMessages();
    }
}
