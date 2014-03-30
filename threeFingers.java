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


class SampleListener4 extends Listener {
	private boolean state;
	private int stateCount = 0;
	
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
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        Frame prevFrame = controller.frame(1);

        if (!frame.hands().isEmpty()) {
            // Get the first hand
           Hand hand = frame.hands().get(0);
           FingerList fingers = hand.fingers();
           float avgXpos = 0;
           for (Finger finger:fingers)
           {
        	   avgXpos += finger.stabilizedTipPosition().getX();
           }
           avgXpos = avgXpos/fingers.count();
           System.out.println("average X position = " + avgXpos); 
           
           if (fingers.count() == 3)
           {
        	   System.out.println("3");
           	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=open&finger3=open&spinner=center","");
           }
           else if (fingers.count() == 2)
           {
        	   if (avgXpos < -25)
        	   {
        		   System.out.println("right down");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=open&finger3=close&spinner=center","");  
        	   }
        	   else if(avgXpos < -15)
        	   {
        		   System.out.println("middle down");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=close&finger3=open&spinner=center","");
        	   }
        	   else
        	   {
        		   System.out.println("left down");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=open&finger3=open&spinner=center","");
        	   }
           }
           else if (fingers.count() == 1)
           {
        	   if (avgXpos < -50)
        	   {
        		   System.out.println("left up");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=open&finger2=close&finger3=close&spinner=center","");  
        	   }
        	   else if(avgXpos < -20)
        	   {
        		   System.out.println("middle up");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=open&finger3=close&spinner=center","");
        	   }
        	   else
        	   {
        		   System.out.println("right up");
               	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=close&finger3=open&spinner=center","");
        	   }
        	   
           }
           else
           {
        	   System.out.println("all down");
           	   executePost("https://agent.electricimp.com/dPaNRLhSUoqc?finger1=close&finger2=close&finger3=close&spinner=center","");
        	   
           }
               
        }
         
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

class threeFingers {
    public static void main(String[] args) {
        // Create a sample listener and controller
        SampleListener4 listener = new SampleListener4();
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


