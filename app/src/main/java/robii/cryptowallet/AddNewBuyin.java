package robii.cryptowallet;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddNewBuyin extends Activity {


    private Button addBuyinButton;
    private EditText dateOfBuyinChoose;
    private EditText timeOfBuyinChoose;
    private EditText investmentEditText;
    private EditText amountEditText;
    private Spinner comboAlLCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_buyin);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        comboAlLCoins = findViewById(R.id.combo_all_coins);
        addBuyinButton = findViewById(R.id.add_buyin_button);
        dateOfBuyinChoose  = findViewById(R.id.dateOfBuyin);
        timeOfBuyinChoose = findViewById(R.id.timeofbuyin);
        investmentEditText = findViewById(R.id.investment_add_buyin);
        amountEditText = findViewById(R.id.amount_edit_add_buyin);
    }

}
