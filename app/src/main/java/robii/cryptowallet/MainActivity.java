package robii.cryptowallet;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import robii.cryptowallet.controler.CoinManager;
import robii.cryptowallet.controler.CoinManagerImpl;
import robii.cryptowallet.controler.api.RESTReaderImpl;
import robii.cryptowallet.controler.db.MyDatabase;
import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;
import robii.cryptowallet.model.CoinImageUrl;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static CoinManager coinManager;
    public static MyDatabase database;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView myCoinsListView;

    private MyCoinsAdapter myCoinsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        coinManager = new CoinManagerImpl();

        database = Room.databaseBuilder(getApplicationContext(),MyDatabase.class, "mycryptos")
                //.fallbackToDestructiveMigration()
                .build();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();

        mSwipeRefreshLayout = findViewById(R.id.MainContainter);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        myCoinsListView = findViewById(R.id.mycoinsListVew);

        onRefresh();

    }

    private void test() {
        while(true) {
            try {
                List<Buying> oo = Common.getFuture(new Callable<List<Buying>>() {
                    @Override
                    public List<Buying> call(){
                        return database.buyingDao().getGrouped();
                    }
                }).get();
                if(oo == null || oo.size() ==0 ) {
                    final Buying b = new Buying();
                    b.setInput(500.0);
                    b.setDate(new Date(2017, 11, 3));
                    b.setSymbol("BTC");
                    b.setPrice(5000.0);

                    final Buying b1 = new Buying();
                    b1.setInput(100.0);
                    b1.setDate(new Date(2017, 12, 29));
                    b1.setPrice(10000.0);
                    b1.setSymbol("BTC");


                    final Buying cb = new Buying();
                    cb.setInput(250.0);
                    cb.setDate(new Date(2017,12,10));
                    cb.setPrice(390.0);
                    cb.setSymbol("ETH");

                    final Buying cb1 = new Buying();
                    cb1.setInput(250.0);
                    cb1.setDate(new Date());
                    cb1.setPrice(670.0);
                    cb1.setSymbol("ETH");

                    final Buying cb2 = new Buying();
                    cb2.setInput(50.0);
                    cb2.setDate(new Date());
                    cb2.setPrice(670.0);
                    cb2.setSymbol("ETH");

                    List<Buying> oo111 = Common.getFuture(new Callable<List<Buying>>() {
                        @Override
                        public List<Buying> call() {
                            coinManager.getAllCoins();
                            return null;
                        }
                    }).get();
                    List<CoinImageUrl> urls = Common.getFuture(new Callable<List<CoinImageUrl>>() {
                        @Override
                        public List<CoinImageUrl> call() {
                            return database.coinImageUrlDao().getAll();
                        }
                    }).get();

                    List<Buying> oo1 = Common.getFuture(new Callable<List<Buying>>() {
                        @Override
                        public List<Buying> call() {
                            database.buyingDao().insertAll(b, b1, cb, cb1, cb2);
                            return null;
                        }
                    }).get();

                }
                List<CoinImageUrl> urls = Common.getFuture(new Callable<List<CoinImageUrl>>() {
                    @Override
                    public List<CoinImageUrl> call() {
                        return database.coinImageUrlDao().getAll();
                    }
                }).get();

                List<Buying> oo1 = Common.getFuture(new Callable<List<Buying>>() {
                    @Override
                    public List<Buying> call() {
                        return database.buyingDao().getGrouped();
                    }
                }).get();
                System.out.println("Done!");
                break;
            }catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRefresh() {
        try {
            myCoinsAdapter = new MyCoinsAdapter();
            myCoinsListView.setAdapter(myCoinsAdapter);

            mSwipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
    }
}
