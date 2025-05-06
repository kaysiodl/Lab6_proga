package lab6.server.network;

import lab6.common.network.Request;
import lab6.common.network.Response;

public abstract class RequestHandler {
    protected abstract Response handleRequest(Request request);
}
