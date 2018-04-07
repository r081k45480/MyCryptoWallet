package robii.cryptowallet.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import robii.cryptowallet.Common;

import static robii.cryptowallet.MainActivity.*;

/**
 * Created by Robert Sabo on 04-Feb-18.
 */

public class Coin implements Comparable<Coin>{
    protected String symbol;
    protected String name;

    public Coin(){}

    //read from API
    protected double currentPrice;
    //amount is sum of amounts by symbol
    protected double amount;
    //input is sum of inputs by symbol
    protected double input;

    private Future<Bitmap> futureIcon;
    private Bitmap icon = null;


    private String imageUrl;

    public Bitmap getIcon(){
            initFutureIcon();
            if(icon == null) {
                icon = Common.getResult(futureIcon);
            }
            return icon;
    }

    public void setLink(final String url){
        imageUrl = url;
    }

    public void saveIfNotExists(){
        final String url = imageUrl;
        final String name = this.name;
        Common.getFuture(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if(database.coinImageUrlDao().findBySymbol(symbol) == null)
                {
                    CoinImageUrl ciu = new CoinImageUrl(symbol, url);
                    ciu.setName(name);
                    database.coinImageUrlDao().insert(ciu);
                }
                return null;
            }

        });
    }

    private void initFutureIcon(){
        if(icon != null || futureIcon != null) return;

        Future<CoinImageUrl> futureImageUrl = Common.getFuture(new Callable<CoinImageUrl>() {
            @Override
            public CoinImageUrl call() throws Exception {
                return database.coinImageUrlDao().findBySymbol(symbol);
            }
        });
        CoinImageUrl y = Common.getResult(futureImageUrl);
        if(y == null)
            return;

        imageUrl = y.getUrl();
        name = y.getName();
        futureIcon = Common.getFuture(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws ExecutionException, InterruptedException {
                Bitmap d = LoadImageFromWebOperations(imageUrl);
                return d;
            }
        });
    }

    public String getImageLink(){ return imageUrl; }

    private static Bitmap LoadImageFromWebOperations(String url) {
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    public double getCurrentCapital(){
    	return amount * currentPrice;
    }
    
    public double getPercentualProfit(){
    	return (getProfit()/input) * 100;
    }
    
    public double getProfit(){
    	return (getCurrentCapital() - input);
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        initFutureIcon();
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setInput(double input) {
        this.input = input;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName(){
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getAmount() {
        return amount;
    }

    public double getInput() {
        return input;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coin coin = (Coin) o;

        return symbol != null ? symbol.equals(coin.symbol) : coin.symbol == null;
    }

    @Override
    public int hashCode() {
        return symbol != null ? symbol.hashCode() : 0;
    }

    @Override
    public int compareTo(Coin coin) {
        return symbol.compareTo(coin.symbol);
    }
}