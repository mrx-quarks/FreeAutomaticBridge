/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quarks.freeautomaticbridg;

import quarks.freeautomaticbridg.JSon.JSonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author eobs
 */
public class HtmlGetRequestHandler implements Runnable {

    private static Logger log = Logger.getLogger(HtmlGetRequestHandler.class);

    private HtmlRequest handler;
    private Socket socket;
    private HashMap<String, String> requestMapping;

    //private HashMap<String,String> requestMap;
    public HtmlGetRequestHandler(Socket socket, HtmlRequest requestHandler) {
        this.handler = requestHandler;
        this.socket = socket;
        this.requestMapping = new HashMap<String, String>();
    }

    @Override
    public void run() {

        try {
            if (parseHeader(handler)) {
                handler.response(requestMapping, this);
                 
            } else {

                socket.close();
            }

        } catch (Exception ex) {
            log.error("Request handle Error", ex);
        }

    }

    private boolean parseHeader(HtmlRequest request) throws Exception {
        log.debug("Parsing the HTML header");

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        String getRequest;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {

            if (line.trim().length() == 0) {
                break;
            }

            if (line.startsWith("POST")) {
                log.warn("post is not supportet");
                return false;
            }

            if (line.startsWith("GET")) {
                
                if (!line.split("\\?")[0].endsWith("rest.api")) {

                    log.error("Application not aceptet just rest.api is acceptet");
                    errorResponse(500);
                    return false;

                }
                parseRequestParameter(line);
                log.info("Request Type GET");

            }

            if (line.startsWith("Host")) {
                String host = line.split(":")[1];
                this.requestMapping.put("host", host);
                log.info("Request from:" + host);
            }

            lineNumber++;
            log.debug(lineNumber + " : " + line);
        }
        return handler.accept(requestMapping,this);
    }

    private void parseRequestParameter(String getLine) {
        String requests = getLine.split("\\?")[1];

        // check the application request
        //cleanup the HTTP** part
        int li = requests.lastIndexOf("HTTP");

        requests = requests.substring(0, li).trim();

        String parameters[] = requests.split("&");

        for (String parameter : parameters) {
            String propValue[] = parameter.split("=");

            this.requestMapping.put(propValue[0].trim(), propValue[1].trim());

            log.debug("Found Parameter:" + parameter);

        }

    }

    public void errorResponse(int httpErrNr) throws IOException {
        log.info("Response Error:" + httpErrNr);

        PrintStream respOut = new PrintStream(socket.getOutputStream());

        respOut.print("HTTP/1.1 " + httpErrNr + " Error\r\n");

        respOut.print("\r\n");

        socket.close();

        log.info("Connection Closed");
    }

    public void jsonResponse(JSonObject jSon) throws IOException {
        log.info("Response JSon Object");

        PrintStream jsonOut = new PrintStream(socket.getOutputStream());

        jsonOut.print("HTTP/1.1 200 OK\r\n");

        jsonOut.print("Content-Type: application/json\r\n");
        jsonOut.print("Content-Length: " + jSon.getContentLengt() + "\r\n");

        jsonOut.print("\r\n");

        socket.getOutputStream().write(jSon.getContent());

        socket.close();

        log.info("Connection Closed");
    }

}
