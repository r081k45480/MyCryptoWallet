package robii.cryptowallet.controler.api;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import robii.cryptowallet.model.Coin;

/**
 * Created by Robert Sabo on 04-Feb-18.
 */

public interface RESTReader {
	
    public SortedMap<String, Coin> getAllCoins();
    
    public double getCurrentPrice(String symbol); 
    
    public Map<String, Double> getPricesFor(List<String> symbols);
    
    public double getHistoricalPrice(String symbol, Date ts);

    public SortedMap<Date, Double> getPriceForLast10Days(String symbol);
    
}
