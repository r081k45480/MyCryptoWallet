package robii.cryptowallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class CoinImageUrl {
    public CoinImageUrl(String symbol, String url){
        this.symbol = symbol;
        this.url = url;
    }
    @PrimaryKey
    @NonNull
    private String symbol;
    private String name;
    private String url;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
