package com;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SimpleOrder.java 速食 點餐結帳平台
 *
 * - 資料 (field、constructor、method、static field、static method)。
 */
public class SimpleOrder {

	// ===== static field (菜單資料，全系統共用) =====
	public static final String[] MENU_NAMES = { "牛肉堡", "炸雞排", "冰可樂", "大薯條", "玉米濃湯" };
	public static final int[] MENU_PRICES = { 120, 85, 30, 55, 45 };

	// ===== static field (全系統累積統計) =====
	private static int orderCount = 0; // 目前已結帳的訂單數量
	private static double totalRevenue = 0.0; // 系統累積營收

	// ===== 一般 (實例) field =====
	private int[] quantities; // 對應 MENU_NAMES 每項餐點的點餐數量 (一維 Array)
	private boolean isMember; // 是否為會員 (會員 9 折)
	private double totalAmount; // 本次結帳總金額 (未打折前)
	private double finalAmount; // 本次結帳最終金額 (打折後)

	// ===== Constructor =====
	public SimpleOrder(boolean isMember) {
		this.isMember = isMember;
		this.quantities = new int[MENU_NAMES.length];
		for (int i = 0; i < this.quantities.length; i++) {
			this.quantities[i] = 0;
		}
	}

	// 增加某項餐點數量 (對應畫面上的 + 按鈕)
	public void addItem(int index) {
		if (index >= 0 && index < quantities.length) {
			quantities[index]++;
		}
	}

	// 減少某項餐點數量 (對應畫面上的 - 按鈕)，不可小於 0
	public void removeItem(int index) {
		if (index >= 0 && index < quantities.length) {
			if (quantities[index] > 0) {
				quantities[index]--;
			}
		}
	}

	// 取得某項餐點的小計
	public int calculateSubtotal(int index) {
		return MENU_PRICES[index] * quantities[index];
	}

	// 使用 loop 加總所有餐點小計，計算總金額 (未打折)
	public double calculateTotalAmount() {
		double sum = 0;
		for (int i = 0; i < quantities.length; i++) {
			sum += calculateSubtotal(i);
		}
		totalAmount = sum;
		return totalAmount;
	}

	// 依會員身份計算最終金額 (會員 9 折)
	public double calculateFinalAmount() {
		if (isMember) {
			finalAmount = totalAmount * 0.9;
		} else {
			finalAmount = totalAmount;
		}
		return finalAmount;
	}

	// 結帳：計算總金額、套用折扣、累加系統統計，回傳最終應付金額
	public double checkout() {
		calculateTotalAmount();
		calculateFinalAmount();
		orderCount++;
		totalRevenue += finalAmount;
		return finalAmount;
	}

	// 全部清除：所有餐點數量歸零
	public void clearAll() {
		for (int i = 0; i < quantities.length; i++) {
			quantities[i] = 0;
		}
		totalAmount = 0;
		finalAmount = 0;
	}

	// 顯示本次訂單明細 (測試時方便直接看結果)
	public void show() {
		System.out.println("=========================");
		System.out.println("身份：" + (isMember ? "會員 (9折)" : "非會員"));
		for (int i = 0; i < quantities.length; i++) {
			if (quantities[i] > 0) {
				System.out.println(MENU_NAMES[i] + " x " + quantities[i] + " = $" + calculateSubtotal(i));
			}
		}
		System.out.println("總金額(未折扣)：$" + totalAmount);
		System.out.println("結帳金額為：$" + finalAmount);
		System.out.println("=========================");
	}

	// 回傳明細字串 (單一字串版本)，供簡易顯示或測試使用
	public String getReceiptText() {
		StringBuilder sb = new StringBuilder();
		sb.append("身份：").append(isMember ? "會員 (9折)" : "非會員").append("\n");
		for (int i = 0; i < quantities.length; i++) {
			if (quantities[i] > 0) {
				sb.append(MENU_NAMES[i]).append(" x ").append(quantities[i]).append(" = $").append(calculateSubtotal(i))
						.append("\n");
			}
		}
		sb.append("總金額(未折扣)：$").append(totalAmount).append("\n");
		sb.append("結帳金額為：$").append(finalAmount);
		return sb.toString();
	}

	/**
	 * 產生收據內容 (逐行字串陣列版本)。 純資料方法，View 端 (SimpleOrderUI) 會用這份資料自行組成畫面或列印內容。
	 */
	public String[] getReceiptLines() {
		calculateTotalAmount();
		calculateFinalAmount();

		int itemCount = 0;
		for (int i = 0; i < quantities.length; i++) {
			if (quantities[i] > 0) {
				itemCount++;
			}
		}

		// 固定行數：分隔線+標題+分隔線(3) + 時間+身分(2) + 分隔線(1) + 分隔線+小計+總金額(3) = 9
		int totalLines = 9 + itemCount;
		String[] lines = new String[totalLines];
		int idx = 0;

		lines[idx++] = "=================================";
		lines[idx++] = "速食 點餐結帳平台";
		lines[idx++] = "=================================";

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		lines[idx++] = "時間：" + time;
		lines[idx++] = "身分：" + (isMember ? "會員" : "非會員");
		lines[idx++] = "---------------------------------";

		for (int i = 0; i < quantities.length; i++) {
			if (quantities[i] > 0) {
				lines[idx++] = MENU_NAMES[i] + " x" + quantities[i] + "  $" + calculateSubtotal(i);
			}
		}

		lines[idx++] = "---------------------------------";
		lines[idx++] = "小計 (原價)：$" + (int) totalAmount;
		lines[idx++] = "總金額：$" + (int) finalAmount;

		return lines;
	}

	// static method - 顯示系統整體統計
	public static void showSystemSummary() {
		System.out.println("---------- 系統統計 ----------");
		System.out.println("累積訂單數量：" + orderCount);
		System.out.println("累積營收：$" + totalRevenue);
		System.out.println("------------------------------");
	}

	// 提供 View 讀取系統統計字串
	public static String getSystemSummaryText() {
		return "累積訂單數量：" + orderCount + "\n累積營收：$" + totalRevenue;
	}

	// Getter / Setter
	public int[] getQuantities() {
		return quantities;
	}

	public boolean isMember() {
		return isMember;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public double getFinalAmount() {
		return finalAmount;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
	}

	public static int getOrderCount() {
		return orderCount;
	}

	public static double getTotalRevenue() {
		return totalRevenue;
	}
}