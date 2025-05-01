package pageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import components.InventoryItemComponent;
import helpers.TestHelper;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class InventoryPage {

    private final ElementsCollection inventoryElements = $(".inventory_list").$$(".pricebar");
    private final SelenideElement cartCounter = $(".shopping_cart_link");

    @Step("Получить список товаров")
    public List<InventoryItemComponent> getInventoryItemComponents(){

        List<InventoryItemComponent> inventoryItemComponents = new ArrayList<>();
        inventoryElements.forEach(inventoryElement -> inventoryItemComponents.add(new InventoryItemComponent(inventoryElement)));
        return inventoryItemComponents;
    }

    @Step("Помещение списка товаров в корзину")
    public String listItemToCart(List<Integer> itemsToCart, TestHelper testHelper){

        BigDecimal totalSum = new BigDecimal(0);
        List<InventoryItemComponent> inventoryComponents = getInventoryItemComponents();

        for (Integer itemIndex : itemsToCart) {
            InventoryItemComponent inventoryComponent = inventoryComponents.get(itemIndex);
            BigDecimal itemPrice = testHelper.getPrice(inventoryComponent.getPrice().getText());
            inventoryComponent.findButton().click();
            totalSum = totalSum.add(itemPrice);
        }

        return testHelper.getFormattedSum(totalSum, "#.##");
    }

    @Step("Получение счетчика корзины")
    public SelenideElement getCartCounter(){
        return cartCounter;
    }

    @Step("Открытие страницы корзины")
    public void openCart(){
        cartCounter.click();
    }









}
