package robii.cryptowallet.controler;

import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;


/**
 * Created by Robert Sabo on 10-Feb-18.
 */

public class CoinManagerTest implements CoinManager {
    @Override
    public CoinDetailed getDetailedCoin(String symbol) {
        return null;
    }

    @Override
    public double getHistorycalPrice(String symbol, Date dateTime) {
        return 1050.34;
    }

    @Override
    public SortedMap<String, Coin> getAllCoins() {
        return null;
    }

    @Override
    public ArrayList<Coin> getMyCoins() {
        Coin btc = new Coin();
        btc.setAmount(0.23);
        btc.setInput(500);
        btc.setCurrentPrice(1000);
        btc.setName("Bitcoin");
        btc.setSymbol("BTC");

        Coin btcc = new Coin();
        btcc.setAmount(2);
        btcc.setInput(300);
        btcc.setCurrentPrice(325.23);
        btcc.setName("Bitcoin Cash");
        btcc.setSymbol("BCH");

        Coin eth = new Coin();
        eth.setAmount(1.30);
        eth.setInput(500);
        eth.setCurrentPrice(625.23);
        eth.setName("Ethereum");
        eth.setSymbol("ETH");

        ArrayList<Coin> map = new ArrayList<Coin>();

        map.add(btc);
        map.add(eth);
        map.add(btcc);
        return map;
    }

    @Override
    public Double getSumPercentualProfit() {
        return (500/1800)*100.0;
    }

    @Override
    public Double getSumProfit() {
        return 500.0;
    }

    @Override
    public Double getSumInput() {
        return 1300.0;
    }

    @Override
    public Double getSumCurrentCapital() {
        return 1800.0;
    }
}
