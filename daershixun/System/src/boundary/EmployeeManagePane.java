package boundary;

import dao.EmployeeDAOImpl;
import entity.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeManagePane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private EmployeeDAOImpl dao = new EmployeeDAOImpl();

    public EmployeeManagePane() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"员工编号", "姓名", "性别", "职位", "地址", "工资"}, 0) {
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
        List<Employee> list = dao.getAll();
        for (Employee e : list) {
            tableModel.addRow(new Object[]{e.getEmpId(), e.getName(), e.getGender(), e.getPosition(), e.getAddress(), e.getSalary()});
        }
    }

    private void onAdd() {
        EmployeeEditDialog dialog = new EmployeeEditDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.add(dialog.getEmployee());
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
        int empId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String gender = (String) tableModel.getValueAt(row, 2);
        String position = (String) tableModel.getValueAt(row, 3);
        String address = (String) tableModel.getValueAt(row, 4);
        double salary = (double) tableModel.getValueAt(row, 5);
        EmployeeEditDialog dialog = new EmployeeEditDialog(new Employee(empId, name, gender, position, address, salary));
        dialog.setVisible(true);
        if (dialog.isOk()) {
            dao.update(dialog.getEmployee());
            refreshTable();
        }
    }

    static class EmployeeEditDialog extends JDialog {
        private JTextField tfId, tfName, tfGender, tfPosition, tfAddress, tfSalary;
        private boolean ok = false;

        public EmployeeEditDialog(Employee e) {
            setTitle(e == null ? "添加员工" : "修改员工");
            setModal(true);
            setSize(350, 280);
            setLayout(new GridLayout(7,2));

            add(new JLabel("员工编号:"));
            tfId = new JTextField();
            add(tfId);
            add(new JLabel("姓名:"));
            tfName = new JTextField();
            add(tfName);
            add(new JLabel("性别:"));
            tfGender = new JTextField();
            add(tfGender);
            add(new JLabel("职位:"));
            tfPosition = new JTextField();
            add(tfPosition);
            add(new JLabel("地址:"));
            tfAddress = new JTextField();
            add(tfAddress);
            add(new JLabel("工资:"));
            tfSalary = new JTextField();
            add(tfSalary);

            JButton okBtn = new JButton("确定");
            JButton cancelBtn = new JButton("取消");
            add(okBtn); add(cancelBtn);

            if (e != null) {
                tfId.setText(String.valueOf(e.getEmpId()));
                tfId.setEditable(false);
                tfName.setText(e.getName());
                tfGender.setText(e.getGender());
                tfPosition.setText(e.getPosition());
                tfAddress.setText(e.getAddress());
                tfSalary.setText(String.valueOf(e.getSalary()));
            }

            okBtn.addActionListener(ev -> {ok = true; setVisible(false);});
            cancelBtn.addActionListener(ev -> {ok = false; setVisible(false);});
        }
        public boolean isOk() {return ok;}
        public Employee getEmployee() {
            return new Employee(
                    Integer.parseInt(tfId.getText().trim()),
                    tfName.getText().trim(),
                    tfGender.getText().trim(),
                    tfPosition.getText().trim(),
                    tfAddress.getText().trim(),
                    Double.parseDouble(tfSalary.getText().trim())
            );
        }
    }
}