package cz.monetplus.mashregisterplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends AdActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		Button buttonMbca = (Button) findViewById(R.id.buttonMbca);
		buttonMbca.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						MbcaBaseActivity.class));

			}
		});
		
		Button buttonMvta = (Button) findViewById(R.id.buttonMvta);
		buttonMvta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						MvtaBaseActivity.class));

			}
		});
		
		Button buttonServis = (Button) findViewById(R.id.buttonServis);
		buttonServis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ServisActivity.class));

			}
		});

	}

}
