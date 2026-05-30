package system.database;

import system.models.Transaction;

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

/**
 * Handles file operations such as logging and general text/binary file I/O.
 * Provides utility methods to read and write data to the filesystem.
 */
public class FileHandler {

    private static final String LOG_FILE = "transactions.log";

    /**
     * Logs a transaction to a text file.
     *
     * @param t The transaction to log.
     */
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

    /**
     * Logs a generic event message to the log file.
     *
     * @param message The message to log.
     */
    public static void logEvent(String message) {
        String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (BufferedWriter w = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            w.write(String.format("[%s] EVENT | %s%n", stamp, message));
        } catch (IOException e) {
            System.err.println("Failed to write event log: " + e.getMessage());
        }
    }

    /**
     * Reads a text file line by line.
     *
     * @param path The path of the file to read.
     * @return A list of strings, each representing a line from the file.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Writes a list of strings to a text file.
     *
     * @param path   The path of the file to write to.
     * @param lines  The lines of text to write.
     * @param append True to append to the file, false to overwrite.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeTextFile(String path, List<String> lines, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, append))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Reads the entire contents of a binary file.
     *
     * @param path The path of the file to read.
     * @return A byte array containing the file data.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Writes a byte array to a binary file.
     *
     * @param path   The path of the file to write to.
     * @param data   The byte array to write.
     * @param append True to append to the file, false to overwrite.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeBinaryFile(String path, byte[] data, boolean append) throws IOException {
        try (OutputStream out = new FileOutputStream(path, append)) {
            out.write(data);
        }
    }
}
