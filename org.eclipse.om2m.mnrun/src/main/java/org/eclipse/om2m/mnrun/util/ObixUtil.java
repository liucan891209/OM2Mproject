package org.eclipse.om2m.mnrun.util;
 
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.obix.Bool;
import org.eclipse.om2m.commons.obix.Contract;
import org.eclipse.om2m.commons.obix.Int;
import org.eclipse.om2m.commons.obix.Str;
import org.eclipse.om2m.commons.obix.Obj;
import org.eclipse.om2m.commons.obix.Op;
import org.eclipse.om2m.commons.obix.Uri;
import org.eclipse.om2m.commons.obix.io.ObixEncoder;
 
public class ObixUtil {
 
	public static String getSensorDescriptorRep(String appId, String ipeId) {
		String prefix = "/" + Constants.CSE_ID + "/" + Constants.CSE_NAME + "/" + appId;
		Obj obj = new Obj();
 
		Op opGet = new Op();
		opGet.setName("GET");
		opGet.setHref(new Uri(prefix + "/DATA/la"));
		opGet.setIs(new Contract("retrieve"));
		obj.add(opGet);
 
		Op opGetDirect = new Op();
		opGetDirect.setName("GET(Direct)");
		opGetDirect.setHref(new Uri(prefix + "?op=get"));
		opGetDirect.setIs(new Contract("execute"));
		obj.add(opGetDirect);
 
		return ObixEncoder.toString(obj);
	}
 
 /*
	public static String getSensorDataRep(int value) {
		Obj obj = new Obj();
		obj.add(new Int("data", value));
		return ObixEncoder.toString(obj);
	}
*/	
	///////////////change to string string
	public static String getSensorDataRep(String value) {
		Obj obj = new Obj();
		
		obj.add(new Str("data", value));

		return ObixEncoder.toString(obj);
	}
	
	///////////////////new insert for subscription content//////////////
	public static String getsubscriptionRep(int value) {
		Obj obj = new Obj();
		obj.add(new Int("data", value));
		return ObixEncoder.toString(obj);
	}
	/////////////////////////////////
 
}