package lab6.common.utility;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IOHandler<T> extends AutoCloseable {
    T read() throws FileNotFoundException;

    void write(String collection) throws IOException;
}
