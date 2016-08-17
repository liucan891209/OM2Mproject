package org.eclipse.om2m.mnrun.monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.mnrun.*;
import org.eclipse.om2m.mnrun.util.*;

public class Monitor {

	static CseService CSE;
	static String CSE_ID = Constants.CSE_ID;
	static String CSE_NAME = Constants.CSE_NAME;
	static String REQUEST_ENTITY = Constants.ADMIN_REQUESTING_ENTITY;
	public static String ipeId = "sample";
	public static String sensorId = "MY_GPS";
	public static boolean actuatorValue = false;
	// public static int sensorValue = 0;
	// ///////////changer sensorValue/////////
	public static String sensorValue;

	static String DESCRIPTOR = "DESCRIPTOR";
	static String DATA = "DATA";
	static String SUB = "SUB";
	// /////////new insert/////////
	public static String ret;
	private SensorListener sensorListener;

	public Monitor(CseService cseService) {
		CSE = cseService;
	}

	public void start() {
		// Create sensor resources
//		createSensorResources();
		// Listen for the sensor data
		listenToSensor();

	}

	public void stop() {
		if (sensorListener != null && sensorListener.isAlive()) {
			sensorListener.stopThread();
		}
	}
//////////////////Create a python running function//////////////
	
	public static String pythonrun() throws ScriptException, IOException {
/*
	    StringWriter writer = new StringWriter(); //ouput will be stored here

	    ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptContext context = new SimpleScriptContext();

	    context.setWriter(writer); //configures output redirection
	    ScriptEngine engine = manager.getEngineByName("python");
	    engine.eval(new FileReader("/home/pi/pythonfile/GPS.py"), context);
	    String out = writer.toString();
*/
		
///////////new try///////////////////////
		String pythonScriptPath = "/home/pi/pythonfile/GPS.py";
	    String[] cmd = { "python", pythonScriptPath };

	    // create runtime to execute external command
	    ProcessBuilder pb = new ProcessBuilder(cmd);
	    if(true){
	        Process p = pb.start();
	        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        ret = new String(in.readLine()).toString();
	        System.out.println("value is : "+ret);
	    }
	        return ret;
//	    System.out.println(writer.toString()); 
	}	
	
////////////////////////////////////////////////////////////////
	public void createSensorResources() {
		String targetId, content;

		targetId = "/" + CSE_ID + "/" + CSE_NAME;
		AE ae = new AE();
		ae.setRequestReachability(true);
		ae.setAppID(ipeId);
		ae.getPointOfAccess().add(ipeId);
		ResponsePrimitive response = RequestSender.createAE(ae, sensorId);

		if (response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)) {
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId;
						
			/*
			 * // Create the subscription for the monitor in IN targetId = "/" +
			 * CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DATA;
			 * ContentInstance sub = new ContentInstance();
			 * sub.setContent(content); sub.setContentInfo(MimeMediaType.OBIX);
			 * RequestSender.createSubscription(targetId,);
	
			// /////////////////////////////////////
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/"
					+ DATA;
			Subscription sub = new Subscription();
			sub.setSubscriberURI(targetId);
			sub.getNotificationURI().add("http://10.42.14.223:1400/Monitor");
			sub.setNotificationContentType(BigInteger.valueOf(2));
			RequestSender.createSubscription(targetId,SUB,sub);
			 */			
		}
	}

	public void listenToSensor() {
		sensorListener = new SensorListener();
		sensorListener.start();
	}

	private static class SensorListener extends Thread {

		private boolean running = true;

		@Override
		public void run() {
			while (running) {

				// get gps data from python scrpit
				try {					
					sensorValue = pythonrun();
					System.out.println(sensorValue);
				} catch (ScriptException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Create the data contentInstance
				String content = ObixUtil.getSensorDataRep(sensorValue);
				String targetId = "/" + CSE_ID + "/" + CSE_NAME + "/"
						+ sensorId + "/" + DATA;
				ContentInstance cin = new ContentInstance();
				cin.setContent(content);
				cin.setContentInfo(MimeMediaType.OBIX);
				RequestSender.createContentInstance(targetId, cin);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

		public void stopThread() {
			running = false;
		}

	}
}