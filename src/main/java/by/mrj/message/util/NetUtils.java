package by.mrj.message.util;


import by.mrj.message.domain.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public abstract class NetUtils {
    public static final String MAGIC = "MAGIC";

    public static byte[] serialize(Message<?> object) { // xxx: Possible should work only with Message type. Some for below.
        return serialize(object, true);
    }

    public static byte[] serialize(Serializable object, boolean withMagic) { // xxx: Possible should work only with Message type. Some for below.
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            if (withMagic)
                oos.writeBytes(MAGIC); // first step check

            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message<?> deserialize(InputStream is) {
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            checkMagic(ois);
            Message message = (Message) ois.readObject();
            return message; // command check to know validations to be performed.
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message<?> deserialize(byte[] bytes) {
        return deserialize(new ByteArrayInputStream(bytes));
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close(); // no synchronization
            } catch (IOException ignored) {
            }
        }
    }

    // Works only with Message deserialization
    private static void checkMagic(ObjectInputStream in) throws IOException {
        byte[] buf = new byte[MAGIC.length()];
        in.read(buf, 0, buf.length);

        if (!Arrays.equals(MAGIC.getBytes(StandardCharsets.UTF_8), buf)) {
            in.close();
            throw new RuntimeException("Wrong magic"); // todo
        }
    }
}
