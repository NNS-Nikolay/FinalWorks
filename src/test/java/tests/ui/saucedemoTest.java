package tests.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit5.BrowserPerTestStrategyExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.UITestHelper;
import helpers.UILoginHelper;
import configs.PropertiesHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pageObjects.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static io.qameta.allure.Allure.step;


@ExtendWith(BrowserPerTestStrategyExtension.class)
public class saucedemoTest {

    private static LoginPage loginPage;
    public static PropertiesHelper propertiesHelper;
    public static UILoginHelper uiLoginHelper;
    public static UITestHelper UITestHelper;

    @BeforeAll
    public static void setUp() throws IOException {

        propertiesHelper = new PropertiesHelper();
        uiLoginHelper = new UILoginHelper();
        UITestHelper = new UITestHelper();
        loginPage = new LoginPage();

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        Configuration.pageLoadStrategy = propertiesHelper.getProperty("pageLoadStrategy");
        Configuration.browser = propertiesHelper.getProperty("browser");
        Configuration.browserPosition = "10x10";
        Configuration.timeout = 20000;
        Configuration.headless = true;
    }

    @Test
    @DisplayName("Авторизация под пользователем standart")
    @Severity(SeverityLevel.BLOCKER)
    public void standardAuthorizationTest() throws IOException {
        loginPage.login_standard(uiLoginHelper.getStandardUser());
    }

    @Test
    @DisplayName("Авторизация под пользователем locked_out")
    @Severity(SeverityLevel.BLOCKER)
    public void lockedAuthorizationTest() throws IOException {
        loginPage.login_locked_out(uiLoginHelper.getLockedUser());
    }


    @Test
    @DisplayName("Заказ товаров. Стандартный пользователь")
    @Description("Добавление в корзину трех товаров, проверка суммы заказа под стандартным пользователем")
    @Severity(SeverityLevel.BLOCKER)
    public void standardUserOrderTest() throws IOException {

        loginPage.login_standard(uiLoginHelper.getStandardUser());
        buyItemsCheckSum();

    }


    @Test
    @Disabled("Отключен, долго выполняется")
    @DisplayName("Заказ товаров. Заглюченный пользователь")
    @Description("Добавление в корзину трех товаров, проверка суммы заказа под заглюченным пользователем")
    public void glitchUserOrderTest() throws IOException {

        loginPage.login_standard(uiLoginHelper.getGlitchedUser());
        buyItemsCheckSum();

    }


    private static void buyItemsCheckSum() throws IOException {

        InventoryPage inventoryPage = new InventoryPage();
        List<Integer> listItemIndex = Arrays.asList(0, 2, 4);
        String orderSum = inventoryPage.listItemToCart(listItemIndex, UITestHelper);
        String orderCount = Integer.toString(listItemIndex.size());
        SelenideElement cartCounter = inventoryPage.getCartCounter();
        step("Проверка количества заказанных товаров ", () -> {
            cartCounter.shouldHave(text(orderCount));
        });
        inventoryPage.openCart();

        CartPage cartPage = new CartPage();
        step("Проверка списка заказанных товаров ", () -> {
            cartPage.getOrderListSize().shouldHave(size(listItemIndex.size()));
        });
        cartPage.goToCheckoutForm();

        CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();
        checkoutStepOnePage.inputBuyerData(UITestHelper);

        CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage();
        checkoutStepTwoPage.checkSumOrder(propertiesHelper, orderSum);

    }









}


