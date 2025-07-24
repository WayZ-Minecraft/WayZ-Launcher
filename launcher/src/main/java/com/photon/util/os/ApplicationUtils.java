package com.photon.util.os;

import java.util.Timer;
import java.util.TimerTask;

public class ApplicationUtils {
    
	public static void exitProperly() { exitProperly(1500L); }
	
    public static void exitProperly(long time) {
		new Timer().schedule(new TimerTask() {
			public void run() { System.exit(0); }
		}, time);
	}
}
