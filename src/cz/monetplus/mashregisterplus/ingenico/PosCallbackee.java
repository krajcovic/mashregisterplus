package cz.monetplus.mashregisterplus.ingenico;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
//import cz.monetplus.blueterm.PosCallbacks;

//public class PosCallbackee implements PosCallbacks {
//
//	private static final String TAG = "PosCallbackee";
//	
//	/**
//	 * 
//	 */
//	private Activity activity;
//	
//	/**
//	 * 
//	 */
//	private Context context;
//
//	/**
//	 * 
//	 */
//	private List<String> ticket = new ArrayList<String>();
//	
//	/**
//	 * @param activity
//	 * @param context
//	 */
//	public PosCallbackee(Activity activity, Context context) {
//		this.activity = activity;
//		this.context = context;
//	}
//
//	@Override
//	public Boolean ticketLine(final String line) {
//		getTicket().add(line);
//		
//		return Boolean.TRUE;
//	}
//	
//	@Override
//	public void ticketFinish() {
//		// Ukonci listek	
//		Log.i(TAG, "Call cut on printer");
//	}
//	
//	@Override
//	public void progress(final String line) {
//		// Zaloguj si kam chces
//		Log.i(TAG, line);
//	}
//
//	public void progressToast(final String line) {
//		activity.runOnUiThread(new Runnable() {
//			public void run() {
//				Toast.makeText(context, line, Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//
//	@Override
//	public Boolean isSingOk() {
//		return Boolean.TRUE;
//	}
//
//	@Override
//	public Boolean isConnectivity() {
//		// Zatim se nepouziva
//		return Boolean.TRUE;
//	}
//
//	public List<String> getTicket() {
//		return ticket;
//	}
//
//	public void setTicket(List<String> ticket) {
//		this.ticket = ticket;
//	}
//}
