
package org.eclipse.om2m.testinside.monitor;
 
import java.math.BigInteger;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.testinside.*;
import org.eclipse.om2m.testinside.util.*;

////////////////////////
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
//////////////////////////
 
public class Monitorinsidebackup {
 
	static CseService CSE;
	static String CSE_ID = Constants.CSE_ID;
	static String CSE_NAME = Constants.CSE_NAME;
	static String REQUEST_ENTITY = Constants.ADMIN_REQUESTING_ENTITY;
	public static String ipeId = "sample";
	public static String sensorId = "MY_SENSOR";
	public static int sensorValue = 0;
	static String DESCRIPTOR = "DESCRIPTOR";
	static String DATA = "DATA";
 
	private SensorListener sensorListener;

 
	public Monitorinsidebackup(CseService cseService){
		CSE = cseService;
	}
/////////////////////////////////////////
	public static class Console
	{	
		
		private String endVariable;
	     class MyHandler implements HttpHandler
	    {

	        public void handle(HttpExchange t)
	            throws IOException
	        {
	            String body = "";
	            try
	            {
	                InputStream is = t.getRequestBody();
	                int i;
	                while((i = is.read()) != -1) 
	                {
	                    char c = (char)i;
	                    body = (new StringBuilder(String.valueOf(body))).append(c).toString();
	                }
	            }
	            
	            catch(Exception e)
	            {
	                e.printStackTrace();
	            }
/*	            
	            /////////////////////new insert////////////////////////////
	           int index_begin = body.indexOf("obj");
	           int index_end = body.indexOf("/obj");
	           
	            String target = body.substring(index_begin, index_end);
	            
	           int index2_begin = target.indexOf("val");
	           int index2_end = target.indexOf("/>");
	           
	           String but = target.substring(index2_begin, index2_end);
	           
	           int index3_begin = but.indexOf(';');
	         
	           String fin = but.substring(index3_begin+1);
	           
	           int index4_begin = fin.indexOf('&');
	            
	           String end = fin.substring(0, index4_begin);
	           
	            ////////////////////send the value of end to sensorValue///////////////////////////////
	           
	         sensorValue = Integer.parseInt(end);
*/	         
	     //////////////////////////////////////////////////////////      
				// Create the data contentInstance
				String content = ObixUtil.getSensorDataRep(sensorValue);
				String targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DATA;
				ContentInstance cin = new ContentInstance();
				cin.setContent(content);
				cin.setContentInfo(MimeMediaType.OBIX);
				RequestSender.createContentInstance(targetId, cin);
  /////////////////////////////////////////////////////////////////////////////////      
	           	           
	            endVariable=body;
	            System.out.println("Received notification:");
	            System.out.println(body);
	            t.sendResponseHeaders(204, -1L);
	     
/////////////////////new insert////////////////////////////
           int index_begin = body.indexOf("obj");
           int index_end = body.indexOf("/obj");
           
            String target = body.substring(index_begin, index_end);
            
           int index2_begin = target.indexOf("val");
           int index2_end = target.indexOf("/>");
           
           String but = target.substring(index2_begin, index2_end);
           
           int index3_begin = but.indexOf(';');
         
           String fin = but.substring(index3_begin+1);
           
           int index4_begin = fin.indexOf('&');
            
           String end = fin.substring(0, index4_begin);
            ////////////////////send the value of end to sensorValue///////////////////////////////
           
         sensorValue = Integer.parseInt(end);
    //////////////////////print out the end     
         
         System.out.println("Received notification:");
         System.out.println(end);
     //////////////////////////////////////////////////////////      
	            
	            
	        }

	        MyHandler()
	        {
	        }
	        
	        
	    }
	     
	    public MyHandler getHandler(){
	    	
	    	return new MyHandler();
	    }
	     
	    public Console()
	    {
	    }
    ////////// turn it into a function/////////
	    public void openconsole(String var)
	        throws Exception
	    {
	        System.out.println("Starting server..");
	        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
	        server.createContext(context, new MyHandler());
	        server.start();
	        System.out.println((new StringBuilder("The server is now listening on\nPort: ")).append(port).append("\nContext: ").append(context).append("\n").toString());
	    }

	    private  int port = 1400;
	    private  String context = "/monitor";

	}
	/////////////////////////////////////
	
	
	
	public void start(){
		// Create sensor resources
		createSensorResources();
		// Listen for the sensor data
		listenToSensor();
 
	}
 
	public void stop(){
		if(sensorListener != null && sensorListener.isAlive()){
			sensorListener.stopThread();
		}
	}
 
	public void createSensorResources(){
		String targetId;
//		String targetId, content;
 
		targetId = "/" + CSE_ID + "/" + CSE_NAME;
		AE ae = new AE();
		ae.setRequestReachability(true);
		ae.setAppID(ipeId);
		ae.getPointOfAccess().add(ipeId);
		ResponsePrimitive response = RequestSender.createAE(ae, sensorId);
 
		if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)){
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId;
			Container cnt = new Container();
			cnt.setMaxNrOfInstances(BigInteger.valueOf(10));
			// Create the DESCRIPTOR container
			RequestSender.createContainer(targetId, DESCRIPTOR, cnt);
 
			// Create the DATA container
			RequestSender.createContainer(targetId, DATA, cnt);
 
			/*
			 
			// Create the description contentInstance
			content = ObixUtil.getSensorDescriptorRep(sensorId, ipeId);
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DESCRIPTOR;
			ContentInstance cin = new ContentInstance();
			cin.setContent(content);
			cin.setContentInfo(MimeMediaType.OBIX);
			RequestSender.createContentInstance(targetId, cin);
			
			*/
		}
	}
 
 
	public void listenToSensor(){
		sensorListener = new SensorListener();
		sensorListener.start();
	}
 
 
	private static class SensorListener extends Thread{
 
		private boolean running = true;
 
		@Override
		//////////////////////new run function////////////////////
		
		
		
		
		
		
		//////////////////////////////////////////////////////////
	
		public void run() {
//			while(running){
				// Simulate a random measurement of the sensor
		//		sensorValue = 10 + (int) (Math.random() * 100);
				
				// open the console to wait the notification
				Console console = new Console();	
			
			try {
					console.openconsole(console.endVariable);
				} 				
				   catch(Exception e)
	            {
	      //         e.printStackTrace();
	            }
			
/* 
				// Create the data contentInstance
				String content = ObixUtil.getSensorDataRep(sensorValue);
				String targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DATA;
				ContentInstance cin = new ContentInstance();
				cin.setContent(content);
				cin.setContentInfo(MimeMediaType.OBIX);
				RequestSender.createContentInstance(targetId, cin);
 
*/			

			/*
				// Wait for 2 seconds
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e){
					e.printStackTrace();
				}	
				
			*/
//			}
		}


		public void stopThread(){
			running = false;
		}
 
	}
 
 
}

