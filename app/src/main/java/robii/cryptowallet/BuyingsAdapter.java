package robii.cryptowallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;

/**
 * Created by Robert Sabo on 09-Apr-18.
 */

public class BuyingsAdapter extends BaseAdapter {

    List<Buying> buyings;

    public BuyingsAdapter(List<Buying> buyingList){
        buyings = buyingList;
    }

    @Override
    public int getCount() {
        return buyings.size();
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
        // init any of items in list
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.buying_list_item, viewGroup, false);
        }
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
}
