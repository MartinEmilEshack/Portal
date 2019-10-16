package Portal.Network;

import Portal.FileManagement.FileBytes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ReceivePort {

    private static final int LOGIN_DATA_PORT = 5876;
    private static final int FILES_PORT = 5877;

    private static boolean stop = false;
    private static int password = 0;

    private static class LoginDataReceiver implements Runnable {

        private ServerSocket server = null;
        private Socket sender = null;
        private DataInputStream input = null;
        private DataOutputStream output = null;

        @Override
        public void run() {

            while (!stop) {
                try {
                    server = new ServerSocket(LOGIN_DATA_PORT);
                    server.setSoTimeout(5000);
                    sender = server.accept();

                    input = new DataInputStream(sender.getInputStream());
                    output = new DataOutputStream(sender.getOutputStream());

                    int sentPassword = input.readInt();
//                    System.out.println("Massage Sent " + sentPassword);

                    if (sentPassword == getPassword()) {
                        Thread fileInputThread = new Thread(new FileDataReceiver());
                        fileInputThread.start();
                        output.writeInt(1);
//                        System.out.println("Wright password");
                    } else {
                        output.writeInt(0);
                    }
                    output.flush();
                } catch (EOFException EOFE) {
                    EOFE.printStackTrace();
                } catch (SocketTimeoutException STE){

                } catch (IOException IOE) {
                    IOE.printStackTrace();
                } finally {
                    try {
                        if (input != null)
                            input.close();
                        if (output != null)
                            output.close();
                        if (sender != null)
                            sender.close();
                        if (server != null)
                            server.close();
                    } catch (IOException IOE) {
                        IOE.printStackTrace();
                    }
                }
//                System.out.println("bla bla");
            }
            System.out.println("Receiver stopped");
        }
    }

    private static class FileDataReceiver implements Runnable {

        @Override
        public void run() {

            ServerSocket server = null;
            Socket receiver = null;

            DataInputStream input = null;
            BufferedInputStream inputBuffered = null;

            FileBytes fileReceived = null;

            try {

                server = new ServerSocket(FILES_PORT);
                server.setSoTimeout(5000);
                receiver = server.accept();

                input = new DataInputStream(receiver.getInputStream());
                inputBuffered = new BufferedInputStream(receiver.getInputStream());



                fileReceived = new FileBytes(input.readUTF(),true);
                long fileSize = input.readLong();

                byte[] fileBytes = new byte[10];
                int bytesBuffered;
                int bytesReceived = 0;

                while (true) {
                    bytesBuffered = inputBuffered.read(fileBytes);
                    if(bytesBuffered > 0){
                        bytesReceived += bytesBuffered;
//                        System.out.println(new String(fileBytes));
                        fileReceived.setBufferedBytes(fileBytes,bytesBuffered);
                    }else break;
                }
                System.out.println(fileReceived.getFileName()+" file was received successfully with size "+bytesReceived);
//                byte[] fileBytes = new byte[10];
//                int bytes = 0;
//                bytes = bytes + input.read(fileName);
//
//                byte[] fileSize = new byte[input.available()];
//                bytes = bytes + input.read(fileSize);
//
//                byte[] file = new byte[input.available()];
//                bytes = bytes + input.read(file);
//
//                FileBytes fileBytes = new FileBytes();
//                fileBytes.fileName = fileName;
//                fileBytes.fileSize = fileSize;
//                fileBytes.file = file;

//                output.write("FILE_TRANSFER_COMPLETED".getBytes());
//                output.flush();
            } catch (IOException IOE) {
                IOE.printStackTrace();
            } finally {
                try {
//                    Thread loginDataThread = new Thread(new LoginDataReceiver());
//                    loginDataThread.start();
                    if(fileReceived != null)
                        fileReceived.close();
                    if (input != null)
                        input.close();
                    if(inputBuffered != null)
                        inputBuffered.close();
//                    if (output != null)
//                        output.close();
                    if (receiver != null)
                        receiver.close();
                    if (server != null)
                        server.close();
                } catch (IOException IOE) {
                    IOE.printStackTrace();
                }
            }

        }

    }

    public static Runnable startLoginDataReceiver() {
        return new LoginDataReceiver();
    }

    public static void generatePassword() {
        double password = Math.random() * 1000000;
        ReceivePort.password =  (int) password;
//        ReceivePort.password = 1234;
    }

    public static int getPassword() {
        return ReceivePort.password;
    }

    public static void close(){
        stop = true;
    }

}