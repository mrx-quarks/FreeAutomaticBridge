/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeautomaticbridg;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import quarks.freeaotomaticbridge.protocoll.PackageBuilder;
import quarks.freeaotomaticbridge.protocoll.WorkingSet;


/**
 *
 * @author eobs
 */
public class CommunicationServer implements  Daemon, HtmlRequest{

    /**
     * @param args the command line arguments
     */
    
     private static Logger log = Logger.getLogger("Main");
     
     public static Properties config;
     
      
      
      private HashMap<String,CommunicationSubSystem> communicationSubSystems=new HashMap<String, CommunicationSubSystem>();
     
    public static void main(String[] args) throws IOException, InterruptedException {
       
        
        if (args!=null)
        {
        config=new Properties();
       
        config.load(new FileInputStream(args[0]));
        PropertyConfigurator.configure(config);
        log.info("Start as Programm");
        CommunicationServer server=new CommunicationServer();
        server.startServer(666);
        }
        
      
    }

  public  CommunicationServer(){
        
      
      ClassLoader classLoader = CommunicationServer.class.getClassLoader();
      
         for (Iterator<Object> it = config.keySet().iterator(); it.hasNext();) {
             String key = (String) it.next();
             
             
             if (key.startsWith("node"))
             {
                  String name=key.split("\\.")[1];
                  
                  if (!this.communicationSubSystems.containsKey(name))
                  {
                      try {
                          Class driver=classLoader.loadClass(config.getProperty("node."+name+".class"));
                          
                          CommunicationSubSystem comSub=(CommunicationSubSystem) driver.newInstance();
                          
                          comSub.configSystem(name, config);
                          
                          this.communicationSubSystems.put(name, comSub);
                          
                          log.info("Load Communication Subsysten: "+name);
                          
                      } catch (ClassNotFoundException ex) {
                          log.error(name);
                      } catch (InstantiationException ex) {
                         log.error(name);
                      } catch (IllegalAccessException ex) {
                         log.error(name);
                      }
                       
                  }
                  
             }
                         
         }
          
                 
                 
        
         
    }
   
   
   public void startServer(int port) throws IOException, InterruptedException
   {
       
       //init CommunicationSubsystems
       
       for (String subSys:this.communicationSubSystems.keySet())
       {
           CommunicationSubSystem comSub=this.communicationSubSystems.get(subSys);
        Thread workingQue=new Thread(comSub);
         workingQue.start();
       }
       
        log.info("Start listening on port "+port);
        
        ServerSocket server=new ServerSocket(port) ;
        
    
        while (true)
        {
         Socket socket=server.accept();
         
         HtmlGetRequestHandler htmlHandler=new HtmlGetRequestHandler(socket, this);
         
         Thread thread=new Thread(htmlHandler);
         thread.start();
         Thread.sleep(5);
         
              
        }
   }

