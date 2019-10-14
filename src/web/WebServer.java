package web;

import utils.ProgramSettings;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;

public class WebServer extends Thread {

    private static final boolean verbose = false;
    private static final File WEB_ROOT = new File("./html/");
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND = "404/index.html";
    private static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    private static ProgramSettings settings = new ProgramSettings();
    private Socket connect;

    public WebServer(Socket c) {
        connect = c;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;

        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream())); // we read characters from the client via input stream on the socket
            out = new PrintWriter(connect.getOutputStream()); // we get character output stream to client (for headers)
            dataOut = new BufferedOutputStream(connect.getOutputStream()); // get binary output stream to client (for requested data)
            String input = in.readLine(); // get first line of the request from the client
            StringTokenizer parse = new StringTokenizer(input); // we parse the request with a string tokenizer
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
            fileRequested = parse.nextToken().toLowerCase(); // we get file requested

            if (!method.equals("GET") && !method.equals("HEAD")) {
                this.sendResponse("HTTP/1.1 501 Not Implemented", out, dataOut, new File(WEB_ROOT, METHOD_NOT_SUPPORTED));

                if (verbose)
                    System.out.println("501 Not Implemented : " + method + " method.");

            } else {
                if (fileRequested.endsWith("/"))
                    fileRequested += DEFAULT_FILE;

                else if (new File(WEB_ROOT, fileRequested).isDirectory())
                    fileRequested += "/" + DEFAULT_FILE;

                if (method.equals("GET"))
                    this.sendResponse("HTTP/1.1 200 OK", out, dataOut, new File(WEB_ROOT, fileRequested));

                if (verbose)
                    System.out.println("File " + fileRequested + " of type " + getContentType(fileRequested) + " returned");
            }
        } catch (FileNotFoundException fnfe) {
            try {
                this.fileNotFound(Objects.requireNonNull(out), Objects.requireNonNull(dataOut), fileRequested);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }
        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                Objects.requireNonNull(in).close();
                Objects.requireNonNull(out).close();
                Objects.requireNonNull(dataOut).close();
                connect.close();
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose)
                System.out.println("Connection closed.\n");
        }
    }

    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        // TODO Instead of inline css, link css file?
        // TODO Auto refresh temperature, mode, and power consumption in index.html
        switch (fileRequested) {
            case "/api/temperature-formatted":
                this.sendResponse(out, (BufferedOutputStream) dataOut, "<div style=\"color: white;font-size: 6.7em;font-family: Lato, sans-serif;\">" + (int) settings.getTemperature() + "</div>");
                break;
            case "/api/mode-formatted":
                this.sendResponse(out, (BufferedOutputStream) dataOut, "<div style=\"font-size: 1.5em; color: #748cb0;font-family: Source Sans Pro, sans-serif; text-align: center;\">" + settings.getMode() + "</div>");
                break;
            case "/api/usage-formatted":
                this.sendResponse(out, (BufferedOutputStream) dataOut, "<div style=\"font-size: 3.3em; font-weight: 300; color: #f3f6fb; font-family: Lato, sans-serif;\">" + settings.getPowerConsumption() + "</div>");
                break;
            case "/api/billing-formatted":
                this.sendResponse(out, (BufferedOutputStream) dataOut, "<div style=\"font-size: 3.3em; font-weight: 300; color: #f3f6fb; font-family: Lato, sans-serif;\">" + settings.getPowerPrice() + "</div>");
                break;
            default:
                this.sendResponse("HTTP/1.1 404 File Not Found", out, (BufferedOutputStream) dataOut, new File(WEB_ROOT, FILE_NOT_FOUND));
                break;
        }

        if (verbose)
            System.out.println("File " + fileRequested + " not found");
    }

    private void sendResponse(String response, PrintWriter out, BufferedOutputStream dataOut, File file) throws IOException {
        int fileLength = (int) file.length();
        byte[] fileData = this.readFileData(file, fileLength);

        out.println(response);
        out.println("Server: Java HTTP Server from Saurel : 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + this.getContentType(file.getPath()));
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and content, very important!
        out.flush(); // flush character output stream buffer

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();
    }

    private void sendResponse(PrintWriter out, BufferedOutputStream dataOut, String data) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Server: Java HTTP Server from Saurel : 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + "text/html");
        out.println("Content-length: " + data.length());
        out.println(); // blank line between headers and content, very important!
        out.println(data);

        out.flush(); // flush character output stream buffer
        dataOut.flush();
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

    // Supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";
        else if (fileRequested.endsWith(".css"))
            return "text/css";
        else if (fileRequested.endsWith(".js"))
            return "text/js";
        else if (fileRequested.endsWith(".jpeg") || fileRequested.endsWith(".jpg"))
            return "image/jpeg";
        else if (fileRequested.endsWith(".png"))
            return "image/png";
        else if (fileRequested.endsWith(".gif"))
            return "image/gif";
        else
            return "text/plain";
    }

}