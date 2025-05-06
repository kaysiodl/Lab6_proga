package lab6.common.network;

import java.io.Serializable;
import java.util.Collection;

public record Response(boolean success, String message, Collection<?> data) implements Serializable {

    public static Response success(String message) {
        return new Response(true, message, null);
    }

    public static Response success(Collection<?> data) {
        return new Response(true, null, data);
    }

    public static Response success(String message, Collection<?> data) {
        return new Response(true, message, data);
    }

    public static Response error(String errorMessage) {
        return new Response(false, errorMessage, null);
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (message != null) {
            sb.append(message);
        }

        if (data != null && !data.isEmpty()) {
            data.forEach(obj -> sb.append("\n").append(obj));
        }

        return sb.toString();
    }

    public static class ResponseBuilder {
        private boolean success = true;
        private String message;
        private Collection<?> data;

        public ResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResponseBuilder data(Collection<?> data) {
            this.data = data;
            return this;
        }

        public Response build() {
            return new Response(success, message, data);
        }
    }
}
