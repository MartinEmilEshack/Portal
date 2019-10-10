package Portal.Network;

import Portal.FileManagement.FileBytes;

import java.io.*;
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
            SendPort.fileBytes = new FileBytes();
            try {
                receiver = new Socket(IP,FILES_PORT);

                output = new DataOutputStream(receiver.getOutputStream());
                input = new DataInputStream(receiver.getInputStream());

                byte[] fileSentConfirm = new byte[2];
                while (!(new String(fileSentConfirm)).equals("FILE_TRANSFER_COMPLETED")) {
                    output.write(SendPort.fileBytes.fileName);
                    output.write(SendPort.fileBytes.fileSize);
                    output.write(SendPort.fileBytes.file);
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

    public static boolean sendRequest(String IP,int password,FileBytes fileBytes){

        SendPort.fileBytes = fileBytes;
        SendPort.IP = IP;

        Socket sender = null;
        DataOutputStream output = null;
        DataInputStream input = null;

        try {
            sender = new Socket(IP, LOGIN_DATA_PORT);
            output = new DataOutputStream(sender.getOutputStream());
            input = new DataInputStream(sender.getInputStream());

            //send to other device
            output.writeInt(password);
            //receive response from other device
            int requestResponse = input.readInt();

//            System.out.println("Send Response -> "+requestResponse);

            if (requestResponse == 0) {
                output.flush();
                System.err.println("Wrong password !");
//                throw new IOException("WRONG_PASSWORD");
                return false;
            }else if(requestResponse == 1){
                output.flush();
                System.out.println("Password is correct, starting sending files");
                return true;
//                Thread fileSending = new Thread(new FileDataSender());
//                fileSending.start();
            }else{
                output.flush();
                System.err.println("Unknown response ! -> "+requestResponse);
                return false;
//                throw new IOException("UNKNOWN_RESPONSE");
//                throw new IOException(new String(requestResponse));
            }
        }catch(IOException IOE){
            IOE.printStackTrace();
            return false;
        }finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (sender != null)
                    sender.close();
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
