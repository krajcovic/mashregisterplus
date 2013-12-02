package cz.monetplus.mashregisterplus;

import cz.monetplus.blueterm.MonetBTAPI;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.mashregisterplus.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class PayBaseActivity extends Activity {
	private static final int ACTIVITY_INTENT_ID = 33333;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 33334;

	// private final ReentrantLock lock = new ReentrantLock();

	private EditText mAmountIdEditText;
	private Spinner mCurrencySpinner;
	private EditText mInvoiceIdEditText;
	private TextView mAnswerTextView;

	private String currentCurrency;
	private TextView blueHwAddress;

	DoTransactionTask transactionTask = null;

	private Menu propertiesMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.activity_pay_base);
		getActionBar().show();

		mAmountIdEditText = (EditText) findViewById(R.id.editPrice);
		mCurrencySpinner = (Spinner) findViewById(R.id.spinnerCurrency);
		mInvoiceIdEditText = (EditText) findViewById(R.id.editTextInvoice);

		mAnswerTextView = (TextView) findViewById(R.id.textAnswer);

		mAnswerTextView.setFocusableInTouchMode(true);
		mAnswerTextView.requestFocus();

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.currency_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mCurrencySpinner.setAdapter(adapter);
		mCurrencySpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View arg1, int pos, long arg3) {
						// TODO Auto-generated method stub
						currentCurrency = parent.getItemAtPosition(pos)
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		Button infoButton = (Button) findViewById(R.id.buttonInfo);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ShowTransactionOut(new TransactionOut());
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress(blueHwAddress.getText().toString());
					transIn.setCommand(TransactionCommand.INFO);

					if (transactionTask != null) {
						transactionTask.cancel(true);
						transactionTask = null;
					}

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		Button payButton2 = (Button) findViewById(R.id.buttonPayTransaction);
		payButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ShowTransactionOut(new TransactionOut());
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress(blueHwAddress.getText().toString());
					transIn.setCommand(TransactionCommand.PAY);
					transIn.setAmount(Integer.valueOf((int) (Double
							.valueOf(mAmountIdEditText.getText().toString()) * 100)));
					transIn.setCurrency(Integer.valueOf(currentCurrency));
					transIn.setInvoice(mInvoiceIdEditText.getText().toString());

					if (transactionTask != null) {
						transactionTask.cancel(true);
						transactionTask = null;
					}

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		Button buttonTranHand = (Button) findViewById(R.id.buttonTransactionHandshake);
		buttonTranHand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ShowTransactionOut(new TransactionOut());
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress(blueHwAddress.getText().toString());
					transIn.setCommand(TransactionCommand.HANDSHAKE);

					if (transactionTask != null) {
						transactionTask.cancel(true);
						transactionTask = null;
					}

					transactionTask = new DoTransactionTask();
					transactionTask.execute(transIn);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

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
		resultString.append("ResultCode:" + out.getResultCode() + "\n");
		resultString.append("Message:" + out.getMessage() + "\n");

		resultString.append("AuthCode:" + out.getAuthCode() + "\n");
		resultString.append("SeqId:" + out.getSeqId() + "\n");
		resultString.append("CardNumber:" + out.getCardNumber() + "\n");
		resultString.append("CardType:" + out.getCardType() + "\n");

		PayBaseActivity.this.runOnUiThread(new Runnable() {

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

	class DoTransactionTask extends
			AsyncTask<TransactionIn, Void, TransactionOut> {

		@Override
		protected TransactionOut doInBackground(TransactionIn... params) {

			// TransactionOut out = new TransactionOut();

			//MonetBTAPI api = new MonetBTAPI();

			return MonetBTAPI.doTransaction(getApplicationContext(), params[0]);

			// return out;

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(TransactionOut result) {
			// do the analysis of the returned data of the function
			ShowTransactionOut(result);
			transactionTask = null;
		}
	}
}
