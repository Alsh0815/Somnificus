package com.x_viria.app.vita.somnificus.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SecureBackup {

    private final long TIME;
    private final Map<String, ?> DATA;

    public SecureBackup(long time, Map<String, ?> data) {
        this.DATA = data;
        this.TIME = time;
    }

    public byte[] sha512() throws NoSuchAlgorithmException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            dataStream.writeLong(TIME);
            dataStream.write(serializeMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest(byteStream.toByteArray());
    }

    private byte[] serializeMap() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(DATA);
        objectStream.flush();
        return byteStream.toByteArray();
    }

}
