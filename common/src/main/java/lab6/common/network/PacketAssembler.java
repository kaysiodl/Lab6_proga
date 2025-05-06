package lab6.common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.*;


public class PacketAssembler {
    private final Map<SocketAddress, SortedMap<Integer, byte[]>> pendingPackets;
    private final Map<SocketAddress, byte[]> completedMessages;

    public PacketAssembler() {
        this.pendingPackets = new HashMap<>();
        this.completedMessages = new HashMap<>();
    }

    public void addPacket(SocketAddress senderAddress, ByteBuffer packetBuffer) throws NetworkException {
        validatePacket(packetBuffer);

        int packetSequence = packetBuffer.getInt();
        int totalPackets = packetBuffer.getInt();
        byte[] packetData = extractPacketData(packetBuffer);

        storePacket(senderAddress, packetSequence, packetData);
        tryAssembleMessage(senderAddress, totalPackets);
    }

    public Map<SocketAddress, byte[]> getCompletedMessages() {
        return Collections.unmodifiableMap(completedMessages);
    }

    public void clearCompletedMessages() {
        completedMessages.clear();
    }

    private void validatePacket(ByteBuffer buffer) throws NetworkException {
        if (buffer.remaining() < 8) {
            throw new NetworkException("Invalid packet: header missing (required 8 bytes)");
        }
    }

    private byte[] extractPacketData(ByteBuffer buffer) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    private void storePacket(SocketAddress address, int sequence, byte[] data) {
        pendingPackets.computeIfAbsent(address, k -> new TreeMap<>()).put(sequence, data);
    }

    private void tryAssembleMessage(SocketAddress address, int expectedPackets) {
        SortedMap<Integer, byte[]> packets = pendingPackets.get(address);
        if (packets != null && packets.size() == expectedPackets) {
            completedMessages.put(address, assembleMessage(packets));
            pendingPackets.remove(address);
        }
    }

    private byte[] assembleMessage(SortedMap<Integer, byte[]> packets) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            packets.values().forEach(packet -> {
                try {
                    outputStream.write(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
