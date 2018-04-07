package robii.cryptowallet.controler;

import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;

import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;

/**
 * Created by Robert Sabo on 04-Feb-18.
 */
public interface CoinManager {

	///DetailedCoinView
	///get all to show on detail veiw
    public CoinDetailed getDetailedCoin(String symbol);
    
    ///AddNewTrans
    // on adding new gets the historical price
    public double getHistorycalPrice(String symbol, Date dateTime);
    
    // on adding new get all to show 
    public SortedMap<String, Coin> getAllCoins();
    
    ///ListOfMyCurrencies
    //
    public ArrayList<Coin> getMyCoins();
    
    //For summer on list
    public Double getSumPercentualProfit();
    public Double getSumProfit();
    public Double getSumInput();
    public Double getSumCurrentCapital();
}
