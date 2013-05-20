package cz.monetplus.mashregisterplus;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.protocol.HTTP;

import cz.monetplus.blueterm.MonetBTAPI;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.mashregisterplus.util.SystemUiHider;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private EditText mAmountIdEditText;
	private Spinner mCurrencySpinner;
	private EditText mInvoiceIdEditText;
	private TextView mAnswerTextView;

	private String currentCurrency;

	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.activity_pay_base);
		
	    ActionBar actionBar = getActionBar();
	    actionBar.show();

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

		Button payButton = (Button) findViewById(R.id.buttonPay);
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType(HTTP.PLAIN_TEXT_TYPE);
				intent.putExtra("Amount", mAmountIdEditText.getText()
						.toString());
				intent.putExtra("Currency", currentCurrency);
				intent.putExtra("Invoice", mInvoiceIdEditText.getText()
						.toString());

				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(intent, ACTIVITY_INTENT_ID);
				} else {
					Toast.makeText(getApplicationContext(),
							"You must install BlueTerm", Toast.LENGTH_LONG)
							.show();
				}

				// startActivity(intent);

			}
		});

		Button payButton2 = (Button) findViewById(R.id.buttonPayTransaction);
		payButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					ShowTransactionOut(new TransactionOut());
					MonetBTAPI btapi = new MonetBTAPI();
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress("00:03:81:99:4F:DA");
					transIn.setCommand(TransactionCommand.PAY);
					transIn.setAmount(Integer.valueOf((int) (Double
							.valueOf(mAmountIdEditText.getText().toString()) * 100)));
					transIn.setCurrency(Integer.valueOf(currentCurrency));
					transIn.setInvoice(mInvoiceIdEditText.getText().toString());
					if (btapi.doTransaction(getApplicationContext(), transIn)) {
						timer.schedule(new CheckResult(btapi), 0, 500);
					}
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
					ShowTransactionOut(new TransactionOut());
					MonetBTAPI btapi = new MonetBTAPI();
					TransactionIn transIn = new TransactionIn();
					transIn.setBlueHwAddress("00:03:81:99:4F:DA");
					transIn.setCommand(TransactionCommand.HANDSHAKE);
					if (btapi.doTransaction(getApplicationContext(), transIn)) {
						timer.schedule(new CheckResult(btapi), 0, 500);
					}

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	class CheckResult extends TimerTask {

		MonetBTAPI api;

		private CheckResult(MonetBTAPI api) {
			super();
			this.api = api;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (api != null) {
				if (api.isTransactionFinished()) {
					ShowTransactionOut(api.getTransactionResult());
					this.cancel();
				}
			}

		}
	}

	private void ShowTransactionOut(TransactionOut out) {
		final StringBuilder resultString = new StringBuilder();
		resultString.append("ResultCode:" + out.getResultCode() + "\n");
		resultString.append("ServerMessage:" + out.getServerMessage() + "\n");

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
		case ACTIVITY_INTENT_ID:
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
					out.append("AuthCode:" + data.getStringExtra("AuthCode")
							+ "\n");
				}

				if (data.hasExtra("SeqId")) {
					out.append("SeqId:" + data.getStringExtra("SeqId") + "\n");
				}

				if (data.hasExtra("CardNumber")) {
					out.append("CardNumber:"
							+ data.getStringExtra("CardNumber") + "\n");
				}

				if (data.hasExtra("CardType")) {
					out.append("CardType:" + data.getStringExtra("CardType")
							+ "\n");
				}

				mAnswerTextView.setText(out.toString());
			}
			break;
		}
	}

	@Deprecated
	class DoTransactionTask extends AsyncTask<Void, Void, Boolean> {

		private TransactionIn transIn;
		private TransactionOut transOut;

		private DoTransactionTask(TransactionIn transIn, TransactionOut transOut) {
			super();
			this.transIn = transIn;
			this.transOut = transOut;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Looper.prepare();
			MonetBTAPI api = new MonetBTAPI();

			Boolean result = api
					.doTransaction(getApplicationContext(), transIn);

			Looper.loop();

			return result;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// do the analysis of the returned data of the function
			ShowTransactionOut(transOut);
		}

	}
}
