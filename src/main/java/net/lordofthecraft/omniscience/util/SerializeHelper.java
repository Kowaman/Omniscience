package net.lordofthecraft.omniscience.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class SerializeHelper {

    // From https://stackoverflow.com/a/43166205

    /**
     * Serializes a string array to base64
     * @param data array being serialized
     * @return string array encoded in base64
     */
    public static String serializeStringArray(final String[] data) {
        try (final ByteArrayOutputStream boas = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(boas)) {
            oos.writeObject(data);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes a base64 sequence to a string array
     * @param data string being deserialized
     * @return String array
     */
    public static String[] deserializeStringArray(final String data) {
        try (final ByteArrayInputStream bias = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             final ObjectInputStream ois = new ObjectInputStream(bias)) {
            return (String[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
