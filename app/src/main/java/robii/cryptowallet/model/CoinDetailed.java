package robii.cryptowallet.model;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
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

	protected Future<SortedMap<Date, Double>> futureHistori;
	protected SortedMap<Date, Double> histori;

	private Future<List<Buying>> futureBuyings;
	protected List<Buying> buyings;

	private Future<Double> futurePriceBefore7days;
	protected Double priceBefore7days;

	public double getDifferenceBetween7daysAndNow(){
		return ((priceBefore7days - currentPrice)/priceBefore7days) * 100;
    }



	public Double getPriceBefore7days() {
		if(futurePriceBefore7days != null){
			priceBefore7days = Common.getResult(futurePriceBefore7days);
			futurePriceBefore7days= null;
		}
		return priceBefore7days;
	}

	public void setPriceBefore7days(Future<Double> fpriceBefore7days) {
		this.futurePriceBefore7days = fpriceBefore7days;
	}
	public void setPriceBefore7days(Double priceBefore7days) {
		priceBefore7days = null;
		this.priceBefore7days = priceBefore7days;
	}


	public List<Buying> getBuyings() {
		if (futureBuyings != null){
			buyings = Common.getResult(futureBuyings);

			futureBuyings = null;
		}
		return buyings;
	}
	public void setFuture(Future<List<Buying>> futureBuyings) {
		this.futureBuyings = futureBuyings;
	}
	public void setBuyings(List<Buying> buyings) {
		futureBuyings = null;
		this.buyings = buyings;
	}


	public SortedMap<Date, Double> getHistori() {
		if(futureHistori != null){
			histori = Common.getResult(futureHistori);

			futureHistori = null;
		}
		return histori;
	}

	public void setHistori(Future<SortedMap<Date, Double>> histori){
		this.futureHistori = histori;
	}
	public void setHistori(SortedMap<Date, Double> histori) {
		futureHistori = null;
		this.histori = histori;
	}



    public String getLinkToCoinMarketCup(){
		return Common.COIN_MARKET_CUP_LINK_BASE+convertToForLink(getName());
	}

    private static String convertToForLink(String name) {
        return name.toLowerCase().replace(" ","-");
    }

    public double getCurrentResult(){
    	return getCurrentCapital() - getInput();
	}

	public double getCurrentResultPercentage(){
		return (getCurrentResult()/getInput())*100;
	}
}
