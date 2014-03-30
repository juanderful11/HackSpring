/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;


class SampleListener2 extends Listener {
	private boolean state;
	private int stateCount = 0;
	private int frameCount = 0;
	
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
    	
    	if (frameCount > 1)
    	{
	        frameCount = 0;
	        // Get the most recent frame and report some basic information
	        Frame frame = controller.frame();
	        Frame prevFrame = controller.frame(1);
	
	        if (!frame.hands().isEmpty()) {
	            // Get the first hand
	            Hand hand = frame.hands().get(0);
	            
	            //open or close
	            	FingerList fingers = hand.fingers();
	                // Calculate the hand's average finger tip position
	                float avgSeparation = 0;
	                float avgVelocity = 0;
	                for (Finger finger : fingers) {
	                	avgSeparation += finger.direction().angleTo(Vector.yAxis());
	                	avgVelocity += finger.tipVelocity().magnitude();
	                }
	                 avgSeparation = avgSeparation/(fingers.count());
	                 avgVelocity = avgVelocity/(fingers.count());
	                 float palmNormalAngle = hand.palmNormal().angleTo(Vector.zAxis());
	                 float roll = hand.palmNormal().roll();
	                 
	                 //palm direction
	                 if (roll > -.3 && roll < .3) //normal direction
	                 {
	                 	System.out.println("regular");
	                    
	                    if (fingers.count() >= 2)
	                    {
	                    	
	                    	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=open&finger3=open&spinner=center","");
	                    	System.out.println("open");
	                    	state = true;
	                    }
	                    else
	                    {
	                    	System.out.println("close");
	                    	state = false;
	                    	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=close&finger3=close&spinner=center","");
	                    }
	                 } 
	                 else // hand is turned
	                 {
	                	 if (roll <= -.3) {
	                		 System.out.println("left");
	                    	 if (fingers.count() >= 2)
	                         {
	                         	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=open&finger3=open&spinner=left","");
	                         	System.out.println("open");
	                         	state = true;
	                         }
	                         else
	                         {
	                         	System.out.println("close");
	                         	state = false;
	                         	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=close&finger3=close&spinner=left","");
	                         }
	                		 
	                	 } else {
	                		 System.out.println("right");
	                    	 if (fingers.count() >= 2)
	                         {
	                         	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=open&finger3=open&spinner=right","");
	                         	System.out.println("open");
	                         	state = true;
	                         }
	                         else
	                         {
	                         	System.out.println("close");
	                         	state = false;
	                         	executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=close&finger3=close&spinner=right","");
	                         }
	                	 }
	                 }
	        }
    	}
    	else 
    		frameCount++;    
    }

    
    public static String executePost(String targetURL, String urlParameters)
    {
      URL url;
      HttpURLConnection connection = null;  
      try {
        //Create connection
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", 
             "application/x-www-form-urlencoded");
  			
        connection.setRequestProperty("Content-Length", "" + 
                 Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");  
  			
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
        wr.writeBytes (urlParameters);
        wr.flush ();
        wr.close ();

        //Get Response	
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer(); 
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();
        return response.toString();

      } catch (Exception e) {

        e.printStackTrace();
        return null;

      } finally {

        if(connection != null) {
          connection.disconnect(); 
        }
      }
    }
}

class test {
    public static void main(String[] args) {
        // Create a sample listener and controller
        SampleListener2 listener = new SampleListener2();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}
