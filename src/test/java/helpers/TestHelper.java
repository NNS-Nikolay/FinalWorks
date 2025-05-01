package helpers;

import entities.Saucedemo.Buyer;
import org.instancio.Instancio;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class TestHelper {

    public BigDecimal getPrice(String itemPriceString){

        String cleanPriceString = itemPriceString.replace("$", "").trim();
        return new BigDecimal(cleanPriceString);

    }

    public Buyer getBuyer() {
        return Instancio.of(Buyer.class).withSeed(1).create();
    }

    public String getFormattedSum(BigDecimal sum, String format){

        DecimalFormat decimalFormat = new DecimalFormat(format);
        String formattedSum = decimalFormat.format(sum);
        return formattedSum.replace(",", ".").trim();

    }


}
