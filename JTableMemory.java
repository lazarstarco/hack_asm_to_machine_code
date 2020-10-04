package final_program;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class JTableMemory extends AbstractTableModel{

	ArrayList<MemoryFragment> list;
	
	public JTableMemory(ArrayList<MemoryFragment> list) {
		this.list = list;
	}
	
	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MemoryFragment mf = list.get(rowIndex);
		switch(columnIndex) {
		case 0: return mf.name;
		case 1: return mf.data;
		default: return null;
		}
	}

	@Override
	public String getColumnName (int columnIndex) {
		switch(columnIndex) {
		case 0: return "Variable";
		case 1: return "Value";
		default: return null;
		}
	}
	
}
