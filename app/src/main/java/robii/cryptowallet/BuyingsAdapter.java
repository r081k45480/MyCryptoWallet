package robii.cryptowallet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.CoinDetailed;

public class BuyingsAdapter extends BaseAdapter {

    CoinDetailed coinDetailed;
    List<Buying> buyings;
    Activity  parent;

    public BuyingsAdapter(CoinDetailed coinDetailed, Activity detailedCoinPreview){
        this.coinDetailed = coinDetailed;
        buyings = coinDetailed.getBuyings();
        this.parent = detailedCoinPreview;
    }

    @Override
    public int getCount() {
        return buyings.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return buyings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (i > 0) {
            i--;
            return getViewForItem(i, view, viewGroup);
        } else {
            //return getViewForItem(i, view,viewGroup);
            return getHeaderView(view, viewGroup);
        }

    }

    public View getViewForItem(int i , View view, ViewGroup viewGroup){

        try {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.buying_list_item, viewGroup, false);

            Buying buying = buyings.get(i);

            TextView date = view.findViewById(R.id.bli_date);
            TextView buyinTime = view.findViewById(R.id.bli_buyin_time);
            TextView nowTime = view.findViewById(R.id.bli_now);
            TextView result = view.findViewById(R.id.bli_result);

            SimpleDateFormat sdf = new SimpleDateFormat(Common.dateTimePattern);

            date.setText(sdf.format(buying.getDate()));

            buyinTime.setText(makeBuyinTimeString(buying));
            nowTime.setText(makeNowTimeString(buying));
            result.setText(makeResultString(buying));

            Common.setColorGoodOrBad(result, buying.getProfit() >= 0);

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
        return view;
    }

    private String makeBuyinTimeString(Buying buying) {
        StringBuilder rez = new StringBuilder();
        rez
            .append(Common.twoDecimalsStr(buying.getPrice()))
            .append(" * ")
            .append(Common.twoDecimalsStr(buying.getInput()))
            .append(Common.currencySymbol)
            .append(" = ")
            .append(Common.twoDecimalsStr(buying.getAmount()))
            .append(" "+buying.getSymbol());
        return rez.toString();
    }

    private String makeNowTimeString(Buying buying) {
        StringBuilder rez = new StringBuilder();
        rez.append(Common.twoDecimalsStr(buying.getAmount()))
                .append(" "+buying.getSymbol())
                .append(" * ")
                .append(Common.twoDecimalsStr(buying.getCoin().getCurrentPrice()))
                .append(" = ")
                .append(Common.twoDecimalsStr(buying.getCurrentCapital()))
                .append(Common.currencySymbol);
        return rez.toString();
    }

    private String makeResultString(Buying buying) {
        StringBuilder rez = new StringBuilder();
        rez.append(Common.twoDecimalsStr(buying.getProfit()));
        rez.append(Common.currencySymbol);
        rez.append(" (");
        rez.append(Common.twoDecimalsStr(buying.getPercentualProfit()));
        rez.append(Common.percentage);
        rez.append(")");
        return  rez.toString();
    }

    
    /****************/
    /**H E A D E R **/
    /****************/



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

    GraphView graph;
    Date minDate;
    Date maxDate;

    Toast toast;

    public View getHeaderView(View view, ViewGroup viewGroup){

        try {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.buying_header, viewGroup, false);

            nameOfCoin_tv = view.findViewById(R.id.details_name_of_coin);
            myCoinImage_iv = view.findViewById(R.id.details_imageViewMyCoin);
            moreDetails_iv = view.findViewById(R.id.details_more_details);

            before7days_tv = view.findViewById(R.id.details_before_7days);
            currentPrice_tv = view.findViewById(R.id.details_current_price);

            investment_tv = view.findViewById(R.id.details_total_investment);
            amount_tv = view.findViewById(R.id.details_total_amount);
            currentPriceagain_tv = view.findViewById(R.id.details_current_price_again);
            capital_tv = view.findViewById(R.id.details_current_capital);
            result_tv = view.findViewById(R.id.details_curent_result);

            graph = view.findViewById(R.id.graph);


            moreDetails_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link = coinDetailed.getLinkToCoinMarketCup();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    parent.startActivity(browserIntent);
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

            Log.i("DEBUG",Thread.currentThread().getId()+" ->"+" before init");
            initGraph();


            Log.i("DEBUG",Thread.currentThread().getId()+" ->"+" before return");

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
        return view;
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
                if(toast != null)
                    toast.cancel();
                toast = Toast.makeText(parent, sdf.format(date)+" : "+dataPoint.getY(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(parent));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);

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

