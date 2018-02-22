package robii.cryptowallet.model.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Robert Sabo on 19-Feb-18.
 */

public class DateConverter {
    @TypeConverter
    public static Long fromDate(Date date) {
        if (date==null)
            return null;

        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long millisSinceEpoch) {
        if (millisSinceEpoch==null)
            return(null);

        return new Date(millisSinceEpoch);
    }
}
