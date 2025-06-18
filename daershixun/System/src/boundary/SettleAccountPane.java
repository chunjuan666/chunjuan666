package boundary;

import entity.OrderItem;
import entity.Order;
import entity.Desk;
import dao.OrderItemDAOImpl;
import dao.OrderDAOImpl;
import dao.DeskDAOImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SettleAccountPane extends JPanel {
    private JTextField orderIdField, paidField, changeField;
    private DefaultTableModel model;
    private OrderItemDAOImpl orderItemDAO = new OrderItemDAOImpl();
    private OrderDAOImpl orderDAO = new OrderDAOImpl();
    private DeskDAOImpl deskDAO = new DeskDAOImpl();

    public SettleAccountPane() {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(255, 253, 208));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("结账管理");
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(233, 30, 99));
        topPanel.add(title);
        topPanel.add(new JLabel("订单编号:"));
        orderIdField = new JTextField(10);
        orderIdField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        topPanel.add(orderIdField);
        JButton showBtn = createButton("显示订单明细", new Color(33,150,243));
        topPanel.add(showBtn);

        model = new DefaultTableModel(new Object[]{"菜品编号", "单价", "小计金额"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 17));
        table.setSelectionBackground(new Color(255, 236, 179));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7),2));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 13));
        bottomPanel.setOpaque(false);
        bottomPanel.add(new JLabel("实收金额:"));
        paidField = new JTextField(8);
        paidField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        bottomPanel.add(paidField);
        JButton calcBtn = createButton("计算找零", new Color(76,175,80));
        bottomPanel.add(calcBtn);
        bottomPanel.add(new JLabel("找零:"));
        changeField = new JTextField(8);
        changeField.setEditable(false);
        changeField.setFont(new Font("微软雅黑", Font.BOLD, 16));
        changeField.setForeground(new Color(233,30,99));
        bottomPanel.add(changeField);
        JButton settleBtn = createButton("结账", new Color(233,30,99));
        bottomPanel.add(settleBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        showBtn.addActionListener(e -> showItems());
        calcBtn.addActionListener(e -> calcChange());
        settleBtn.addActionListener(e -> settleAccount());
    }

    private void showItems() {
        model.setRowCount(0);
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            List<OrderItem> items = orderItemDAO.getAll();
            double total = 0;
            for (OrderItem oi : items) {
                if (oi.getOrderId() == orderId) {
                    model.addRow(new Object[]{oi.getDishId(), oi.getDishPrice(), oi.getTotalAmount()});
                    total += oi.getTotalAmount();
                }
            }
            model.addRow(new Object[]{"合计", "", total});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "输入订单编号有误！");
        }
    }

    private void calcChange() {
        try {
            double paid = Double.parseDouble(paidField.getText());
            double total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                if ("合计".equals(model.getValueAt(i, 0))) {
                    total = Double.parseDouble(model.getValueAt(i, 2).toString());
                    break;
                }
            }
            changeField.setText(String.format("%.2f", paid - total));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "请先显示订单明细并输入实收金额！");
        }
    }

    private void settleAccount() {
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            double paid = Double.parseDouble(paidField.getText());
            double total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                if ("合计".equals(model.getValueAt(i, 0))) {
                    total = Double.parseDouble(model.getValueAt(i, 2).toString());
                    break;
                }
            }
            if (paid < total) {
                JOptionPane.showMessageDialog(this, "实收金额不足！");
                return;
            }
            // 更新订单结账时间
            Order order = orderDAO.getById(orderId);
            if (order != null) {
                order.setEndTime(new java.util.Date());
                orderDAO.update(order);
                // 桌台状态清空
                Desk desk = deskDAO.getById(order.getDeskId());
                desk.setStatus("空闲");
                deskDAO.update(desk);
            }
            JOptionPane.showMessageDialog(this, "结账完成！找零：" + String.format("%.2f", paid - total));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "结账失败，请核对信息！");
        }
    }

    // 通用美化按钮
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);

        // 关键属性全部设置
        btn.setBackground(bg);                          // 按钮背景色
        btn.setForeground(Color.BLACK);                 // 文字颜色
        btn.setFont(new Font("微软雅黑", Font.BOLD, 17)); // 字体
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6,24,6,24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 关键：保证背景填充和不透明
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);

        // Nimbus风格下可加这一句增强兼容性
        btn.putClientProperty("JButton.buttonType", "roundRect");

        return btn;
    }
}