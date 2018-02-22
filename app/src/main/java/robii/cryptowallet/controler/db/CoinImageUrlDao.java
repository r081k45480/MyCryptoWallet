package robii.cryptowallet.controler.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import robii.cryptowallet.model.CoinImageUrl;

/**
 * Created by Robert Sabo on 20-Feb-18.
 */
@Dao
public interface CoinImageUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(CoinImageUrl url);

    @Query("SELECT * FROM CoinImageUrl WHERE symbol = :symbol LIMIT 1")
    public CoinImageUrl findBySymbol(String symbol);

    @Query("SELECT * FROM CoinImageUrl")
    public List<CoinImageUrl> getAll();
}
