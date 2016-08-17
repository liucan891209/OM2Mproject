package org.eclipse.om2m.testmnside.monitor;
 
import java.math.BigInteger;
 
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.testmnside.*;
import org.eclipse.om2m.testmnside.util.*;
 
public class Monitor {
 
	static CseService CSE;
	static String CSE_ID = Constants.CSE_ID;
	static String CSE_NAME = Constants.CSE_NAME;
	static String REQUEST_ENTITY = Constants.ADMIN_REQUESTING_ENTITY;
	public static String ipeId = "sample";
	public static String sensorId = "MY_SENSOR";
	public static boolean actuatorValue = false;
	public static int sensorValue = 0;
	static String DESCRIPTOR = "DESCRIPTOR";
	static String DATA = "DATA";
 
	private SensorListener sensorListener;
 
	public Monitor(CseService cseService){
		CSE = cseService;
	}
 
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
		String targetId, content;
 
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
 
			// Create the description contentInstance
			content = ObixUtil.getSensorDescriptorRep(sensorId, ipeId);
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DESCRIPTOR;
			ContentInstance cin = new ContentInstance();
			cin.setContent(content);
			cin.setContentInfo(MimeMediaType.OBIX);
			RequestSender.createContentInstance(targetId, cin);
/*			
			// Create the subscription for the monitor in IN
			targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DATA;
			ContentInstance sub = new ContentInstance();
			sub.setContent(content);
			sub.setContentInfo(MimeMediaType.OBIX);
			RequestSender.createSubscription(targetId,);
*/			
			///////////////////////////////////////
		}
	}

 
	public void listenToSensor(){
		sensorListener = new SensorListener();
		sensorListener.start();
	}
  
	private static class SensorListener extends Thread{
 
		private boolean running = true;
 
		@Override
		public void run() {
			while(running){
				// Simulate a random measurement of the sensor
				sensorValue = 10 + (int) (Math.random() * 100);
	///////////////////////////////new insert///////////////////////////////////
	//			get gps data from uart port ang after paraser
	UART gpsdata = new UART();	
	
	////////////////////////////////////////////////////////////////////////////			
 
				// Create the data contentInstance
				String content = ObixUtil.getSensorDataRep(sensorValue);
				String targetId = "/" + CSE_ID + "/" + CSE_NAME + "/" + sensorId + "/" + DATA;
				ContentInstance cin = new ContentInstance();
				cin.setContent(content);
				cin.setContentInfo(MimeMediaType.OBIX);
				RequestSender.createContentInstance(targetId, cin);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				
			}
 
		}
 
		public void stopThread(){
			running = false;
		}
 
	} 
}