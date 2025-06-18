package boundary;

import dao.CategoryDAOImpl;
import entity.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryManagePane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private CategoryDAOImpl dao = new CategoryDAOImpl();

    public CategoryManagePane() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"类别编号", "类别名称", "描述"}, 0) {
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
        List<Category> list = dao.getAll();
        for (Category c : list) {
            tableModel.addRow(new Object[]{c.getCategoryId(), c.getCategoryName(), c.getDescription()});
        }
    }

    private void onAdd() {
        CategoryEditDialog dialog = new CategoryEditDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.add(dialog.getCategory());
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
        String desc = (String) tableModel.getValueAt(row, 2);
        CategoryEditDialog dialog = new CategoryEditDialog(new Category(id, name, desc));
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.update(dialog.getCategory());
            refreshTable();
        }
    }

    static class CategoryEditDialog extends JDialog {
        private JTextField tfId, tfName, tfDesc;
        private boolean ok = false;

        public CategoryEditDialog(Category c) {
            setTitle(c == null ? "添加类别" : "修改类别");
            setModal(true);
            setSize(300, 200);
            setLayout(new GridLayout(4,2));

            add(new JLabel("类别编号:"));
            tfId = new JTextField();
            add(tfId);
            add(new JLabel("类别名称:"));
            tfName = new JTextField();
            add(tfName);
            add(new JLabel("描述:"));
            tfDesc = new JTextField();
            add(tfDesc);

            JButton okBtn = new JButton("确定");
            JButton cancelBtn = new JButton("取消");
            add(okBtn); add(cancelBtn);

            if (c != null) {
                tfId.setText(String.valueOf(c.getCategoryId()));
                tfId.setEditable(false);
                tfName.setText(c.getCategoryName());
                tfDesc.setText(c.getDescription());
            }

            okBtn.addActionListener(e -> {ok = true; setVisible(false);});
            cancelBtn.addActionListener(e -> {ok = false; setVisible(false);});
        }
        public boolean isOk() {return ok;}
        public Category getCategory() {
            return new Category(
                    Integer.parseInt(tfId.getText().trim()),
                    tfName.getText().trim(),
                    tfDesc.getText().trim()
            );
        }
    }
}