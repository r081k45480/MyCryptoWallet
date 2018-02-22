package robii.cryptowallet.controler;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.net.SocketOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import robii.cryptowallet.Common;
import robii.cryptowallet.controler.api.RESTReader;
import robii.cryptowallet.controler.api.RESTReaderImpl;
import robii.cryptowallet.controler.db.DBReader;
import robii.cryptowallet.controler.db.DBReaderImpl;
import robii.cryptowallet.controler.db.DBReaderTest;
import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;
import robii.cryptowallet.model.comparators.CoinAmountComparator;

/**
 * Created by Robert Sabo on 04-Feb-18.
 */

public class CoinManagerImpl implements CoinManager{
	
	private RESTReader restReader;
	private DBReader dbReader;
		
	public CoinManagerImpl(){
		this.dbReader = new DBReaderImpl();
		restReader = new RESTReaderImpl();
		//TODO: initialize dbReader
		Thread tr = new Thread(new Runnable() {
			@Override
			public void run() {
				allCoins = restReader.getAllCoins();
			}
		});
		//tr.start();
		
	}
    SortedMap<String, Coin> allCoins;
    Map<String, Coin> myCoinsMap;
    ArrayList<Coin> myCoins;

	@Override
    public CoinDetailed getDetailedCoin(final String symbol) {
    	Future<Double> futurePrice = Common.getFuture(new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return restReader.getCurrentPrice(symbol);
			}
		});

    	Future<SortedMap<Date, Double>> futureHistory = Common.getFuture(new Callable<SortedMap<Date, Double>>() {
			@Override
			public SortedMap<Date, Double> call() throws Exception {
				return restReader.getPriceForLast10Days(symbol);
			}
    	});
    	Future<Double> futurePriceBefore7Days = Common.getFuture(new Callable<Double>() {
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public Double call() throws Exception {
				Date d =  Date.from(ZonedDateTime.now().minusDays(7).toInstant());
				return restReader.getHistoricalPrice(symbol, d);
			}
		});

    	CoinDetailed coin = dbReader.getCoin(symbol);

		List<Buying> b = coin.getBuyings();
		coin.setCurrentPrice(Common.getResult(futurePrice));
		coin.setHistori(Common.getResult(futureHistory));
		coin.setPriceBefore7days(Common.getResult(futurePriceBefore7Days));
		return coin;
    }
  

    @Override
    public double getHistorycalPrice(String symbol, Date dateTime) {
        return restReader.getHistoricalPrice(symbol, dateTime);
    }

    @Override
    public SortedMap<String, Coin> getAllCoins() {
    	if(allCoins!=null)
    		return allCoins;
    	else 
    		return restReader.getAllCoins();
    }

	private void getMyCoinsWithoutPrices(){
		this.myCoinsMap = new HashMap<>();
		this.myCoins = new ArrayList<>();
		final List<Coin> myCoins = dbReader.getAllMyCoins();

		for (Coin v : myCoins) {
			this.myCoinsMap.put(v.getSymbol(), v);
			this.myCoins.add(v);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    public ArrayList<Coin> getMyCoins() {
		if(this.myCoins == null)
			getMyCoinsWithoutPrices();

    	List<String> myCoinsSymbols = new ArrayList<>(this.myCoinsMap.keySet());

    	Map<String, Double> prices = restReader.getPricesFor(myCoinsSymbols);

    	for (Coin v : myCoins){
    		v.setCurrentPrice(prices.get(v.getSymbol()));
    	}
    	Collections.sort(myCoins, new CoinAmountComparator());
        return this.myCoins;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    public Double getSumPercentualProfit(){
    	return (getSumProfit()/getSumInput())*100;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    public Double getSumProfit(){
    	return getSumCurrentCapital() - getSumInput();
    }
    
    @Override
    public Double getSumInput(){
    	return dbReader.getSumInput();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    public Double getSumCurrentCapital(){
    	if(myCoins == null)
    		getMyCoinsWithoutPrices();

    	Double sum = 0.0;
    	for (Coin t : myCoins){
    		sum += t.getCurrentCapital();
		}

    	return sum;
    }
}


