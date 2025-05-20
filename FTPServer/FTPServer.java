import java.io.*;
import java.net.*;

public class FTPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5217);
        System.out.println("Servidor FTP escoltant al port 5217");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new TransferFile(clientSocket).start();
        }
    }
}

class TransferFile extends Thread {
    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    public TransferFile(Socket socket) {
        this.clientSocket = socket;
        try {
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Client connectat: " + socket.getInetAddress());
        } catch (IOException e) {
            System.out.println("Error inicialitzant connexió: " + e.getMessage());
        }
    }

    public void run() {
        boolean connected = true;
        try {
            while (connected) {
                String option = din.readUTF();
                switch (option) {
                    case "1": sendFile(); break;
                    case "2": receiveFile(); break;
                    case "3": renameFile(); break;
                    case "4": connected = false; break;
                    default: break;
                }
            }
            clientSocket.close();
            System.out.println("Client desconnectat.");
        } catch (Exception e) {
            System.out.println("Error durant la sessió: " + e.getMessage());
        }
    }

    private void sendFile() throws Exception {
        String filename = din.readUTF();
        File file = new File(filename);

        if (!file.exists()) {
            dout.writeUTF("NOT_FOUND");
            return;
        }

        dout.writeUTF("EXISTS");
        String clientOpt = din.readUTF();
        if (!"OVERWRITE".equals(clientOpt)) return;

        dout.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dout.write(buffer, 0, bytesRead);
            }
        }

        dout.writeUTF("Fitxer enviat correctament.");
    }

    private void receiveFile() throws Exception {
        String filename = din.readUTF();
        File file = new File(filename);

        if (file.exists()) {
            dout.writeUTF("EXISTS");
            String clientOpt = din.readUTF();
            if (!"OVERWRITE".equals(clientOpt)) return;
        } else {
            dout.writeUTF("READY");
            din.readUTF(); // client envia OVERWRITE
        }

        long fileSize = din.readLong();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long remaining = fileSize;
            int bytesRead;
            while (remaining > 0 && (bytesRead = din.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                fos.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
        }

        dout.writeUTF("Fitxer rebut correctament.");
    }

    private void renameFile() throws Exception {
        String oldName = din.readUTF();
        String newName = din.readUTF();

        File oldFile = new File(oldName);
        File newFile = new File(newName);

        if (!oldFile.exists()) {
            dout.writeUTF("El fitxer original no existeix.");
            return;
        }

        boolean success = oldFile.renameTo(newFile);
        if (success) {
            dout.writeUTF("Fitxer renombrat correctament: " + oldName + " → " + newName);
        } else {
            dout.writeUTF("Error en renombrar el fitxer.");
        }
    }
}