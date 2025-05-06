package lab6.server.network;

import lab6.common.network.Request;
import lab6.common.network.Response;
import lab6.common.network.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Server implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Selector selector;
    private final DatagramChannel serverChannel;
    private final InetSocketAddress serverAddress;
    private final RequestHandler requestHandler;
    private final NetworkManager networkManager;
    private boolean isRunning = false;

    public Server(InetSocketAddress serverAddress, RequestHandler requestHandler) throws IOException {
        this.serverAddress = serverAddress;
        this.requestHandler = requestHandler;
        this.serverChannel = DatagramChannel.open();
        this.selector = Selector.open();
        this.networkManager = new NetworkManager(serverChannel);

        initServer();
    }

    protected void initServer() throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.bind(serverAddress);
        serverChannel.register(selector, SelectionKey.OP_READ);
        logger.info("UDP Server started on port {}", serverAddress.getPort());
        isRunning = true;
    }

    public void run() {
        try {
            while (selector.isOpen()) {
                if (selector.select() == 0) continue;

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        handleKey(key);
                    } catch (Exception e) {
                        logger.error("Error handling key", e);
                        key.cancel();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in server main loop", e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                logger.error("Error closing server", e);
            }
        }
    }

    private void handleKey(SelectionKey key) throws Exception {
        if (key.isReadable()) {
            DatagramChannel channel = (DatagramChannel) key.channel();
            System.out.println("Channel is readable");

            Map<SocketAddress, byte[]> messages = networkManager.readFromChannel(channel);
            System.out.println("Received messages count: " + messages.size());

            if (messages.isEmpty()) {
                System.out.println("No complete messages received");
                return;
            }

            for (Map.Entry<SocketAddress, byte[]> entry : messages.entrySet()) {
                System.out.println("Processing message from " + entry.getKey());
                System.out.println("Message size: " + entry.getValue().length + " bytes");

                try {
                    Request request = (Request) Serializer.deserialazeObject(entry.getValue());
                    System.out.println("Deserialized command: " + request.command());

                    Response response = requestHandler.handleRequest(request);
                    System.out.println("Sending response: " + response.message() + response.data());

                    networkManager.sendData(Objects.requireNonNull(Serializer.serializeObject(response)), entry.getKey());
                } catch (Exception e) {
                    System.err.println("Error processing request: " + e.getMessage());

                    Response error = Response.error("Processing error: " + e.getMessage());
                    networkManager.sendData(Serializer.serializeObject(error), entry.getKey());
                }
            }
        }
    }

    public void close() throws IOException {
        logger.info("Shutting down server...");
        isRunning = false;
        if (selector != null) {
            selector.close();
        }
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}