package robii.cryptowallet;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Common {

    public Common(Resources resources){
		try {
			currencySymbol = resources.getString(R.string.euro_simbol);
			percentage = resources.getString(R.string.percentage_simbol);
			datePattern = resources.getString(R.string.date_patern);
			timePattern = resources.getString(R.string.time_patern);
			dateTimePattern = datePattern+" "+timePattern;
		}catch (Exception e){
			Log.e("ERROR", e.getMessage());
			e.printStackTrace();
		}
	}

	public static String datePattern;
	public static String timePattern;
	public static String dateTimePattern;
	public static String currencySymbol = "";
	public static String percentage = "";

	public static final String COIN_MARKET_CUP_LINK_BASE= "https://coinmarketcap.com/currencies/";

	private static ExecutorService executor = Executors.newFixedThreadPool(8);

	// Using executor service to make Async calls
    public static<T> Future<T> getFuture(Callable<T> task){
    	Future<T> futurePrice = executor.submit(task);
    	return futurePrice;
    }

	// waiting the result of task t
    public static<T> T getResult(Future<T> t){
		try {
			return t.get();
		} catch (Exception e) {
			Log.e("ERROR",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

    public static void setColorGoodOrBad(TextView textView, boolean good) {
		int color = good ? Color.GREEN : Color.RED;
		textView.setTextColor(color);
    }

	public static Double twoDecimals(Double v) {
		Double shifted = (v * 100.00);
		long round=Math.round(shifted);
		return (round*1.0)/100.00;
	}

	public static String twoDecimalsStr(Double v){
		Double d = twoDecimals(v);
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}


}