    @Override
    public boolean accept(HashMap<String, String> requestMap,HtmlGetRequestHandler handler) {
       /* 
        if (!requestMap.containsKey("addr")) 
        {
            try {
                handler.errorResponse(510);
            } catch (IOException ex) {
               log.error(ex);
            }
            return false;
        }
               */
       return true;
    }

  
    /**
     * Is processing the html get request
     * 
     * Supportet is the following scema
     *  <br> host:port/rest.api?<addr=(number ignored by ethernet nodss )>&node=(nodename)&port=[port]&command=[CMD_SET[..]|CMD_GET[..]]
     * 
     * <br> CMD_SET : type=RGB expectet three values r=0-255 & g=... and b 
     * <br>         : type=SW_AND send a shortvalue with hint to alter the states this should be implementet as an logical or
     * <br>         : type=SW_OR  send a shortvalue with hint to reset the states completly expectet &value=[integer]
     * <br>         a single Switche port can handle up to 16 switches using bit operation (16 bit) expectet &value=[integer]
     * 
     *
     * 

     * @param requestMap
     * @throws java.io.IOException
     */
    public void response(HashMap<String, String> requestMap,HtmlGetRequestHandler handler) throws IOException {
        
        
        byte   dest_addr=Byte.parseByte(requestMap.get("addr"));
        
      
        
        CommunicationSubSystem comSub=this.communicationSubSystems.get(requestMap.get("node"));
        
        
        if (requestMap.get("command").compareTo("CMD_SET")==0)
        {
            String type=requestMap.get("type");
            
            // send RGB SET
            if (type.compareTo("RGB")==0)
            {
                byte port=Byte.parseByte(requestMap.get("port"));
                int r=Integer.parseInt(requestMap.get("r")); 
                int g=Integer.parseInt(requestMap.get("g")); 
                int b=Integer.parseInt(requestMap.get("b")); 
                
                log.info("Recive CMD_SET: for node:"+dest_addr+":"+port+" RGB ");
 
                PackageBuilder pack=new PackageBuilder( dest_addr, (byte) 0, port  , (byte) PackageBuilder.CMD_SET,r,g,b);
                
                 WorkingSet ws=new WorkingSet(handler,pack );
                comSub.putWorkingSet(ws);
                
            }
            
                if (type.compareTo("SW_AND")==0)
            {
                byte port=Byte.parseByte(requestMap.get("port"));
                int value=Integer.parseInt(requestMap.get("value")); 
                
                
                log.info("Recive CMD_SET: for node:"+dest_addr+":"+port+" SW_AND ");
               byte command=(byte) PackageBuilder.CMD_SET+(byte) PackageBuilder.TYPE_SW_AND;
                PackageBuilder pack=new PackageBuilder( dest_addr, (byte) 0, port  ,command ,(short) value,(short) 0,(short) 0, (short) 0);
                
                 WorkingSet ws=new WorkingSet(handler,pack );
                comSub.putWorkingSet(ws);
                
            }
                
                       if (type.compareTo("SW_OR")==0)
            {
                byte port=Byte.parseByte(requestMap.get("port"));
                int value=Integer.parseInt(requestMap.get("value")); 
                
                
                log.info("Recive CMD_SET: for node:"+dest_addr+":"+port+" SW_AND ");
                byte command=(byte) PackageBuilder.CMD_SET+(byte) PackageBuilder.TYPE_SW_OR;
                PackageBuilder pack=new PackageBuilder( dest_addr, (byte) 0, port  , command,(short) value,(short) 0,(short) 0, (short) 0);
                
                 WorkingSet ws=new WorkingSet(handler,pack );
                comSub.putWorkingSet(ws);
         
                
            }
            
             
            
        }
        
        
        if (requestMap.get("command").compareTo("CMD_GET")==0)
        {
            
            
            byte port=Byte.parseByte(requestMap.get("port"));
            
             
            
            
            
             log.info("Recive CMD_GET: for node:"+dest_addr+":"+port);
            
            PackageBuilder pack=new PackageBuilder( dest_addr, (byte) 0, port  , (byte) PackageBuilder.CMD_GET, (short) 0 , (short) 0, (short) 0, (short)0);
                      
            WorkingSet ws=new WorkingSet(handler,pack );
            comSub.putWorkingSet(ws);
        }
        
      
     
    }

    @Override
    public void init(DaemonContext dc) throws DaemonInitException, Exception {
       
        config=new Properties();
       
        config.load(new FileInputStream(dc.getArguments()[0]));
        
        PropertyConfigurator.configure(config);
        log.info("Start as Programm");
        CommunicationServer server=new CommunicationServer();
        server.startServer(666);
        
    }

    @Override
    public void start() throws Exception {
        main(null);
    }

    @Override
    public void stop() throws Exception {
        
        
         log.info("Service Stoped");
         System.exit(0);
         
         
    }

    @Override
    public void destroy() {
        log.info("Service Stoped");
        System.exit(0);
    }
      
    
    
    
}
