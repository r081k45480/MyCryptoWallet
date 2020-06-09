package robii.cryptowallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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


public class DetailedCoinPreview extends AppCompatActivity {

    public static final String SYMBOL_BUDLE_CODE = "symbol";
    public static final int REQUEST_CODE = 122;

    CoinDetailed coinDetailed;

    ListView buyings_lv;

    private Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detailed_coin_preview);

            Bundle bundle = getIntent().getExtras();
            String symbol = bundle.getString(SYMBOL_BUDLE_CODE);

            // get Data
            coinDetailed = coinManager.getDetailedCoin(symbol);

            buyings_lv= findViewById(R.id.buyings_list_veiw);

            BuyingsAdapter adapter = new BuyingsAdapter(coinDetailed, this);
            buyings_lv.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }

        System.out.print("Done!");
    }


}
