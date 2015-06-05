package cz.monetplus.mashregisterplus.ingenico;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import cz.monetplus.blueterm.MonetBTAPI;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.mashregisterplus.ingenico.R;
import cz.monetplus.mashregisterplus.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ServisActivity extends AdActivity {
	private static final int ACTIVITY_INTENT_ID = 33333;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 33334;

	private TextView mAnswerTextView;
	private TextView blueHwAddress;

	DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servis);

		super.adAddView();
		setButtons(false);

		mAnswerTextView = (TextView) findViewById(R.id.textAnswer);

		mAnswerTextView.setFocusableInTouchMode(true);
		mAnswerTextView.requestFocus();

		serviceButtons();

		Button buttonSelect = (Button) findViewById(R.id.buttonHwSelect);
		buttonSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getApplicationContext(),
						DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_INSECURE);

			}
		});

		blueHwAddress = (TextView) findViewById(R.id.textViewHw);
	}

	private void serviceButtons() {
		Button pinButton = (Button) findViewById(R.id.buttonGetPin);
		pinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {			
					EditText ettn = (EditText) findViewById(R.id.editTextTerminalName);
					Toast.makeText(getApplicationContext(), MonetBTAPI.getPin(ettn.getText().toString()), Toast.LENGTH_LONG).show();

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});
		
		Button connectButton = (Button) findViewById(R.id.buttonConnect);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ShowTransactionOut(new TransactionOu));
					mAnswerTextView.setText("Calling connecting...");
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress(blueHwAddress.getText().toString());
					transIn.setCommand(TransactionCommand.ONLY_CONNECT);

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});

		Button disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAnswerTextView.setText("Calling disconnecting...");
				try {
					// TODO: precti si poradne dokumentaci.... ackoliv je to
					// asynchroni task, tak se vyhybaji paralelnimu vykonavani.
					// DoCancelTask doCancelTask = new DoCancelTask();
					// doCancelTask.execute();

					MonetBTAPI.doCancel();

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.properties_menu, menu);
		propertiesMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.bt_enabled:
			item.setChecked(true);
			propertiesMenu.findItem(R.id.tcp_enabled).setChecked(false);
			break;
		case R.id.tcp_enabled:
			item.setChecked(true);
			propertiesMenu.findItem(R.id.bt_enabled).setChecked(false);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void ShowTransactionOut(TransactionOut out) {
		final StringBuilder resultString = new StringBuilder();
		if (out != null) {
			resultString.append("ResultCode:" + out.getResultCode() + "\n");
			resultString.append("Message:" + out.getMessage() + "\n");

			resultString.append("AuthCode:" + out.getAuthCode() + "\n");
			resultString.append("SeqId:" + out.getSeqId() + "\n");
			resultString.append("CardNumber:" + out.getCardNumber() + "\n");
			resultString.append("CardType:" + out.getCardType() + "\n");
		} else {
			resultString.append("NULL returned!!!");
		}

		ServisActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAnswerTextView.setText(resultString.toString());

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_INSECURE:
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
					blueHwAddress
							.setText(data
									.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS));

					if (blueHwAddress.getText().length() > 0) {
						setButtons(true);

					}
				}
			}
			break;
		case ACTIVITY_INTENT_ID:
			if (resultCode == Activity.RESULT_OK) {
				StringBuilder out = new StringBuilder();
				if (data != null) {
					if (data.hasExtra("ResultCode")) {
						out.append("ResultCode:"
								+ data.getStringExtra("ResultCode") + "\n");
					}
					if (data.hasExtra("ServerMessage")) {
						out.append("ServerMessage:"
								+ data.getStringExtra("ServerMessage") + "\n");
					}

					if (data.hasExtra("AuthCode")) {
						out.append("AuthCode:"
								+ data.getStringExtra("AuthCode") + "\n");
					}

					if (data.hasExtra("SeqId")) {
						out.append("SeqId:" + data.getStringExtra("SeqId")
								+ "\n");
					}

					if (data.hasExtra("CardNumber")) {
						out.append("CardNumber:"
								+ data.getStringExtra("CardNumber") + "\n");
					}

					if (data.hasExtra("CardType")) {
						out.append("CardType:"
								+ data.getStringExtra("CardType") + "\n");
					}

					mAnswerTextView.setText(out.toString());
				}
			}
			break;
		}
	}

	private void setButtons(boolean enabled) {
		Button button = (Button) findViewById(R.id.buttonConnect);
		button.setEnabled(enabled);
		button = (Button) findViewById(R.id.buttonDisconnect);
		button.setEnabled(enabled);
	}

	class DoTransactionTask extends
			AsyncTask<TransactionIn, Void, TransactionOut> {

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {
			return MonetBTAPI.doTransaction(
			/* getApplicationContext() */ServisActivity.this, params[0]);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(TransactionOut result) {
			// do the analysis of the returned data of the function
			ShowTransactionOut(result);
			transactionTask = null;
		}
	}

	class DoCancelTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MonetBTAPI.doCancel();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ServisActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAnswerTextView.setText("MonetBTApi is closed. maybe.");

				}
			});
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
