package Portal.Network;

import Portal.FileManagement.FileBytes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendPort {

    private static final int LOGIN_DATA_PORT = 5876;
    private static final int FILES_PORT = 5877;

    private static String IP = null;
    private static FileBytes fileBytes = null;

    private static class FileDataSender implements Runnable {

        @Override
        public void run() {
            Socket receiver = null;
            DataOutputStream output = null;
            DataInputStream input = null;
            try {
                receiver = new Socket(IP,FILES_PORT);
                output = new DataOutputStream(receiver.getOutputStream());
                input = new DataInputStream(receiver.getInputStream());
                byte[] fileSentConfirm = new byte[2];
                while (!(new String(fileSentConfirm)).equals("FILE_TRANSFER_COMPLETED")) {
                    output.write(fileBytes.fileName);
                    output.write(fileBytes.fileSize);
                    output.write(fileBytes.file);
                    fileSentConfirm = new byte[input.available()];
                    int bytes = input.read(fileSentConfirm);
                }
            }catch(IOException IOE){
                IOE.printStackTrace();
            }finally {
                try {
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    if (receiver != null)
                        receiver.close();
                }catch (IOException IOE){
                    IOE.printStackTrace();
                }
            }
        }

    }

    public static void sendRequest(String IP,int password,FileBytes fileBytes) throws IOException{
        SendPort.fileBytes = fileBytes;
        Socket receiver = null;
        DataOutputStream output = null;
        DataInputStream input = null;
        try {
            receiver = new Socket(IP, LOGIN_DATA_PORT);
            output = new DataOutputStream(receiver.getOutputStream());
            output.write(password);
            input = new DataInputStream(receiver.getInputStream());
            byte[] requestResponse = new byte[input.available()];
            int bytes = input.read(requestResponse);
            if (new String(requestResponse).equals("WRONG_PASSWORD")) {
                output.flush();
                throw new IOException("WRONG_PASSWORD");
            }else if(new String(requestResponse).equals("START_FILE_TRANSMITION")){
                output.flush();
                Thread fileSending = new Thread(new FileDataSender());
                fileSending.run();
            }else{
                output.flush();
                throw new IOException("UNKNOWN_RESPONSE");
            }
        }catch(IOException IOE){
            IOE.printStackTrace();
        }finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (receiver != null)
                    receiver.close();
            }catch (IOException IOE){
                IOE.printStackTrace();
            }
        }
    }

    public static String getLocalIP()throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }

}
