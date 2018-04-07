package robii.cryptowallet.model;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import robii.cryptowallet.Common;

public class CoinDetailed extends Coin {
	public CoinDetailed(Coin c){
		super();
		name = c.name;
		symbol = c.symbol;
		amount = c.amount;
		input = c.input;
	}
	
	protected SortedMap<Date, Double> histori;
	protected List<Buying> buyings;
	protected Double priceBefore7days;

	private Future<List<Buying>> futureBuyings;

	
	public double getDifferenceBetween7daysAndNow(){
		return ((priceBefore7days - currentPrice)/priceBefore7days) * 100;
    }
	
	public Double getPriceBefore7days() {
		return priceBefore7days;
	}
	
	public void setPriceBefore7days(Double priceBefore7days) {
		this.priceBefore7days = priceBefore7days;
	}

	public List<Buying> getBuyings() {
		if (futureBuyings != null){
			buyings = Common.getResult(futureBuyings);

			futureBuyings = null;
		}
		return buyings;
	}

	public void setBuyings(List<Buying> buyings) {
		futureBuyings = null;
		this.buyings = buyings;
	}

	public SortedMap<Date, Double> getHistori() {
		return histori;
	}

	public void setHistori(SortedMap<Date, Double> histori) {
		this.histori = histori;
	}

    public void setFutureBuyings(Future<List<Buying>> futureBuyings) {
        this.futureBuyings = futureBuyings;
    }

    public String getLinkToCoinMarketCup(){
		return Common.COIN_MARKET_CUP_LINK_BASE+convertToForLink(getName());
	}

    private static String convertToForLink(String name) {
        return name.toLowerCase().replace(" ","-");
    }
}
