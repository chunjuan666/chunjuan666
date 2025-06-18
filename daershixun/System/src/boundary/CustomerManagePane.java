package boundary;

import dao.CustomerDAOImpl;
import entity.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagePane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private CustomerDAOImpl dao = new CustomerDAOImpl();

    public CustomerManagePane() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"顾客编号", "姓名", "电话", "性别"}, 0) {
            public boolean isCellEditable(int row, int column) {return false;}
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

        addBtn.addActionListener(e -> onAdd());
        delBtn.addActionListener(e -> onDelete());
        updateBtn.addActionListener(e -> onUpdate());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Customer> list = dao.getAll();
        for (Customer c : list) {
            tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone(), c.getGender()});
        }
    }

    private void onAdd() {
        CustomerEditDialog dialog = new CustomerEditDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.add(dialog.getCustomer());
            refreshTable();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {JOptionPane.showMessageDialog(this, "请选择要删除的行！"); return;}
        int id = (int) tableModel.getValueAt(row, 0);
        dao.delete(id);
        refreshTable();
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) {JOptionPane.showMessageDialog(this, "请选择要修改的行！"); return;}
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String phone = (String) tableModel.getValueAt(row, 2);
        String gender = (String) tableModel.getValueAt(row, 3);
        CustomerEditDialog dialog = new CustomerEditDialog(new Customer(id, name, phone, gender));
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.update(dialog.getCustomer());
            refreshTable();
        }
    }

    static class CustomerEditDialog extends JDialog {
        private JTextField tfId, tfName, tfPhone, tfGender;
        private boolean ok = false;

        public CustomerEditDialog(Customer c) {
            setTitle(c == null ? "添加顾客" : "修改顾客");
            setModal(true);
            setSize(300, 220);
            setLayout(new GridLayout(5,2));

            add(new JLabel("顾客编号:"));
            tfId = new JTextField();
            add(tfId);
            add(new JLabel("姓名:"));
            tfName = new JTextField();
            add(tfName);
            add(new JLabel("电话:"));
            tfPhone = new JTextField();
            add(tfPhone);
            add(new JLabel("性别:"));
            tfGender = new JTextField();
            add(tfGender);

            JButton okBtn = new JButton("确定");
            JButton cancelBtn = new JButton("取消");
            add(okBtn); add(cancelBtn);

            if (c != null) {
                tfId.setText(String.valueOf(c.getCustomerId()));
                tfId.setEditable(false);
                tfName.setText(c.getName());
                tfPhone.setText(c.getPhone());
                tfGender.setText(c.getGender());
            }

            okBtn.addActionListener(e -> {ok = true; setVisible(false);});
            cancelBtn.addActionListener(e -> {ok = false; setVisible(false);});
        }
        public boolean isOk() {return ok;}
        public Customer getCustomer() {
            return new Customer(
                    Integer.parseInt(tfId.getText().trim()),
                    tfName.getText().trim(),
                    tfPhone.getText().trim(),
                    tfGender.getText().trim()
            );
        }
    }
}