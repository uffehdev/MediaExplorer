package se.hdev.MediaExplorer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MediaExplorerActivity extends Activity implements OnClickListener {
	private String TAG = "Main";
    Context c;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnTest = (Button) findViewById(R.id.btn_Connect);
        c = this;
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.btn_Connect) {
			sendStarterPacket();
		}
	}
    
private static final int DISCOVERY_PORT = 9050;
	
	void sendStarterPacket() {
		Log.d(TAG, "SKASKICKA");
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(DISCOVERY_PORT);
			socket.setBroadcast(true);
			String data = "START MEDIASERVER";
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
			    getBroadcastAddress(), DISCOVERY_PORT);
			socket.send(packet);
			socket.close();
			Log.d(TAG, "SKICKAT UDP PACKET");
			Toast.makeText(this, "Skickade packet: " + data, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "TRY NY THREAD");
			//newThread();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "FEL MED S€NDING1: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "FEL MED S€NDING2: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG, "FEL MED S€NDING3: " + e.toString());
			e.printStackTrace();
		}
		
		
		/*a
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);*/
	}
	
	void newThread() {
		Thread thrRecive = new Thread(){
			public void run() {
				Log.d(TAG, "NY THREAD");
				try {
					DatagramSocket socket;
					socket = new DatagramSocket(DISCOVERY_PORT);
					socket.setBroadcast(true);
					
					byte[] buf = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					socket.close();
					Log.d(TAG, "HEJSAN: " + packet.toString());
					//Toast.makeText(this, "TOGETMOT: " + packet.toString(), Toast.LENGTH_SHORT).show();
					
				} catch (Exception e) {
					Log.d(TAG, "NÅGOT GICK FEL MED ATT TAEMOT: " + e.toString());
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thrRecive.start();
	}
	
	InetAddress getBroadcastAddress() throws IOException {
	    WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);//  Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}
}