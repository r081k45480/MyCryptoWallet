package robii.cryptowallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import robii.cryptowallet.controler.CoinManager;
import robii.cryptowallet.controler.CoinManagerImpl;
import robii.cryptowallet.model.Coin;

import static robii.cryptowallet.MainActivity.*;


/**
 * Created by Robert Sabo on 10-Feb-18.
 */

public class MyCoinsAdapter extends BaseAdapter {

    List<Coin> coins;

    private TextView totalInput;
    private TextView totalCapital;
    private TextView totalProfit;
    private TextView totalProfitPercentage;
    private ImageButton openAddNewCoin;

    private Activity parent;

    public MyCoinsAdapter(Activity parent){
        this.parent = parent;

        Future<ArrayList<Coin>> futureMyCoins = Common.getFuture(new Callable<ArrayList<Coin>>() {
            @Override
            public ArrayList<Coin> call() throws Exception {
                return coinManager.getMyCoins();
            }
        });

        coins = Common.getResult(futureMyCoins);
    }

    @Override
    public int getCount() {
        return coins.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return coins.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try{
            if(i>0) {
                i--;
                return getViewForItem(i, view, viewGroup);
            } else
                return getHeaderView(i,view,viewGroup);
        } catch (Exception e){
            Log.e("ERROR",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private View getViewForItem(int i, View view, ViewGroup viewGroup) {
        // init any of items in list
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.my_coin_list_item, viewGroup, false);

            //TODO: on click open detailed view
        }
        TextView name = view.findViewById(R.id.textViewMyCoinName);
        ImageView imageView = view.findViewById(R.id.imageViewMyCoin);
        TextView amount = view.findViewById(R.id.textViewMyCoinAmnount);
        TextView price = view.findViewById(R.id.textViewMyCoinPrice);
        TextView profitTextView = view.findViewById(R.id.textViewMyCoinProfit);

        Coin coin = coins.get(i);

        name.setText(coin.getName());
        imageView.setImageBitmap(coin.getIcon());
        amount.setText(Common.twoDecimals(coin.getAmount()) + " " + coin.getSymbol());
        price.setText(Common.twoDecimals(coin.getCurrentPrice()) + Common.currencySymbol);

        Double profit = coin.getProfit();
        profitTextView.setText(Common.twoDecimals(profit) + " (" + Common.twoDecimals(coin.getPercentualProfit()) + Common.percentage + ")");
        if (profit >= 0) {
            profitTextView.setTextColor(Color.GREEN);
        } else {
            profitTextView.setTextColor(Color.RED);
        }

        return view;
    }

    private View getHeaderView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.my_coin_header, viewGroup, false);


        totalInput = view.findViewById(R.id.textViewTotalInvestment);
        totalCapital = view.findViewById(R.id.textViewTotalCapital);
        totalProfit = view.findViewById(R.id.textViewCapital);
        totalProfitPercentage = view.findViewById(R.id.textViewCapitalPercentage9);
        openAddNewCoin = view.findViewById(R.id.openAddNewCoinButton);

        openAddNewCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewCoin();
            }
        });

        readHeaderData();

        return view;
    }

    public void openAddNewCoin(){
        Log.i("DEBUG","openAddNewCoin-1");
        Intent intent = new Intent(parent, AddNewBuyin.class);
        parent.startActivityForResult(intent,AddNewBuyin.REQUEST_CODE);
        Log.i("DEBUG","openAddNewCoin-2");
    }

    private void readHeaderData() {
        totalInput.setText(Common.twoDecimals(coinManager.getSumInput()) + Common.currencySymbol);
        totalCapital.setText(Common.twoDecimals(coinManager.getSumCurrentCapital()) + Common.currencySymbol);
        Double profit = coinManager.getSumProfit();
        totalProfit.setText(Common.twoDecimals(profit) + Common.currencySymbol);
        totalProfitPercentage.setText("("+Common.twoDecimals(coinManager.getSumPercentualProfit()) + Common.percentage+")");

        if(profit >= 0){
            totalProfit.setTextColor(Color.GREEN);
            totalProfitPercentage.setTextColor(Color.GREEN);
        } else {
            totalProfit.setTextColor(Color.RED);
            totalProfitPercentage.setTextColor(Color.RED);
        }
    }

}
