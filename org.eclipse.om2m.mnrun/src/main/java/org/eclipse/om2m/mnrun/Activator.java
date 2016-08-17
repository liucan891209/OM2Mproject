package org.eclipse.om2m.mnrun;

import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.interworking.service.InterworkingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.eclipse.om2m.mnrun.monitor.*;
import org.eclipse.om2m.mnrun.controller.*;
 
public class Activator implements BundleActivator {
 
	private static BundleContext context;
 
	private Monitor monitor;
 
	static BundleContext getContext() {
		return context;
	}
 
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println("Starting Sample Ipe");
		new ServiceTracker<Object, Object>(bundleContext, CseService.class.getName(), null){
 
			@Override
			public Object addingService(ServiceReference<Object> reference) {
				/////// changer cse à final///////
				final CseService cse = (CseService) this.context.getService(reference);
				if(cse != null){
					RequestSender.CSE = cse;
					new Thread(){
						public void run() {
							monitor = new Monitor(cse);
							monitor.start();
						};
					}.start();
				}
				return cse;	
			}
		}.open();
 
		bundleContext.registerService(InterworkingService.class.getName(), new Controller(), null);
 
	}
 
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		System.out.println("Stopping Sample Ipe");
		if(monitor != null){
			monitor.stop();
		}
	}
 
}
