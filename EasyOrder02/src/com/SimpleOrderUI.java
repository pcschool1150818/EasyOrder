package com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * SimpleOrderUI.java (View)
 * 速食 點餐結帳平台 - (WindowBuilder 風格 Swing 視窗)
 *
 * - 畫面建立、佈局、事件註冊，以及「如何顯示」SimpleOrder 的資料。
 * - (計算金額、判斷折扣、結帳、清除) 呼叫 SimpleOrder的方法，
 * - 畫面(刷新表格、組收據面板、列印) 
 */
public class SimpleOrderUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private SimpleOrder order; 
    private JPanel contentPane;
    private JRadioButton rbNonMember;
    private JRadioButton rbMember;
    private JLabel[] lblQuantity = new JLabel[SimpleOrder.MENU_NAMES.length];
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblTotalAmount;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SimpleOrderUI frame = new SimpleOrderUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public SimpleOrderUI() {

        order = new SimpleOrder(false);

        setTitle("速食點餐APP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 680, 700);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(242, 240, 140));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // ===== 身份選擇區 =====
        JLabel lblIdentity = new JLabel("身份:");
        lblIdentity.setForeground(new Color(128, 0, 0));
        lblIdentity.setFont(new Font("新細明體", Font.BOLD, 12));
        lblIdentity.setBounds(20, 15, 40, 25);
        contentPane.add(lblIdentity);

        rbNonMember = new JRadioButton("非會員");
        rbNonMember.setForeground(new Color(128, 0, 0));
        rbNonMember.setFont(new Font("新細明體", Font.BOLD, 12));
        rbNonMember.setBounds(60, 15, 80, 25);
        rbNonMember.setSelected(true);
        contentPane.add(rbNonMember);

        rbMember = new JRadioButton("會員 (9折)");
        rbMember.setForeground(new Color(128, 0, 0));
        rbMember.setFont(new Font("新細明體", Font.BOLD, 12));
        rbMember.setBounds(150, 15, 110, 25);
        contentPane.add(rbMember);

        ButtonGroup bgIdentity = new ButtonGroup();
        bgIdentity.add(rbNonMember);
        bgIdentity.add(rbMember);

        ActionListener identityListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                order.setMember(rbMember.isSelected());
            }
        };
        rbNonMember.addActionListener(identityListener);
        rbMember.addActionListener(identityListener);

        // ===== 系統時間 =====
        JLabel timeZone = new JLabel("目前時間:yyyy-MM-dd HH:mm:ss");
        timeZone.setFont(new Font("新細明體", Font.BOLD, 12));
        timeZone.setForeground(new Color(128, 0, 0));
        timeZone.setBounds(361, -5, 184, 64);
        contentPane.add(timeZone);

        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                timeZone.setText("目前時間：" + "\n" + time);
            }
        });
        timer.start();

        // ===== 菜單 =====
        JLabel lblMenuTitle = new JLabel("菜單");
        lblMenuTitle.setForeground(new Color(128, 0, 0));
        lblMenuTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        lblMenuTitle.setBounds(20, 55, 120, 25);
        contentPane.add(lblMenuTitle);

        int startY = 90;
        for (int i = 0; i < SimpleOrder.MENU_NAMES.length; i++) {
            final int index = i;

            JLabel lblName = new JLabel(SimpleOrder.MENU_NAMES[i] + " ($" + SimpleOrder.MENU_PRICES[i] + ")");
            lblName.setFont(new Font("Dialog", Font.BOLD, 13));
            lblName.setBounds(20, startY, 200, 25);
            contentPane.add(lblName);

            JButton btnMinus = new JButton("-");
            btnMinus.setBounds(240, startY, 60, 25);
            contentPane.add(btnMinus);

            lblQuantity[i] = new JLabel("0");
            lblQuantity[i].setHorizontalAlignment(SwingConstants.CENTER);
            lblQuantity[i].setBounds(310, startY, 40, 25);
            contentPane.add(lblQuantity[i]);

            JButton btnPlus = new JButton("+");
            btnPlus.setBounds(360, startY, 60, 25);
            contentPane.add(btnPlus);

            btnMinus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    order.removeItem(index); // 更新資料
                    refreshTable();          // View：刷新畫面
                }
            });

            btnPlus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    order.addItem(index);    // 更新資料
                    refreshTable();          // View：刷新畫面
                }
            });

            startY += 45;
        }

        // ===== 明細表格 =====
        String[] columnNames = { "餐點", "單價", "數量", "小計" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, startY + 10, 620, 150);
        contentPane.add(scrollPane);

        // ===== 總金額顯示 =====
        lblTotalAmount = new JLabel("總金額: $0");
        lblTotalAmount.setFont(new Font("Dialog", Font.BOLD, 22));
        lblTotalAmount.setForeground(Color.RED);
        lblTotalAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotalAmount.setBounds(300, startY + 170, 340, 35);
        contentPane.add(lblTotalAmount);

        // ===== 操作按鈕 =====
        int btnY = startY + 220;

        JButton btnCheckout = new JButton("結帳");
        btnCheckout.setFont(new Font("新細明體", Font.BOLD, 12));
        btnCheckout.setForeground(new Color(128, 0, 0));
        btnCheckout.setBounds(60, btnY, 100, 35);
        contentPane.add(btnCheckout);

        JButton btnClearAll = new JButton("全部清除");
        btnClearAll.setFont(new Font("新細明體", Font.BOLD, 12));
        btnClearAll.setForeground(new Color(128, 0, 0));
        btnClearAll.setBounds(180, btnY, 100, 35);
        contentPane.add(btnClearAll);

        JButton btnPreview = new JButton("預覽收據");
        btnPreview.setFont(new Font("新細明體", Font.BOLD, 12));
        btnPreview.setForeground(new Color(128, 0, 0));
        btnPreview.setBounds(300, btnY, 100, 35);
        contentPane.add(btnPreview);

        JButton btnClose = new JButton("關閉系統");
        btnClose.setFont(new Font("新細明體", Font.BOLD, 12));
        btnClose.setForeground(new Color(128, 0, 0));
        btnClose.setBounds(420, btnY, 100, 35);
        contentPane.add(btnClose);

        btnCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double finalAmount = order.checkout(); //結帳運算
                JOptionPane.showMessageDialog(SimpleOrderUI.this, "結帳金額為：$" + finalAmount, "Message",
                        JOptionPane.INFORMATION_MESSAGE);
                order = new SimpleOrder(rbMember.isSelected()); // 開啟下一筆新訂單
                refreshTable();
            }
        });

        // 全部清除
        btnClearAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                order.clearAll(); // 清除資料
                refreshTable();   // 刷新畫面
            }
        });

        // 預覽收據：跳出小視窗(資料來自 order.getReceiptLines())
        btnPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReceiptPreviewDialog();
            }
        });

        // 關閉系統
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        refreshTable();
    }

    /**
     * View 方法：order 目前的資料，刷新畫面上的
     * 數量標籤、明細表格與總金額顯示。
     * 這個方法只讀取 的資料 (getQuantities、calculateSubtotal、calculateTotalAmount)，
     */
    private void refreshTable() {
        int[] quantities = order.getQuantities();

        for (int i = 0; i < quantities.length; i++) {
            lblQuantity[i].setText(String.valueOf(quantities[i]));
        }

        tableModel.setRowCount(0);
        for (int i = 0; i < quantities.length; i++) {
            if (quantities[i] > 0) {
                int subtotal = order.calculateSubtotal(i);
                tableModel.addRow(new Object[] {
                        SimpleOrder.MENU_NAMES[i], SimpleOrder.MENU_PRICES[i], quantities[i], subtotal
                });
            }
        }

        double total = order.calculateTotalAmount();
        lblTotalAmount.setText("總金額: $" + (int) total);
    }

    /**
     * View 方法：顯示收據預覽小視窗。
     * 內容面板依 order.getReceiptLines() 提供的資料
     */
    private void showReceiptPreviewDialog() {
        final JDialog dialog = new JDialog(this, "收據預覽", true);
        dialog.setSize(360, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel receiptPanel = buildReceiptPanel(order.getReceiptLines());
        JScrollPane scrollPane = new JScrollPane(receiptPanel);
        dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        JButton btnSaveCheckout = new JButton("存檔並結帳");
        JButton btnPrint = new JButton("列印");
        JButton btnCancel = new JButton("取消");
        buttonPanel.add(btnSaveCheckout);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnPrint);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnCancel);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // 存檔並結帳：checkout()，完成後關閉視窗
        btnSaveCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double finalAmount = order.checkout();
                JOptionPane.showMessageDialog(dialog, "結帳金額為：$" + finalAmount, "Message",
                        JOptionPane.INFORMATION_MESSAGE);
                order = new SimpleOrder(rbMember.isSelected());
                refreshTable();
                dialog.dispose();
            }
        });

        // 列印
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printReceiptToPdf(order.getReceiptLines());
            }
        });

        // 取消：關閉小視窗，不做任何異動
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     *依收據內容 (逐行字串) 組出置中顯示的 JPanel。
     */
    private JPanel buildReceiptPanel(String[] receiptLines) {
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        receiptPanel.setBackground(Color.WHITE);

        for (String line : receiptLines) {
            JLabel lblLine = new JLabel(line);
            lblLine.setFont(new Font("Monospaced", Font.PLAIN, 13));
            lblLine.setAlignmentX(Component.CENTER_ALIGNMENT);
            receiptPanel.add(lblLine);
        }

        return receiptPanel;
    }

    /**
     * 呼叫 java.awt.print.PrinterJob 的 print() 方法列印收據，
     * 
     * 輸出 PDF 檔。
     */
    private void printReceiptToPdf(final String[] receiptLines) {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                Font font = new Font("Microsoft JhengHei", Font.PLAIN, 13);
                g2d.setFont(font);

                double pageWidth = pageFormat.getImageableWidth();
                FontRenderContext frc = g2d.getFontRenderContext();

                float y = 20f;
                float leading = 20f;

                for (String line : receiptLines) {
                    Rectangle2D bounds = font.getStringBounds(line, frc);
                    double textWidth = bounds.getWidth();
                    double x = (pageWidth - textWidth) / 2; // 置中計算

                    g2d.drawString(line, (float) x, y);
                    y += leading;
                }

                return PAGE_EXISTS;
            }
        });

        boolean userConfirmed = job.printDialog();
        if (!userConfirmed) {
            return; // 使用者取消列印
        }

        try {
            job.print();
            JOptionPane.showMessageDialog(this,
                    "已送出列印工作，如需輸出 PDF，\n請於印表機清單選擇「另存為 PDF」的虛擬印表機。",
                    "列印", JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "列印失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
}