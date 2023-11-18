package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.DashboardPage;
import page.LoginPage;

import static data.DataHelper.*;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TransferCashTest {
    DashboardPage dashboardPage;
    CardInfo firstCardInfo;
    CardInfo twoCardInfo;
    int firstCardBalance;
    int twoCardBalance;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerity(verificationCode);
        firstCardInfo = getFirstCardInfo();
        twoCardInfo = getTwoCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        twoCardBalance = dashboardPage.getCardBalance(twoCardInfo);
    }

    @Test
    void shouldTransferCashFromFirst() {
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceTwoCard = twoCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(twoCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceTwoCard = dashboardPage.getCardBalance(twoCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceTwoCard, actualBalanceTwoCard);
    }

    @Test
    void shouldBeErrorIfAmountMoreThanBalance() {
        var amount = generateInvalidAmount(twoCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), twoCardInfo);
        transferPage.getError("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceTwoCard = dashboardPage.getCardBalance(twoCardInfo);
        assertEquals(firstCardBalance, actualBalanceFirstCard);
        assertEquals(twoCardBalance, actualBalanceTwoCard);
    }

    @Test
    void shouldTransCashZeroCards() {
        int amount = 0;
        var transferPage = dashboardPage.selectCardToTransfer(twoCardInfo);
        transferPage.makeTransfer(String.valueOf(amount),firstCardInfo);
        transferPage.getError("Сумма перевода не может быть равна 0");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceTwoCard = dashboardPage.getCardBalance(twoCardInfo);
        assertEquals(firstCardBalance - amount, actualBalanceFirstCard);
        assertEquals(twoCardBalance + amount, actualBalanceTwoCard);
    }
}
