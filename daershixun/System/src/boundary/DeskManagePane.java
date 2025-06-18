package boundary;

import dao.DeskDAOImpl;
import entity.Desk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DeskManagePane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DeskDAOImpl dao = new DeskDAOImpl();

    public DeskManagePane() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"桌台编号", "区域编号", "容量", "状态"}, 0) {
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
        List<Desk> list = dao.getAll();
        for (Desk d : list) {
            tableModel.addRow(new Object[]{d.getDeskId(), d.getAreaId(), d.getCapacity(), d.getStatus()});
        }
    }

    private void onAdd() {
        DeskEditDialog dialog = new DeskEditDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.add(dialog.getDesk());
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
        int deskId = (int) tableModel.getValueAt(row, 0);
        int areaId = (int) tableModel.getValueAt(row, 1);
        int capacity = (int) tableModel.getValueAt(row, 2);
        String status = (String) tableModel.getValueAt(row, 3);
        DeskEditDialog dialog = new DeskEditDialog(new Desk(deskId, areaId, capacity, status));
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.update(dialog.getDesk());
            refreshTable();
        }
    }

    static class DeskEditDialog extends JDialog {
        private JTextField tfId, tfAreaId, tfCapacity, tfStatus;
        private boolean ok = false;

        public DeskEditDialog(Desk d) {
            setTitle(d == null ? "添加桌台" : "修改桌台");
            setModal(true);
            setSize(300, 220);
            setLayout(new GridLayout(5,2));

            add(new JLabel("桌台编号:"));
            tfId = new JTextField();
            add(tfId);
            add(new JLabel("区域编号:"));
            tfAreaId = new JTextField();
            add(tfAreaId);
            add(new JLabel("容量:"));
            tfCapacity = new JTextField();
            add(tfCapacity);
            add(new JLabel("状态:"));
            tfStatus = new JTextField();
            add(tfStatus);

            JButton okBtn = new JButton("确定");
            JButton cancelBtn = new JButton("取消");
            add(okBtn); add(cancelBtn);

            if (d != null) {
                tfId.setText(String.valueOf(d.getDeskId()));
                tfId.setEditable(false);
                tfAreaId.setText(String.valueOf(d.getAreaId()));
                tfCapacity.setText(String.valueOf(d.getCapacity()));
                tfStatus.setText(d.getStatus());
            }

            okBtn.addActionListener(e -> {ok = true; setVisible(false);});
            cancelBtn.addActionListener(e -> {ok = false; setVisible(false);});
        }
        public boolean isOk() {return ok;}
        public Desk getDesk() {
            return new Desk(
                    Integer.parseInt(tfId.getText().trim()),
                    Integer.parseInt(tfAreaId.getText().trim()),
                    Integer.parseInt(tfCapacity.getText().trim()),
                    tfStatus.getText().trim()
            );
        }
    }
}