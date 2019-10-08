package Portal.Network;

import Portal.FileManagement.FileBytes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ReceivePort{

    private static final int LOGIN_DATA_PORT = 5876;
    private static final int FILES_PORT = 5877;

    private static int password = 0;
    public static Thread fileInputThread = null;

    private static class LoginDataReceiver implements Runnable{

        @Override
        public void run() {
            ServerSocket server = null;
            Socket sender = null;
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                server = new ServerSocket(LOGIN_DATA_PORT);
                sender = server.accept();
                input = new DataInputStream(sender.getInputStream());
                output = new DataOutputStream(sender.getOutputStream());
                while(true){
                    byte[] passwordBytes = new byte[input.available()];
                    int bytes = input.read(passwordBytes);
                    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                    buffer.put(passwordBytes);
                    buffer.flip();
                    if(buffer.getInt() != getPassword()){
                        output.write(("WRONG_PASSWORD").getBytes());
                    }else{
                        fileInputThread = new Thread(new FileDataReceiver());
                        fileInputThread.run();
                        output.write(("START_FILE_TRANSMITION").getBytes());
                        break;
                    }
                }
                output.flush();
            }catch (IOException IOE){
                IOE.printStackTrace();
            }finally {
                try {
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    if (sender != null)
                        sender.close();
                    if (server != null)
                        server.close();
                }catch (IOException IOE){
                    IOE.printStackTrace();
                }
            }
        }

    }

    private static class FileDataReceiver implements Runnable{

        @Override
        public void run() {

            ServerSocket server = null;
            Socket sender = null;
            DataInputStream input = null;
            DataOutputStream output = null;
            try {
                server = new ServerSocket(FILES_PORT);
                sender = server.accept();
                input = new DataInputStream(sender.getInputStream());
                output = new DataOutputStream(sender.getOutputStream());
                byte[] fileName = new byte[input.available()];
                int bytes = 0;
                bytes = bytes + input.read(fileName);
                byte[] fileSize = new byte[input.available()];
                bytes = bytes + input.read(fileSize);
                byte[] file = new byte[input.available()];
                bytes = bytes + input.read(file);
                FileBytes fileBytes = new FileBytes();
                fileBytes.fileName = fileName;
                fileBytes.fileSize = fileSize;
                fileBytes.file = file;
                output.write("FILE_TRANSFER_COMPLETED".getBytes());
                output.flush();
            }catch(IOException IOE){
                IOE.printStackTrace();
            }finally {
                try {
                    Thread loginDataThread = new Thread(new LoginDataReceiver());
                    loginDataThread.run();
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    if (sender != null)
                        sender.close();
                    if (server != null)
                        server.close();
                }catch (IOException IOE){
                    IOE.printStackTrace();
                }
            }


        }

    }

    public static Runnable startLoginDataReceiver(){
        return new LoginDataReceiver();
    }

    public static void generatePassword(){
        double password = Math.random()*10;
        ReceivePort.password =  (int) password;
    }

    public static int getPassword(){
        return ReceivePort.password;
    }

}
