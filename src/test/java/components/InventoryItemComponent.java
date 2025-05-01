package components;

import com.codeborne.selenide.SelenideElement;

public class InventoryItemComponent {

    private final SelenideElement element;

    public InventoryItemComponent(SelenideElement element) {
        this.element = element;
    }

    public SelenideElement getPrice(){
        return element.find(".inventory_item_price");
    }

    public SelenideElement findButton(){
        return element.find(".btn_inventory");
    }


}
