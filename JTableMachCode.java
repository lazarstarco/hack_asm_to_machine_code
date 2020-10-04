package final_program;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class JTableMachCode extends AbstractTableModel{

	ArrayList<String> list;
	
	public JTableMachCode(ArrayList<String> list) {
		this.list = list;
	}
	
	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return list.get(rowIndex);
	}

	@Override
	public String getColumnName (int columnIndex) {
		return "Machine code";
	}
}
