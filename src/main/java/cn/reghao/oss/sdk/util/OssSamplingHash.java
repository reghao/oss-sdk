package cn.reghao.oss.sdk.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * @author reghao
 * @date 2026-02-02 09:47:25
 */
public class OssSamplingHash {
    private static final int SAMPLE_SIZE = 2 * 1024 * 1024; // 每段取 2MB

    public static String calculateSampleHash(Path path) throws Exception {
        long fileSize = path.toFile().length();

        // 如果文件太小（小于 6MB），直接全量计算
        if (fileSize <= SAMPLE_SIZE * 3) {
            return calculateFullHash(path);
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            // 1. 抽取头部
            readAndUpdate(channel, 0, SAMPLE_SIZE, digest);

            // 2. 抽取中间
            readAndUpdate(channel, fileSize / 2, SAMPLE_SIZE, digest);

            // 3. 抽取尾部
            readAndUpdate(channel, fileSize - SAMPLE_SIZE, SAMPLE_SIZE, digest);
        }

        // 4. 关键：混合文件大小，防止不同大小的文件但在这些位置数据相同
        digest.update(ByteBuffer.allocate(8).putLong(fileSize).flip());
        return HexFormat.of().formatHex(digest.digest());
    }

    public static String calculateSampleMd5(String absolutePath, long offset, int length) {
        File file = new File(absolutePath);

        if (!file.exists()) return null;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {

            // 1. 定位并读取指定区间的数据到内存
            ByteBuffer buffer = ByteBuffer.allocate(length);
            channel.read(buffer, offset);
            buffer.flip();

            // 2. 计算该区段的哈希 (为了速度，局部哈希可以用 MD5)
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return bytesToHex(md5.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static void readAndUpdate(FileChannel channel, long position, int size, MessageDigest digest) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        channel.read(buffer, position);
        buffer.flip();
        digest.update(buffer);
    }

    public static String calculateFullHash(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (var is = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
