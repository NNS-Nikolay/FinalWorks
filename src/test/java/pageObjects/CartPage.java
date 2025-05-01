package pageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CartPage {

    private final ElementsCollection orderList = $$(".cart_list > .cart_item");
    private final SelenideElement checkoutButton = $("#checkout");

    @Step("Открытие страницы проверки заказа")
    public void goToCheckoutForm() {
        checkoutButton.click();
    }

    @Step("Проверяем количество заказанных товаров")
    public ElementsCollection getOrderListSize(){
        return orderList;
    }


}
