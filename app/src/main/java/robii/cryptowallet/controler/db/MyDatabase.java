package robii.cryptowallet.controler.db;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;

import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.CoinImageUrl;


@Database(entities = {Buying.class, CoinImageUrl.class}, version = 4)
public abstract class MyDatabase extends RoomDatabase {
    public abstract BuyingDao buyingDao();
    public abstract CoinImageUrlDao coinImageUrlDao();
}
