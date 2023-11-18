package page;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;


public class TransferPage {

    private final SelenideElement amount = $("[data-test-id=amount] input");
    private final SelenideElement from = $("[data-test-id=from] input");
    private final SelenideElement transfer = $("[data-test-id=action-transfer]");
    private final SelenideElement heading = $(withText("Пополнение карты"));
    private final SelenideElement error = $("[data-test-id=error-notification]");

    public TransferPage() {
        heading.shouldBe(visible);
    }

    public DashboardPage transferForm(String sum, DataHelper.CardInfo cardInfo) {
        makeTransfer(sum, cardInfo);
        return new DashboardPage();
    }

    public void makeTransfer(String amountTransfer, DataHelper.CardInfo cardInfo) {
        amount.setValue(amountTransfer);
        from.setValue(cardInfo.getCardNumber());
        transfer.click();
    }

    public void getError(String expectedText) {
        error.shouldHave(text(expectedText), Duration.ofSeconds(12)).shouldBe(visible);
    }
}