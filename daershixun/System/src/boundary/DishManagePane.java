package boundary;

import dao.DishDAOImpl;
import entity.Dish;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DishManagePane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DishDAOImpl dishDAO = new DishDAOImpl();

    public DishManagePane() {
        setLayout(new BorderLayout());

        // 表格数据与表头
        tableModel = new DefaultTableModel(new String[]{"菜品编号", "菜品名称", "价格", "类别编号"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("添加");
        JButton delBtn = new JButton("删除");
        JButton updateBtn = new JButton("修改");

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        btnPanel.add(updateBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // 添加按钮事件
        addBtn.addActionListener(e -> onAdd());
        delBtn.addActionListener(e -> onDelete());
        updateBtn.addActionListener(e -> onUpdate());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Dish> dishes = dishDAO.getAll();
        for (Dish d : dishes) {
            tableModel.addRow(new Object[]{
                    d.getDishId(), d.getDishName(), d.getPrice(), d.getCategoryId()
            });
        }
    }

    private void onAdd() {
        DishEditDialog dialog = new DishEditDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            Dish dish = dialog.getDish();
            dishDAO.add(dish);
            refreshTable();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的行！");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        dishDAO.delete(id);
        refreshTable();
    }

    // 新增：修改按钮事件
    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的行！");
            return;
        }
        int dishId = (int) tableModel.getValueAt(row, 0);
        String dishName = (String) tableModel.getValueAt(row, 1);
        double price = (double) tableModel.getValueAt(row, 2);
        int categoryId = (int) tableModel.getValueAt(row, 3);

        Dish oldDish = new Dish(dishId, dishName, price, categoryId);
        DishEditDialog dialog = new DishEditDialog(oldDish);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            Dish newDish = dialog.getDish();
            dishDAO.update(newDish);
            refreshTable();
        }
    }

    // 编辑弹窗
    static class DishEditDialog extends JDialog {
        private JTextField tfId, tfName, tfPrice, tfCategoryId;
        private boolean ok = false;

        public DishEditDialog(Dish dish) {
            setTitle(dish == null ? "添加菜品" : "修改菜品");
            setModal(true);
            setSize(300, 250);
            setLayout(new GridLayout(5, 2));

            add(new JLabel("菜品编号:"));
            tfId = new JTextField();
            add(tfId);

            add(new JLabel("菜品名称:"));
            tfName = new JTextField();
            add(tfName);

            add(new JLabel("价格:"));
            tfPrice = new JTextField();
            add(tfPrice);

            add(new JLabel("类别编号:"));
            tfCategoryId = new JTextField();
            add(tfCategoryId);

            JButton okBtn = new JButton("确定");
            JButton cancelBtn = new JButton("取消");
            add(okBtn);
            add(cancelBtn);

            if (dish != null) {
                tfId.setText(String.valueOf(dish.getDishId()));
                tfId.setEditable(false); // 主键禁止修改
                tfName.setText(dish.getDishName());
                tfPrice.setText(String.valueOf(dish.getPrice()));
                tfCategoryId.setText(String.valueOf(dish.getCategoryId()));
            }

            okBtn.addActionListener(e -> {
                ok = true;
                setVisible(false);
            });

            cancelBtn.addActionListener(e -> {
                ok = false;
                setVisible(false);
            });
        }

        public boolean isOk() {
            return ok;
        }

        public Dish getDish() {
            return new Dish(
                    Integer.parseInt(tfId.getText().trim()),
                    tfName.getText().trim(),
                    Double.parseDouble(tfPrice.getText().trim()),
                    Integer.parseInt(tfCategoryId.getText().trim())
            );
        }
    }
}