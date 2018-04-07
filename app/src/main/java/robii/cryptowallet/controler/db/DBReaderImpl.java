package robii.cryptowallet.controler.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import robii.cryptowallet.Common;
import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;
import robii.cryptowallet.model.comparators.CoinAmountComparator;

import static robii.cryptowallet.MainActivity.database;

/**
 * Created by Robert Sabo on 10-Feb-18.
 */

public class DBReaderImpl implements DBReader {

    List<Coin> myCoins;

    @Override
    public List<Coin> getAllMyCoins() {
        Future<List<Buying>> future = Common.getFuture(new Callable<List<Buying>>() {
            @Override
            public List<Buying> call() throws Exception {
                return database.buyingDao().getGrouped();
            }
        });

        List<Buying> buyings = Common.getResult(future);
        myCoins = new ArrayList<>(buyings.size());

        for(Buying b:buyings)
        {
            Coin c = makeCoinFromBuying(b);
            myCoins.add(c);
        }
        Collections.sort(myCoins, new CoinAmountComparator());
        return myCoins;
    }

    @Override
    public CoinDetailed getCoin(final String symbol) {
        Future<Coin> futurecoin = Common.getFuture(new Callable<Coin>() {
            @Override
            public Coin call() throws Exception {
                Buying summed = database.buyingDao().getGrouped(symbol);
                return makeCoinFromBuying(summed);
            }
        });

        Future<List<Buying>> buyings = Common.getFuture(new Callable<List<Buying>>() {
            @Override
            public List<Buying> call(){
                return database.buyingDao().getAllBySymbol(symbol);
            }
        });

        Coin c = Common.getResult(futurecoin);
        CoinDetailed cd = new CoinDetailed(c);
        cd.setFutureBuyings(buyings);
        return cd;
    }

    @Override
    public Double getSumInput() {
        if(myCoins == null)
            getAllMyCoins();
        Double summ = 0.0;
        for(Coin c : myCoins)
            summ+=c.getInput();
        return summ;
    }

    private Coin makeCoinFromBuying(Buying b){
        Coin c= new Coin();
        c.setSymbol(b.getSymbol());
        // in query selected price as amount
        c.setAmount(b.getPrice());
        c.setInput(b.getInput());
        return c;
    }
}
