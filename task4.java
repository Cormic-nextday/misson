package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class task4 {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("server is running...");
        while(true)
        {
            Socket s = server.accept();
            System.out.println("connect from "+s.getRemoteSocketAddress());
            Thread t = new ServerThread(s);
            t.start();
        }
    }
}

class ServerThread extends Thread
{
    Socket socket;

    public ServerThread(Socket s)
    {
        this.socket=s;
    }

    @Override
    public void run()
    {
        try(InputStream inputStream = this.socket.getInputStream())
        {
            try(OutputStream outputStream = this.socket.getOutputStream()) {
                ToDo(inputStream, outputStream);
            }
        }catch (Exception e)
        {
            try{
                this.socket.close();
            }catch (IOException ioe){
            }
            System.out.println("this thread is end");
        }
    }

    private void ToDo(InputStream in,OutputStream out) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        String buffer = reader.readLine();
        System.out.println(buffer);
        boolean answer = false;
        if(buffer.startsWith("GET / HTTP/1."))
            answer = true;
        while(true)
        {
            String get = reader.readLine();
            if(get.isEmpty())
                break;
            System.out.println(get);
        }
        if(answer)
        {
            String date = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<meta charset=\"utf-8\" />\n" +
                    "\t\t<title>响应网络</title>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t\t<h1>Hello,World!</h1>\n" +
                    "\t</body>\n" +
                    "</html>\n";
            int len= date.getBytes(StandardCharsets.UTF_8).length;
            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + len + "\r\n");
            writer.write("\r\n");
            writer.write(date);
            writer.flush();
        }
        else {
            writer.write("HTTP/1.0 404 Not Found\r\n");
            writer.write("Content-Length: 0\r\n");
            writer.write("\r\n");
            writer.flush();
        }
    }
}
