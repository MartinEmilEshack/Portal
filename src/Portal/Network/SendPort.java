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

    public static boolean sendRequest(String IP,int password,String fileName){

//        FileBytes fileSent = fileBytes;
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
            output.flush();
            if (requestResponse == 0) {
                System.err.println("Wrong password !");
//                throw new IOException("WRONG_PASSWORD");
                return false;
            }else if(requestResponse == 1){
                System.out.println("Password is correct, starting sending files");
                Thread fileSending = new Thread(new FileDataSender(fileName));
                fileSending.start();
                return true;
            }else{
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

    private static class FileDataSender implements Runnable {

        private String fileName;

        FileDataSender(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void run() {

            FileBytes fileSent = null;

            Socket sender = null;

            DataOutputStream output = null;
            BufferedOutputStream outputBuffered = null;

            try {
                sender = new Socket(IP,FILES_PORT);

                output = new DataOutputStream(sender.getOutputStream());
                outputBuffered = new BufferedOutputStream(sender.getOutputStream());

                fileSent = new FileBytes(fileName,false);

                output.writeUTF(fileSent.getFileName());
                output.writeLong(fileSent.getFileSize());

                byte[] fileBytes = new byte[10];
                int bytesBuffered;
                int bytesSent = 0;

                while (true) {
                    bytesBuffered = fileSent.getBufferedBytes(fileBytes);
                    if(bytesBuffered > 0){
                        bytesSent += bytesBuffered;
                        outputBuffered.write(fileBytes,0,bytesBuffered);
                    }else break;
                }

                output.flush();
                outputBuffered.flush();

            }catch(IOException IOE){
                IOE.printStackTrace();
            }finally {
                try {
//                    if (input != null)
//                        input.close();
                    if(fileSent != null)
                        fileSent.close();
                    if (output != null)
                        output.close();
                    if(outputBuffered != null)
                        outputBuffered.close();
                    if (sender != null)
                        sender.close();
                }catch (IOException IOE){
                    IOE.printStackTrace();
                }
            }
        }

    }

    public static String getPublicIP()throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }

}
