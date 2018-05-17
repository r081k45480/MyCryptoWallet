package robii.cryptowallet;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import robii.cryptowallet.model.*;

import static robii.cryptowallet.MainActivity.*;

public class AddNewBuyin extends AppCompatActivity{

    public static final int REQUEST_CODE = 22;

    private static AddNewBuyin me;

    private Button addBuyinButton;
    private TextView dateOfBuyinChoose;
    private TextView timeOfBuyinChoose;
    private EditText investmentEditText;
    private EditText amountEditText;
    private TextView priceEditText;
    private Spinner comboAlLCoins;

    Map<String, String> nameToSymbol;
    SortedMap<String, Coin> allCoins;
    List<String> coinsArray;
    Calendar calendar;
    Date selectedDate;
    Buying buying;
    String coinSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        me = this;
        try {
            Future<SortedMap<String, Coin>> futureAllCoins = Common.getFuture(new Callable<SortedMap<String, Coin>>() {
                @Override
                public SortedMap<String, Coin> call() throws Exception {
                    SortedMap<String,Coin> ac = coinManager.getAllCoins();
                    coinsArray = getCoinNames(ac);
                    return ac;
                }
            });
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_new_buyin);

            buying = new Buying();

            comboAlLCoins = findViewById(R.id.combo_all_coins);
            addBuyinButton = findViewById(R.id.add_buyin_button);
            dateOfBuyinChoose = findViewById(R.id.dateOfBuyin);
            timeOfBuyinChoose = findViewById(R.id.timeOfBuyin);
            investmentEditText = findViewById(R.id.investment_add_buyin);
            amountEditText = findViewById(R.id.amount_edit_add_buyin);
            priceEditText = findViewById(R.id.add_buyinpriceEditText);

            dateOfBuyinChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
                }
            });
            timeOfBuyinChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                }
            });

            investmentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    investmendChosed();
                    return true;
                }
            });
            investmentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    investmendChosed();
                }
            });
            amountEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    amountChosed();
                    return true;
                }
            });
            amountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    amountChosed();
                }
            });

            addBuyinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    buying.setDate(selectedDate);
                    buying.setSymbol(coinSelected);

                    final Buying buyinToSave = buying;

                    Future<Object> o = Common.getFuture(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            database.buyingDao().insertAll(buyinToSave);
                            return null;
                        }
                    });
                    //didn't helpsed
                    Common.getResult(o);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfull_add_text), Toast.LENGTH_LONG).show();

                    setResult(Activity.RESULT_OK, null);
                    finish();
                }
            });

            allCoins = Common.getResult(futureAllCoins);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, coinsArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            comboAlLCoins.setAdapter(spinnerArrayAdapter);

            comboAlLCoins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = (String) adapterView.getSelectedItem();
                    coinSelected(s);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    ;
                }
            });

            coinSelected = nameToSymbol.get(coinsArray.get(0));
            calendar = Calendar.getInstance();
            setSelectedDate();

        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
            ex.printStackTrace();

        }
    }

    private List<String> getCoinNames(SortedMap<String, Coin> allCoins) {
        List<String> coinNames = new ArrayList<>(allCoins.size());
        nameToSymbol = new HashMap<>();

        Iterator<String> iterator =allCoins.keySet().iterator();

        int i =0;
        while(iterator.hasNext()){
            String cur = iterator.next();
            Coin c = allCoins.get(cur);
            coinNames.add(c.getName());
            nameToSymbol.put(c.getName(), c.getSymbol());
        }

        return coinNames;
    }

    private void amountChosed() {
        double input = 0.0;
        try {
            input = Double.parseDouble(amountEditText.getText() + "");
        } catch (Exception e){}

        buying.setAndCalculateAmount(input);

        updateAutoSetterFields();
    }

    private void investmendChosed() {
        double input = 0.0;
        try {
            input = Double.parseDouble(investmentEditText.getText() + "");
        } catch (Exception e){}

        buying.setInput(input);
        updateAutoSetterFields();
    }

    private void coinSelected(final String s) {
        String newSelected = nameToSymbol.get(s);

        if(!(newSelected+"").equals(coinSelected)){
            coinSelected = newSelected;
            updatePrice();
        }
    }



    public void setSelectedDate() {
        selectedDate = calendar.getTime();

        SimpleDateFormat date_sdf = new SimpleDateFormat(Common.datePattern);
        SimpleDateFormat time_sdf = new SimpleDateFormat(Common.timePattern);

        dateOfBuyinChoose.setText(date_sdf.format(selectedDate));
        timeOfBuyinChoose.setText(time_sdf.format(selectedDate));

        updatePrice();
    }

    // update fields when you change some of autoCalculated data...
    private void updateAutoSetterFields() {
        double price = buying.getPrice() != null ? buying.getPrice() : 0;
        double investment = buying.getInput()!= null ? buying.getInput() : 0;
        double amount = buying.getAmount()!= null ? buying.getAmount() : 0;

        priceEditText.setText(Common.twoDecimalsStr(price));
        investmentEditText.setText(Common.twoDecimalsStr(investment));
        amountEditText.setText(Common.twoDecimalsStr(amount));
    }

    // update fields when you change some of restReaded data...
    private void updatePrice(){

        Future<Double> futurePrice = Common.getFuture(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return coinManager.getHistorycalPrice(coinSelected,selectedDate);
            }
        });

        double price = Common.getResult(futurePrice);

        priceEditText.setText(Common.twoDecimalsStr(price));
        buying.setPrice(price);

        updateAutoSetterFields();
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            me.calendar = Calendar.getInstance();
            int year = me.calendar.get(Calendar.YEAR);
            int month = me.calendar.get(Calendar.MONTH);
            int day = me.calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            me.calendar.set(year, month, day,0,0);
            me.setSelectedDate();
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = me.calendar.get(Calendar.HOUR_OF_DAY);
            int minute = me.calendar.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            me.calendar.set(Calendar.HOUR, hourOfDay);
            me.calendar.set(Calendar.MINUTE, minute);
            me.setSelectedDate();
        }
    }
}
