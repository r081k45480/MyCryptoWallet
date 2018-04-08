package robii.cryptowallet.controler.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import robii.cryptowallet.Common;
import robii.cryptowallet.controler.api.RESTReaderImpl;
import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;

import static robii.cryptowallet.MainActivity.*;

/**
 * Created by Robert Sabo on 10-Feb-18.
 */

public class DBReaderTest implements DBReader {
    public DBReaderTest(){
        myCoins = new ArrayList<Coin>();//dbReader.getAllMyCoins();
        final Coin btc = new Coin();
        btc.setAmount(0.02);
        btc.setInput(500.0);
        btc.setName("Bitcoin");
        btc.setSymbol("BTC");


        final Coin btcc = new Coin();
        btcc.setAmount(5.0);
        btcc.setInput(300.0);
        btcc.setName("Bitcoin Cash");
        btcc.setSymbol("BCH");

        final Coin eth = new Coin();
        eth.setAmount(0.10);
        eth.setInput(500.0);
        eth.setName("Ethereum");
        eth.setSymbol("ETH");

        myCoins.add(btc);
        myCoins.add(btcc);
        myCoins.add(eth);


    }
    List<Coin> myCoins;
    @Override
    public List<Coin> getAllMyCoins() {
        return myCoins;
    }

    @Override
    public CoinDetailed getCoin(final String symbol) {
        Coin c = myCoins.get(0);
        CoinDetailed cd = new CoinDetailed(c);
        Future<List<Buying>> buyings = Common.getFuture(new Callable<List<Buying>>() {
            @Override
            public List<Buying> call(){
                return database.buyingDao().getAllBySymbol(symbol);
            }
        });
        cd.setFuture(buyings);
        return cd;
    }

    @Override
    public Double getSumInput() {
        return 1300.0;
    }
}
