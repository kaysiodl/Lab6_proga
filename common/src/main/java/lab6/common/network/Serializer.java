package lab6.common.network;

import java.io.*;

public class Serializer {
    public static byte[] serializeObject(Object object) throws IOException {
        try {
            if (object == null) {
                System.err.println("Serializer: null object");
                return null;
            }
            if (!(object instanceof Serializable)) {
                System.err.println("Serializer: object not Serializable - " + object.getClass());
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            objectOutputStream.close();
            return bytes;
        } catch (IOException e) {
            throw new IOException("Failed serializing object!");
        }
    }

    public static Object deserialazeObject(byte[] data) throws Exception {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object obj = objectInputStream.readObject();
            objectInputStream.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Failed deserializing object!");
        }
    }
}
