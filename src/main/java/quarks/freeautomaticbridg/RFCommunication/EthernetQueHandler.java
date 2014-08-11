/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quarks.freeautomaticbridg.RFCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.log4j.Logger;
import quarks.freeaotomaticbridge.protocoll.PackageBuilder;
import quarks.freeaotomaticbridge.protocoll.WorkingSet;
import quarks.freeautomaticbridg.CommunicationSubSystem;
import quarks.freeautomaticbridg.JSon.SimpleJson;

/**
 *
 * @author eobs
 */
public class EthernetQueHandler implements CommunicationSubSystem {

    private static Logger log = Logger.getLogger(EthernetQueHandler.class);

    private LinkedList<WorkingSet> que = new LinkedList<WorkingSet>();
    private HashMap<Integer, WorkingSet> responseMap = new HashMap<Integer, WorkingSet>();
    private InputStream rfIn;
    private OutputStream rfOut;

    private String name;
    private boolean reconnect = false;
    private String host;
    private int port;
    private Socket socket;

    public static final int TIMEOUT = 5000;

    public EthernetQueHandler() {
        log.info("new instance");

    }

    @Override
    public void run() {

        log.debug("Communication Que for" + this.name + " is starting");

        while (true) {

            try {

                if (que.size() == 0) {
                    //save cpu time
                    Thread.sleep(100);

                } // if the que has somthing todo do it
                else {
                // make sure that the connection are open

                    if (this.rfIn == null) {
                        connect();
                    }

                    log.debug("Send Package Qusize:" + this.que.size());
                    log.debug("response Map Size:" + this.responseMap.size());
                    WorkingSet ws = this.que.remove();
                    this.responseMap.put((int) ws.getUid(), ws);

                    try {

                        sendPackage(ws.getPackageStructure().getPackage());
                        log.debug("Wait for response");
                        // and wait for response
                        byte respPck[] = new byte[14];
                        int l = this.rfIn.read(respPck);

                        // if reconnect is on close connection 
                        if (this.reconnect) {
                            this.socket.close();
                            //wait a secont
                            log.debug("Connection Closed");
                            this.rfIn = null;
                            this.rfOut = null;
                            Thread.sleep(1000);

                        }

                        if (l != 14) {
                            log.error("Wrong response");
                        } else {
                            recivingPackage(respPck);

                        }

                    } catch (InterruptedIOException ex) {
                        this.rfIn = null;
                        this.rfOut = null;
                        log.error("Time out reconnect");
                    } catch (IOException ex) {
                        // reconnect 
                        this.rfIn = null;
                        this.rfOut = null;

                        log.error("Connection error reconnect", ex);
                    }
                      

                }

            } catch (InterruptedException ex) {
                log.error(ex);

            }

        }

    }

    /**
     * Adds a Working set to the que
     *
     * @param workingSet
     */
    public void putWorkingSet(WorkingSet workingSet) {
        que.add(workingSet);

    }

    private void sendPackage(byte[] packageStructure) throws IOException, InterruptedIOException {
        log.debug("Sending Package");
        this.rfOut.write(packageStructure);
    }

    private void recivingPackage(byte[] packageStructure) throws IOException, InterruptedIOException {
        PackageBuilder pack = new PackageBuilder(packageStructure);
        log.debug("Reciving Package :" + pack.getUid());

        WorkingSet ws = this.responseMap.remove((int) pack.getUid());
        SimpleJson json = new SimpleJson();
        json.addValue("resp", "OK");
        if (ws != null) {
            byte cm = pack.getCommand();
            log.debug("Reciving CMD :" + cm);
            
            if (cm == PackageBuilder.CMD_RES+PackageBuilder.TYPE_SHORT) {

                json.addValue("value1", String.valueOf(pack.getShortValue(0)));
                 json.addValue("value2", String.valueOf(pack.getShortValue(1)));
                  json.addValue("value3", String.valueOf(pack.getShortValue(2)));
                   json.addValue("value4", String.valueOf(pack.getShortValue(3)));
                  json.addValue("type", "SHORT");
                
                
                // @todo implement all types 
            } else if (cm == PackageBuilder.CMD_RES+PackageBuilder.TYPE_INT)
            {
                json.addValue("value1", String.valueOf(pack.getIntValue(0)));
                json.addValue("value2", String.valueOf(pack.getIntValue(1)));
                json.addValue("type", "INT");
            }
            
            
            else {
                log.debug("No Response");

                json.addValue("type", "NA");
               

            }

            Date time = new Date();
            json.addValue("Time", time.toString());

            json.build();
            ws.getHandler().jsonResponse(json);

        }
    }

    /**
     * This Method is configure the EthernetQueHandler the property file should
     * contain the following notation :
     *
     * node.<NodeName>.host : Ethernet Host adress node.<NodeName>.port :
     * Ethernet Port node.<NodeName>.connection : <hold|reconnect>
     *
     * reconnect:means that after a communication session the Bridge is
     * reconnecting hold : means the bridge trys to keep the connection just in
     * a failer situation the bridg is reconnecting automaticly
     *
     * @param prefix
     * @param properties
     * @return
     */
    @Override
    public boolean configSystem(String prefix, Properties properties) {

        log.info("Open Connection to Ethernet driver instance name:" + prefix);
        this.name = prefix;

        this.host = properties.getProperty("node." + prefix + ".host");
        String portS = properties.getProperty("node." + prefix + ".port", "111");
        this.port = Integer.parseInt(portS);

        if (properties.getProperty("node." + prefix + ".connection").compareTo("hold") == 0) {
            this.reconnect = false;
            log.info("Connection should be hold ");
            if (connect()) {
                log.info("Connectet");
            } else {
                log.error("Connection faild");
                return false;
            }
        } else {
            this.reconnect = true;
        }

        return true;

    }

    @Override
    public String getName(String name) {
        return this.name;
    }

    private boolean connect() {
        int RETRY = 10;
        for (int i = 0; i <= RETRY; i++) {
            try {
                log.info(name + " Try to connectet to --> " + host + ":" + port);

                socket = new Socket();

                socket.connect(new InetSocketAddress(host, port), TIMEOUT);

                this.socket.setSoTimeout(TIMEOUT);

                this.rfIn = socket.getInputStream();
                this.rfOut = socket.getOutputStream();

            } catch (SocketTimeoutException ex) {
                log.error("Connection faild Timout try " + i, ex);

            } catch (IOException ex) {
                log.error("Connection faild try " + i, ex);

            }
            return true;
        }

        return false;

    }

}
