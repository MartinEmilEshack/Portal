package Portal.FileManagement;

import Portal.UI.Main;

import java.io.*;
import java.nio.ByteBuffer;

public class Explorer {

    public synchronized static FileBytes copyFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new FileNotFoundException();
        FileBytes fileBytes = new FileBytes();
        try (InputStream sourceFile = new FileInputStream(file)) {
            fileBytes.file = sourceFile.readAllBytes();
        }
        fileBytes.fileName = file.getName().getBytes();
        fileBytes.fileSize = ByteBuffer.allocate(Long.BYTES).putLong(file.length()).array();
        return fileBytes;
    }

    public synchronized static void pasteFile(FileBytes fileBytes) throws IOException {
        if (ReceivedFolderExists()) {
            String path = getReceivedFolderPath() + "\\" + new String(fileBytes.fileName);
            try (OutputStream receivedFile = new FileOutputStream(path)) {
                receivedFile.write(fileBytes.file);
                receivedFile.flush();
            }
        }
    }

    private static boolean ReceivedFolderExists() throws IOException {
        File file = new File(getReceivedFolderPath());
        boolean fileCreated;
        if (!file.exists())
            fileCreated = file.createNewFile();
        else return true;
        return fileCreated;
    }

    private static String getReceivedFolderPath() {
        return (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent() + "\\Received";
    }

}