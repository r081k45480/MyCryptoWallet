package robii.cryptowallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import robii.cryptowallet.model.CoinDetailed;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import static robii.cryptowallet.MainActivity.coinManager;


public class DetailedCoinPreview extends Activity {

    public static final String SYMBOL_BUDLE_CODE = "symbol";
    public static final int REQUEST_CODE = 122;

    CoinDetailed coinDetailed;

    TextView nameOfCoin_tv ;
    ImageView myCoinImage_iv;
    ImageButton moreDetails_iv ;

    TextView before7days_tv ;
    TextView currentPrice_tv ;

    TextView investment_tv ;
    TextView amount_tv ;
    TextView currentPriceagain_tv ;
    TextView capital_tv ;
    TextView result_tv ;

    ListView buyings_lv;

    GraphView graph;
    Date minDate;
    Date maxDate;

    private Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        while(true){
            try {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_detailed_coin_preview);
                if (getActionBar() != null)
                    getActionBar().setDisplayHomeAsUpEnabled(true);

                Bundle bundle = getIntent().getExtras();
                String symbol = bundle.getString(SYMBOL_BUDLE_CODE);

                // get Data
                coinDetailed = coinManager.getDetailedCoin(symbol);

                nameOfCoin_tv = findViewById(R.id.details_name_of_coin);
                myCoinImage_iv = findViewById(R.id.details_imageViewMyCoin);
                moreDetails_iv = findViewById(R.id.details_more_details);

                before7days_tv = findViewById(R.id.details_before_7days);
                currentPrice_tv = findViewById(R.id.details_current_price);

                investment_tv = findViewById(R.id.details_total_investment);
                amount_tv = findViewById(R.id.details_total_amount);
                currentPriceagain_tv = findViewById(R.id.details_current_price_again);
                capital_tv = findViewById(R.id.details_current_capital);
                result_tv = findViewById(R.id.details_curent_result);

                buyings_lv= findViewById(R.id.buyings_list_veiw);

                graph = findViewById(R.id.graph);

                moreDetails_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = coinDetailed.getLinkToCoinMarketCup();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                });

                nameOfCoin_tv.setText(coinDetailed.getName());
                myCoinImage_iv.setImageBitmap(coinDetailed.getIcon());

                String curPrice = Common.twoDecimalsStr(coinDetailed.getCurrentPrice());
                before7days_tv.setText(Common.twoDecimalsStr(coinDetailed.getPriceBefore7days()));
                currentPrice_tv.setText(curPrice);

                investment_tv.setText(Common.twoDecimalsStr(coinDetailed.getInput()));
                amount_tv.setText(Common.twoDecimalsStr(coinDetailed.getAmount()));
                currentPriceagain_tv.setText(curPrice);
                capital_tv.setText(Common.twoDecimalsStr(coinDetailed.getCurrentCapital()));


                String profitWithPercentager = getProfitWithPercentage();
                result_tv.setText(profitWithPercentager);
                Common.setColorGoodOrBad(result_tv, coinDetailed.getCurrentResult() >= 0);

                initGraph();

                BuyingsAdapter adapter = new BuyingsAdapter(coinDetailed.getBuyings());
                buyings_lv.setAdapter(adapter);
                break;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.print("Done!");
    }

    private void initGraph(){
        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        DataPoint[] dataPoints = getDataPoints();
        LineGraphSeries<DataPoint> series =  new LineGraphSeries<>(dataPoints);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Double d = dataPoint.getX();
                Date date = new Date(d.longValue());
                SimpleDateFormat sdf = new SimpleDateFormat(Common.dateTimePattern);
                Toast.makeText(getActivity(), sdf.format(date)+" : "+dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(minDate.getTime());
        graph.getViewport().setMaxX(maxDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);


    }

    public DataPoint[] getDataPoints() {
        SortedMap<Date, Double> history = coinDetailed.getHistori();

        minDate = history.firstKey();
        maxDate = history.lastKey();

        DataPoint[] dataPoints = new DataPoint[history.size()];

        int i = 0;
        for(Date cur : history.keySet()){
            Double value = history.get(cur);
            value = Common.twoDecimals(value);
            dataPoints[i++] = new DataPoint(cur ,value);
        }

        return dataPoints;
    }

    public String getProfitWithPercentage() {
        StringBuilder rez = new StringBuilder();
        rez.append(Common.twoDecimalsStr(coinDetailed.getCurrentResult()));
        rez.append(Common.currencySymbol);
        rez.append(" (");
        rez.append(Common.twoDecimalsStr(coinDetailed.getCurrentResultPercentage()));
        rez.append(Common.percentage);
        rez.append(")");
        return rez.toString();
    }
}
