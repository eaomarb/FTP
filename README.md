# 📁 Mini FTP en Java (Cliente-Servidor)

Proyecto de práctica para entender cómo funciona la comunicación cliente-servidor con sockets en Java. El cliente puede:

- Enviar archivos al servidor  
- Descargar archivos desde el servidor  
- Renombrar archivos remotos  

---

## 🔧 Tecnologías usadas

- Java  
- Sockets TCP/IP  
- Streams de entrada/salida  
- Threads para múltiples clientes  

---

## ▶️ Cómo se usa

    # Compilar los dos archivos
    javac FTPServer.java
    javac FTPClient.java

    # Ejecutar el servidor
    java FTPServer

    # Ejecutar el cliente (en otra terminal)
    java FTPClient

---

## 🧑‍💻 Sobre el proyecto

Es un proyecto sencillo que me ayudó a practicar:

- Programación de red en Java  
- Manejo de archivos con streams  
- Uso de hilos (threads) para atender a varios clientes de forma concurrente  
