package robii.cryptowallet.controler.db;

import java.util.List;

import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;


public interface DBReader {

	// fill the coin with data from database:
	// group by symbol and get sums
	
	
	// get all coins filled with amount and input
	List<Coin> getAllMyCoins();
	
	// gett coin filled with amount and input
	CoinDetailed getCoin(String symbol);
	
	Double getSumInput();
}
