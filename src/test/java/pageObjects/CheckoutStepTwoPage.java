package pageObjects;

import com.codeborne.selenide.SelenideElement;
import configs.PropertiesHelper;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;
import java.io.IOException;
import java.time.Duration;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class CheckoutStepTwoPage {

    private final SelenideElement itemTotal = $("[data-test=subtotal-label]");
    private final SelenideElement finishButton = $("#finish");
    private final SelenideElement completeHeader = $(".complete-header");


    @Step("Проверка стоимости заказа без учета налогов")
    public void checkSumOrder(PropertiesHelper propertiesHelper, String orderSum) throws IOException {

        String checkSumHeader = PropertiesHelper.getProperty("ui.checkSumHeader");
        String checkSumText = checkSumHeader + orderSum;

        itemTotal.shouldHave(text(checkSumText), Duration.ofSeconds(10));
        attachImage(itemTotal);

        finishButton.click();
        completeHeader.shouldHave(text(PropertiesHelper.getProperty("ui.finishHeader")), Duration.ofSeconds(10));
        attachImage(completeHeader);
    }

    @Attachment(value = "data", type = "image/png", fileExtension = ".png")
    public byte[] attachImage(SelenideElement element){
        return element.getScreenshotAs(OutputType.BYTES);
    }

}
