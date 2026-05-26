package atmsystem.database;

import atmsystem.models.Transaction;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileHandler {

    private static final String LOG_FILE = "transactions.log";

    public static void logTransaction(Transaction t) {
        String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line = String.format("[%s] %s | %s | %.2f | balance=%.2f%s%n",
                stamp,
                t.getAccountNumber(),
                t.getTransactionType(),
                t.getAmount(),
                t.getBalanceAfter(),
                t.getTargetAccount() != null ? " | target=" + t.getTargetAccount() : "");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            w.write(line);
        } catch (IOException e) {
            System.err.println("Failed to write transaction log: " + e.getMessage());
        }
    }

    public static void logEvent(String message) {
        String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (BufferedWriter w = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            w.write(String.format("[%s] EVENT | %s%n", stamp, message));
        } catch (IOException e) {
            System.err.println("Failed to write event log: " + e.getMessage());
        }
    }

    public static List<String> readTextFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream in = new BufferedInputStream(new FileInputStream(path));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            String content = out.toString("UTF-8");
            String[] split = content.split("\\R");
            for (String line : split) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static void writeTextFile(String path, List<String> lines, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, append))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static byte[] readBinaryFile(String path) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(path));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    public static void writeBinaryFile(String path, byte[] data, boolean append) throws IOException {
        try (OutputStream out = new FileOutputStream(path, append)) {
            out.write(data);
        }
    }
}
