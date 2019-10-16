package Portal.FileManagement;

import java.io.*;

public class FileBytes {

    private String filePath;
    private FileInputStream fileReadStream;
    private BufferedInputStream bufferedFileReadStream;
    private FileOutputStream fileWriteStream;
    private BufferedOutputStream bufferedFileWriteStream;

    public FileBytes(String fileName,boolean newFile) {
        try {
//            filePath = "Document.doc";
            if(newFile) {
                filePath = "E:\\Desktop\\Receiver Gate\\"+fileName;
                fileWriteStream = new FileOutputStream(filePath);
                bufferedFileWriteStream = new BufferedOutputStream(fileWriteStream);
            } else {
                filePath = "E:\\Desktop\\Sender Gate\\"+fileName;
//                System.out.println("looking for "+filePath);
                fileReadStream = new FileInputStream(filePath);
                bufferedFileReadStream = new BufferedInputStream(fileReadStream);
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found! case = "+newFile);
        }
    }

    public int getBufferedBytes(byte[] fileBytes) {
        int bufferedBytesNum = 0;
        if (bufferedFileReadStream != null) {
            try {
                bufferedBytesNum = bufferedFileReadStream.read(fileBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bufferedBytesNum;
    }

    public void setBufferedBytes(byte[] fileBytes,int maxBytesToRead){
        if (bufferedFileWriteStream != null) {
            try {
                bufferedFileWriteStream.write(fileBytes,0,maxBytesToRead);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileName() {
        return filePath == null ? "" : new File(filePath).getName();
    }

//    public void setFileName(String filePath) {
//        this.filePath = filePath;
//    }

    public long getFileSize() {
        return filePath == null ? 0 : new File(filePath).length();
    }

    public void close(){
            try {
                if(bufferedFileReadStream != null)
                    bufferedFileReadStream.close();
                if (fileReadStream != null)
                    fileReadStream.close();
                if (bufferedFileWriteStream != null){
                    bufferedFileWriteStream.flush();
                    bufferedFileWriteStream.close();
                }
                if (fileWriteStream != null){
                    fileWriteStream.flush();
                    fileWriteStream.close();
                }
            } catch (IOException IOE) {
                IOE.printStackTrace();
            }
    }

}