
import java.io.*;
import java.net.*;

class FTPClient {

    public static void main(String args[]) throws Exception {
        Socket soc = new Socket("127.0.0.1", 5217);
        transferfileClient t = new transferfileClient(soc);
        t.displayMenu();

    }
}

class transferfileClient {

    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;

    transferfileClient(Socket soc) {
        try {
            ClientSoc = soc;
            din = new DataInputStream(ClientSoc.getInputStream());
            dout = new DataOutputStream(ClientSoc.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception ex) {
        }
    }

    public void displayMenu() throws Exception {
        boolean connected = true;
        while (connected) {
            System.out.println("[ MENU ]");
            System.out.println("1. Enviar fitxer");
            System.out.println("2. Rebre fitxer");
            System.out.println("3. Renombrar fitxer remoto");
            System.out.println("4. Sortir");
            System.out.print("Opció: ");
            int choice = Integer.parseInt(br.readLine());
            dout.writeUTF(choice + ""); // enviem l'opció al servidor
            switch (choice) {
                case 1:
                    sendFile();
                    break;
                case 2:
                    receiveFile();
                    break;
                case 3:
                    renameRemoteFile();
                    break;
                case 4:
                    connected = false;
                    // tancarem connexió després del bucle
                    break;
                default:
                    /* opció invàlida */ break;
            }
        }
        ClientSoc.close();
    }

    void sendFile() throws Exception {
        // lectura nom fitxer local
        System.out.print("Nom fitxer a enviar: ");
        String filename = br.readLine();
        File f = new File(filename);
        boolean doTransfer = true;
        // avisar al servidor
        dout.writeUTF(filename);
        if (!f.exists()) {
            System.out.println("No existeix localment.");
            dout.writeUTF("NO_FILE");
            doTransfer = false;
        } else {
            String serverResp = din.readUTF();
            if ("EXISTS".equals(serverResp)) {
                System.out.print("Sobreescriure? (Y/N): ");
                String opt = br.readLine();
                dout.writeUTF(opt.equalsIgnoreCase("Y") ? "OVERWRITE" : "SKIP");
                if (!opt.equalsIgnoreCase("Y")) {
                    doTransfer = false;
                }
            } else {
                // READY
                dout.writeUTF("OVERWRITE");
            }
        }
        if (doTransfer) {
            // enviem mida i dades
            dout.writeLong(f.length());
            try (FileInputStream fis = new FileInputStream(f)) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = fis.read(buf)) != -1) {
                    dout.write(buf, 0, r);
                }
            }
            System.out.println(din.readUTF()); // OK del servidor
        }
    }

    void receiveFile() throws Exception {
        System.out.print("Nom fitxer a rebre: ");
        String filename = br.readLine();
        dout.writeUTF(filename);
        String serverResp = din.readUTF();
        boolean doReceive = true;
        if ("NOT_FOUND".equals(serverResp)) {
            System.out.println("No existeix remotament.");
            doReceive = false;
        } else if ("EXISTS".equals(serverResp)) {
            System.out.print("Sobreescriure local? (Y/N): ");
            String opt = br.readLine();
            dout.writeUTF(opt.equalsIgnoreCase("Y") ? "OVERWRITE" : "SKIP");
            if (!opt.equalsIgnoreCase("Y")) {
                doReceive = false;
            }
        } else {
            // READY
            dout.writeUTF("OVERWRITE");
        }
        if (doReceive) {
            long size = din.readLong();
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                byte[] buf = new byte[4096];
                long rem = size;
                int r;
                while (rem > 0 && (r = din.read(buf, 0, (int) Math.min(buf.length, rem))) != -1) {
                    fos.write(buf, 0, r);
                    rem -= r;
                }
            }
            System.out.println(din.readUTF()); // OK del servidor
        }
    }

    void renameRemoteFile() throws Exception {
        // renombrar al servidor
        System.out.print("Nom actual remot: ");
        String oldName = br.readLine();
        System.out.print("Nou nom remot: ");
        String newName = br.readLine();
        dout.writeUTF(oldName);
        dout.writeUTF(newName);
        System.out.println(din.readUTF()); // resposta servidor
    }
}
