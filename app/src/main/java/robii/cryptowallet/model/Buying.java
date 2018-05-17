package robii.cryptowallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.io.OptionalDataException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import robii.cryptowallet.model.converter.DateConverter;

@Entity
public class Buying {

	@PrimaryKey
	protected Long id;

	protected String symbol;

	@TypeConverters({DateConverter.class})
	protected Date date;

	protected Double input;
	protected Double price;
	protected Double amount;

	@Ignore
	protected Coin coin;
	
	public Double getAmount(){
		//manual added amount
		if(amount != null)
			return amount;

		if(input == null) input = 0.0;
		if(price == null) return 0.0;
		return input/price;
	}

	public void setAndCalculateAmount(double amount){
		if(price == null || price == 0.0) return;
		input = amount*price;
		setAmount(amount);
	}

	public void setAmount(double amount){
		this.amount = amount;
	}
	
	public Double getCurrentCapital(){
		return getAmount() * getCoin().getCurrentPrice();
	}
	
	public Double getProfit(){
		return getCurrentCapital() - input;
	}
	
	public double getPercentualProfit(){
    	return (getProfit()/input) * 100;
    }
	
	public Coin getCoin() {
		return coin;
	}
	public void setCoin(Coin coin) {
		this.coin = coin;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getInput() {
		return input;
	}
	public void setInput(Double in) {
		this.input = in;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
}
