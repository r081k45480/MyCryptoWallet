package robii.cryptowallet;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Common {
	public static final String currencySymbol = "€";
	public static final String percentage = "%";

	private static ExecutorService executor = Executors.newFixedThreadPool(8);
	
    public static<T> Future<T> getFuture(Callable<T> task){
    	Future<T> futurePrice = executor.submit(task);
    	return futurePrice;
    }

    public static Double twoDecimals(Double v) {
    	Double shifted = (v * 100.00);
    	long round=Math.round(shifted);
    	return (round*1.0)/100.00;
    }

    public static<T> T getResult(Future<T> t){
		try {
			return t.get();
		} catch (Exception e) {
			Log.e("ERROR",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
