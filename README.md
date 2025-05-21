# FTP en Java

---

## Índice
1. [Descripción](#descripción)  
2. [Tecnologías](#tecnologías)  
3. [Requisitos](#requisitos) 
4. [Uso](#uso)   

---

## Descripción
Proyecto de cliente/servidor FTP en Java cuya interfaz por consola permite enviar, recibir y renombrar archivos en un servidor remoto. Aprendí a gestionar sockets TCP/IP, a trabajar con I/O streams y a levantar hilos para atender múltiples clientes simultáneamente.

---

## Tecnologías
- Java 
- Sockets TCP/IP  
- I/O Streams
- Threads para concurrencia  

---

## Requisitos
- JDK 8 o superior  
- Terminal con acceso al host donde se ejecuta el servidor  
- Carpeta local con permisos de lectura/escritura para el directorio base del servidor  

---

## Uso
    git clone https://github.com/eaomarb/FTP.git
    cd FTP-main
    
    javac FTPServer/FTPServer.java
    javac FTPClient/FTPClient.java

    java -cp FTPServer FTPServer <puerto> <directorio-base>
    # ej.: java -cp FTPServer FTPServer 5500 /home/usuario/ftp

    java -cp FTPClient FTPClient <host> <puerto>
    # ej.: java -cp FTPClient FTPClient localhost 5500

**Comandos disponibles en la consola del cliente:**
- `PUT <archivo_local>`: sube un archivo al servidor  
- `GET <archivo_remoto>`: descarga un archivo del servidor  
- `RENAME <viejo> <nuevo>`: renombra un archivo en el servidor  
- `QUIT`: cierra la conexión  

---
