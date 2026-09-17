package com;

/**
 * SimpleOrderMain.java (Test)
 * 速食 點餐結帳平台 - 架構測試
 *
 * 測試 SimpleOrder 
 */
public class SimpleOrderMain {

    public static void main(String[] args) {

        // 測試案例 1：會員身份
        // 炸雞排 x3 ($255)、大薯條 x4 ($220)、玉米濃湯 x3 ($135)、牛肉堡 x1 ($120)
        // 小計(原價) = 730，會員 9 折後總金額 = 657.0
        SimpleOrder order1 = new SimpleOrder(true);
        order1.addItem(1); // 炸雞排 x3
        order1.addItem(1);
        order1.addItem(1);
        order1.addItem(3); // 大薯條 x4
        order1.addItem(3);
        order1.addItem(3);
        order1.addItem(3);
        order1.addItem(4); // 玉米濃湯 x3
        order1.addItem(4);
        order1.addItem(4);
        order1.addItem(0); // 牛肉堡 x1
        order1.checkout();
        order1.show(); // 預期結帳金額為：$657.0

        // 測試 getReceiptLines()：確認逐行收據內容產生正確
        String[] lines = order1.getReceiptLines();
        for (String line : lines) {
            System.out.println(line);
        }

        // 測試案例 2：非會員，驗證未打折計算
        SimpleOrder order2 = new SimpleOrder(false);
        order2.addItem(0); // 牛肉堡 +1
        order2.addItem(2); // 冰可樂 +1
        order2.addItem(2); // 冰可樂 再 +1
        order2.checkout();
        order2.show(); // 總金額 = 120 + 30*2 = 180 (未打折)

        // 測試案例 3：測試 -/+ 邏輯與 clearAll()
        SimpleOrder order3 = new SimpleOrder(false);
        order3.addItem(1);
        order3.addItem(1);
        order3.removeItem(1); // 炸雞排數量 2 -> 1
        order3.checkout();
        order3.show(); // 結帳金額為：$85

        order3.clearAll();
        System.out.println("清除後總金額：$" + order3.calculateTotalAmount());

        // 顯示系統累積統計 (static method)
        SimpleOrder.showSystemSummary();
    }
}