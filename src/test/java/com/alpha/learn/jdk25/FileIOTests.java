package com.alpha.learn.jdk25;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileIOTests {

    @Test
    @SneakyThrows
    public void testIOWithFile() {
        TestForJDKSerialize test = new TestForJDKSerialize("test",  "123", 1);

        String filePath = "user_io.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            TestForJDKSerialize readUser = (TestForJDKSerialize) ois.readObject();
            log.info("readUser: {}", readUser);
        }
    }

    @SneakyThrows
    @Test
    public void testNIOWithFile() {
        TestForJDKSerialize test = new TestForJDKSerialize("Alice", "123456",30);
        Path path = Paths.get("user_nio.ser");

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            byte[] nameBytes = test.getName().getBytes(StandardCharsets.UTF_8);
            byte[] pwdBytes = (test.getPassword() == null ? "" : ("ENC(" + test.getPassword() + ")")).getBytes(StandardCharsets.UTF_8);

            ByteBuffer buffer = ByteBuffer.allocate(4 + nameBytes.length + 4 + 4 + pwdBytes.length);
            buffer.putInt(nameBytes.length);
            buffer.put(nameBytes);

            buffer.putInt(test.getAge());

            buffer.putInt(pwdBytes.length);
            buffer.put(pwdBytes);

            buffer.flip();
            channel.write(buffer);
        }

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            buffer.flip();

            int nameLen = buffer.getInt();
            byte[] nameBytes = new byte[nameLen];
            buffer.get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            int age = buffer.getInt();

            int pwdLen = buffer.getInt();
            byte[] pwdBytes = new byte[pwdLen];
            buffer.get(pwdBytes);
            String pwdStr = new String(pwdBytes, StandardCharsets.UTF_8);
            String password = pwdStr.startsWith("ENC(") ? pwdStr.substring(4, pwdStr.length() - 1) : pwdStr;

            TestForJDKSerialize readUser = new TestForJDKSerialize(name, password, age);
            log.info("NIO自定义序列化反序列化结果: {}", readUser);
        }
    }


    @Getter
    @Setter
    static class TestForJDKSerialize implements Serializable {

        @Serial
        private static final long serialVersionUID = -4499471911661866197L;

        private String name;
        private transient String password;
        private int age;

        public TestForJDKSerialize(String name, String password, int age) {
            this.name = name;
            this.password = password;
            this.age = age;
        }

        @Override
        public String toString() {
            return "TestForJDKSerialize{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    ", age=" + age +
                    '}';
        }

        @Serial
        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            out.writeUTF(name);
            out.writeInt(age);
            out.writeUTF(password == null ? null : "ENC(" + password + ")");
        }

        @Serial
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            name = in.readUTF();
            age = in.readInt();
            String encPwd = in.readUTF();
            if (encPwd.startsWith("ENC(")) {
                password = encPwd.substring(4, encPwd.length() - 1);
            }
        }
    }
}
