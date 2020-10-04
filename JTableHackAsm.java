package final_program;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class JTableHackAsm extends AbstractTableModel{

	ArrayList<String> list;
	
	public JTableHackAsm(ArrayList<String> list) {
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
		String string = list.get(rowIndex);
		switch(columnIndex) {
		case 0: return rowIndex;
		case 1: return string;
		default: return null;
		}
	}
	
	@Override
	public String getColumnName (int columnIndex) {
		switch(columnIndex) {
		case 0: return "Line number";
		case 1: return "Instruction";
		default: return null;
		}
	}
	
}
