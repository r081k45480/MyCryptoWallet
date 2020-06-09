package robii.cryptowallet.controler.api;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import robii.cryptowallet.model.Coin;


public class RESTReaderImpl implements RESTReader{

    public static final String URL_BASE = "https://min-api.cryptocompare.com/data/";

    public static final String ALL_CURRENCIES = URL_BASE+"top/totalvol";
    public static final String PRICES = URL_BASE+"pricemulti";
    public static final String HISTORICAL_PRICE = URL_BASE + "pricehistorical";
    public static final String HOURLY_HISTORY_PRICES = URL_BASE +"histohour";

    public static final String IMAGE_BASE_URL = "https://www.cryptocompare.com";
    
    public static final Integer NUMBER_OF_DAYS = 10;

    public RESTReaderImpl(){}

    @Override
    public SortedMap<String, Coin> getAllCoins() {
        Map<String, String> params = new HashMap<String, String>();

        params.put("limit","100");
        params.put("tsym","EUR");
        String respo = getFromAddress(ALL_CURRENCIES, params);

        SortedMap<String, Coin> allCoins = new TreeMap<String, Coin>();
        try {
        	JSONObject jobj = new JSONObject(respo);
            JSONArray array = jobj.getJSONArray("Data");
            for (int i = 0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i).getJSONObject("CoinInfo");
                String symbol = obj.getString("Name");
                String name = obj.getString("FullName");
                String image = obj.getString("ImageUrl");

                Coin coin = new Coin();
                coin.setSymbol(symbol);
                coin.setName(name);
                coin.setLink(IMAGE_BASE_URL+image);

                // save coin infos in db, if not exists
                coin.saveIfNotExists();

                allCoins.put(symbol, coin);
            }
            return allCoins;
        }catch (JSONException ex){
            System.err.println(ex.getMessage()+" at: ");
            ex.printStackTrace();
            return  null;
        }
    }

    public double getCurrentPrice(String sybmol) {
    	return getPricesFor(Arrays.asList(sybmol)).get(sybmol);
    }

    public Map<String, Double> getPricesFor(List<String> symbols) {
        Map<String, String> params = new HashMap<String, String>();

        String symbolsAsOne= "";
        for(String s : symbols)
            symbolsAsOne +=","+s;

        symbolsAsOne = symbolsAsOne.substring(1, symbolsAsOne.length());
        
        params.put("fsyms",symbolsAsOne);
        params.put("tsyms","EUR");
        String respo = getFromAddress(PRICES, params);
        
        Map<String, Double> prices = new HashMap<String, Double>();
        try {
        	JSONObject jobj = new JSONObject(respo);
            
            for (int i = 0; i<symbols.size(); i++){
                String symbol = symbols.get(i);
                
                JSONObject valute = jobj.getJSONObject(symbol);
                Double price = valute.getDouble("EUR");

                prices.put(symbol, price);
            }
            return prices;
        }catch (JSONException ex){
            System.err.println(ex.getMessage()+" at: ");
            ex.printStackTrace();
            return  null;
        }
    }
    
    public double getHistoricalPrice(String symbol, Date ts) {
        Map<String, String> params = new HashMap<String, String>();
        //?fsym=BTC&tsyms=USD,EUR&ts=1452680400
        params.put("fsym",symbol);
        params.put("tsyms","EUR");
        Long days= ts.getTime() / (1000);
        params.put("ts", ""+days);
        String respo = getFromAddress(HISTORICAL_PRICE, params);

        try {
        	JSONObject jobj = new JSONObject(respo);
            
            JSONObject valute = jobj.getJSONObject(symbol);
            Double price = valute.getDouble("EUR");

            return price;
        }catch (JSONException ex){
            System.err.println(ex.getMessage()+" at: ");
            ex.printStackTrace();
            return 0;
        }
    }

    public SortedMap<Date, Double> getPriceForLast10Days(String symbol){
    	Map<String, String> params = new HashMap<String, String>();
        //?fsym=BTC&tsyms=USD,EUR&ts=1452680400
        params.put("fsym",symbol);
        params.put("tsym","EUR");
        params.put("limit", ""+(NUMBER_OF_DAYS*24));//limit rows... 10 days
        String respo = getFromAddress(HOURLY_HISTORY_PRICES, params);

        SortedMap<Date,Double> map = new TreeMap<Date,Double>();
        try {
        	JSONObject jobj = new JSONObject(respo);
            JSONArray array = jobj.getJSONArray("Data");
            for(int i = 0; i < array.length(); i++){
            	JSONObject curr = array.getJSONObject(i);
            	Long time=curr.getLong("time");
            	//time *=1000;
            	Date dt = new Date(1000 * time);
            	Double pr = curr.getDouble("high");
            	map.put(dt,  pr);
            }
            	
            //for(Map.Entry<Date,Double> v : map.entrySet())
            //	System.out.println(v.getKey() + " ---> "+v.getValue());
            
            return map;
        }catch (JSONException ex){
            System.err.println(ex.getMessage()+" at: ");
            ex.printStackTrace();
            return null;
        }
    }
    
    private String getFromAddress(String adress, Map<String, String> params){
        String charSet = "UTF-8";
        BufferedReader in = null;
        try {
        	String addressWithParams = params.size()>0? adress+"?" : adress;
            for (Map.Entry<String, String> param : params.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();
            	addressWithParams += (
            	        URLEncoder.encode(key, charSet)+
                                "="+
                        URLEncoder.encode(value,charSet)) + "&";
            }
            addressWithParams = addressWithParams.substring(0, addressWithParams.length()-1);
            
            URL url = new URL(addressWithParams);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoInput(true);
            httpCon.setRequestMethod("GET");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.connect();

            in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            return "";
        } finally {
            try {
                if(in!=null)
                    in.close();
            }catch (Exception e){}
        }
    }


}
