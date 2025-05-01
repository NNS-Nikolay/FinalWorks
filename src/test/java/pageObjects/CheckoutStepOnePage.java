package pageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import entities.Saucedemo.Buyer;
import helpers.TestHelper;
import io.qameta.allure.Step;
import java.time.Duration;
import static com.codeborne.selenide.Selenide.$;

public class CheckoutStepOnePage {
    
    private final SelenideElement firstNameField = $("#first-name");
    private final SelenideElement lastNameField = $("#last-name");
    private final SelenideElement zipPostCodeField = $("#postal-code");
    private final SelenideElement continueButton = $("#continue");
    public final SelenideElement errorHeader = $("h3[data-test='error']");

    @Step("Ввод данных покупателя")
    public void inputBuyerData(TestHelper testHelper){

        Buyer buyerData = testHelper.getBuyer();

        firstNameField.setValue(buyerData.firstName());
        lastNameField.setValue(buyerData.lastName());
        zipPostCodeField.setValue(buyerData.zipPostalCode());

        continueButton.click();
        errorHeader.shouldNotBe(Condition.visible, Duration.ofSeconds(3));

    }
}
