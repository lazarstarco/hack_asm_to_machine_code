package final_program;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class JFrameMain extends JFrame {

	private JPanel contentPane;
	private static JTable tableHackAsm;
	private static JTable tableMachCode;
	private static JTable tableMemory;
	private static JButton btnFile = new JButton("File");
	private static JButton btnRun = new JButton("Run");
	private static JButton btnLoad = new JButton("Load");
	private static JLabel lblFileLoaded = new JLabel("");
	private static JLabel lblCompare = new JLabel("");

	private static ArrayList<String> listHackAsm;
	private static ArrayList<String> listMachCode;
	private static ArrayList<MemoryFragment> mainListMemory;
	private static ArrayList<MemoryFragment> listMemory;
	private static ArrayList<String> listTst;

	private static boolean isLoad;
	private static Scanner scanner;
	private static String path;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameMain frame = new JFrameMain();
					tableHackAsm.setModel(
							new DefaultTableModel(new Object[][] {}, new String[] { "Line number", "Instruction" }));
					tableMachCode.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Machine code" }));
					tableMemory
							.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Variable", "Value" }));
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
	public JFrameMain() {
		setTitle("Hack assembly to machine code");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 325,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 200, 650, 399);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		listHackAsm = new ArrayList<String>();
		listMachCode = new ArrayList<String>();
		mainListMemory = new ArrayList<MemoryFragment>(Arrays.asList(new MemoryFragment(0, "R0", 0),
				new MemoryFragment(0, "R1", 1), new MemoryFragment(0, "R2", 2), new MemoryFragment(0, "R3", 3),
				new MemoryFragment(0, "R4", 4), new MemoryFragment(0, "R5", 5), new MemoryFragment(0, "R6", 6),
				new MemoryFragment(0, "R7", 7), new MemoryFragment(0, "R8", 8), new MemoryFragment(0, "R9", 9),
				new MemoryFragment(0, "R10", 10), new MemoryFragment(0, "R11", 11), new MemoryFragment(0, "R12", 12),
				new MemoryFragment(0, "R13", 13), new MemoryFragment(0, "R14", 14), new MemoryFragment(0, "R15", 15),
				new MemoryFragment(1, "SCREEN", 16384), new MemoryFragment(1, "KBD", 24576)));
		listMemory = new ArrayList<MemoryFragment>();
		listTst = new ArrayList<String>();

		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				processing(false);

			}
		});
		btnFile.setBounds(91, 11, 89, 23);
		contentPane.add(btnFile);

		btnRun.setVisible(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				listMachCode.clear();

				boolean isPassed = true;
				String machCode = "";
				lblCompare.setText("");

				for (int i = 0; i < listHackAsm.size(); i++) {

					String instruction = listHackAsm.get(i);
					String tmpInstruction = "";
					boolean isAddress = instruction.charAt(0) == '@'
							&& Character.isAlphabetic((int) instruction.charAt(1));
					if (!((instruction.charAt(0) == '(') || isAddress)) {
						try {
							if (instruction.charAt(0) == '@') {
								tmpInstruction = instructionTypeA(instruction);
							} else {
								tmpInstruction = instructionTypeC(instruction);
							}
						} catch (Exception e1) {
							isPassed = false;
							lblCompare.setText("Error in line " + i);
							lblCompare.setForeground(Color.RED);
							break;
						}
					}
					if (isAddress) {
						for (MemoryFragment memoryFragment : listMemory) {
							if (memoryFragment.name.equals(instruction.substring(1))) {
								tmpInstruction = instructionTypeA("@" + memoryFragment.data);
							}
						}
					}
					if (!tmpInstruction.equals("")) {
						listMachCode.add(tmpInstruction);
						machCode += tmpInstruction + "\n";
					} else {
						continue;
					}

				}

				tableMachCode.setModel(new JTableMachCode(listMachCode));

				if (isLoad) {
					if (compareCodes(listMachCode, listTst)) {
						lblCompare.setText("Test passed");
					} else {
						lblCompare.setText("Test not passed");
					}
				}
				lblCompare.setVisible(true);
				if (isPassed) {
					toTxt(machCode, path.replace(".asm", ".out").replace(".cmp", ".out"));
				}
			}
		});
		btnRun.setBounds(451, 11, 89, 23);
		contentPane.add(btnRun);

		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				processing(true);

			}
		});
		btnLoad.setBounds(271, 11, 89, 23);
		contentPane.add(btnLoad);

		lblFileLoaded.setBounds(8, 42, 160, 14);
		contentPane.add(lblFileLoaded);

		lblCompare.setBounds(8, 67, 370, 14);
		contentPane.add(lblCompare);

		JScrollPane scrollPaneHackAsm = new JScrollPane();
		scrollPaneHackAsm.setBounds(8, 100, 200, 250);
		contentPane.add(scrollPaneHackAsm);

		tableHackAsm = new JTable();
		scrollPaneHackAsm.setViewportView(tableHackAsm);

		JScrollPane scrollPaneMachCode = new JScrollPane();
		scrollPaneMachCode.setBounds(216, 100, 200, 250);
		contentPane.add(scrollPaneMachCode);

		tableMachCode = new JTable();
		scrollPaneMachCode.setViewportView(tableMachCode);

		JScrollPane scrollPaneMemory = new JScrollPane();
		scrollPaneMemory.setBounds(424, 100, 200, 250);
		contentPane.add(scrollPaneMemory);

		tableMemory = new JTable();
		scrollPaneMemory.setViewportView(tableMemory);

	}

	public static String workingString(String instruction) {

		int i;

		for (i = 0; i < instruction.length(); i++) {

			if (instruction.charAt(i) == '=') {
				break;
			}

		}

		return instruction.substring(i + 1);

	}

	public static String prettifier(String finalString) {

		String machineCode = "";

		for (int i = 0; i < 16; i++) {

			if (i % 4 == 0 && i != 0) {
				machineCode += ' ';
			}

			machineCode += finalString.charAt(i);

		}

		return machineCode;

	}

	public static String instructionTypeA(String instruction) {

		String finalString = "", machineCode = Integer.toBinaryString(Integer.parseInt(instruction.substring(1)));

		for (int i = 0; i < 16 - machineCode.length(); i++) {
			finalString += '0';
		}

		finalString += machineCode;

		return prettifier(finalString);

	}

	public static char digit(String instruction) {

		char digit = '0';

		if (!instruction.contains(";")) {
			instruction = workingString(instruction);
		}

		else {

			int i;

			for (i = 0; i < instruction.length(); i++) {
				if (instruction.charAt(i) == ';') {
					break;
				}
			}

			instruction = instruction.substring(0, i);

		}

		for (int i = 0; i < instruction.length(); i++) {

			if (instruction.charAt(i) == 'M') {
				digit = '1';
				return digit;
			}

		}

		return digit;

	}

	public static String alu(String instruction) {

		if (!instruction.contains(";")) {
			instruction = workingString(instruction);
		}

		else {

			int i;

			for (i = 0; i < instruction.length(); i++) {
				if (instruction.charAt(i) == ';') {
					break;
				}
			}

			instruction = instruction.substring(0, i);

		}

		switch (instruction.length()) {

		case 1:

			switch (instruction.charAt(0)) {
			case '0':
				return "101010";
			case '1':
				return "111111";
			case 'D':
				return "001100";
			case 'A':
			case 'M':
				return "110000";
			}

			break;

		case 2:

			switch (instruction.charAt(0)) {
			case '-':

				switch (instruction.charAt(1)) {
				case '1':
					return "111010";
				case 'D':
					return "001111";
				case 'A':
				case 'M':
					return "110011";
				}

				break;

			case '!':

				switch (instruction.charAt(1)) {
				case 'D':
					return "001101";
				case 'A':
				case 'M':
					return "110001";
				}

				break;

			}

			break;

		case 3:

			switch (instruction.charAt(1)) {
			case '+':

				switch (instruction.charAt(0)) {
				case 'D':
					if (instruction.charAt(2) == '1') {
						return "011111";
					} else {
						return "000010";
					}
				case 'A':
				case 'M':
					if (instruction.charAt(2) == '1') {
						return "110111";
					} else {
						return "000010";
					}
				}

				break;

			case '-':

				switch (instruction.charAt(0)) {
				case 'D':
					if (instruction.charAt(2) == '1') {
						return "001110";
					} else {
						return "010011";
					}
				case 'A':
				case 'M':
					if (instruction.charAt(2) == '1') {
						return "110010";
					} else {
						return "000111";
					}
				}

				break;

			case '&':
				return "000000";

			case '|':
				return "010101";

			}

			break;

		}

		return "";
	}

	public static String destination(String instruction) {

		char[] destination = new char[] { '0', '0', '0' };

		int i;

		if (instruction.contains(";")) {
			return String.valueOf(destination);
		}

		for (i = 0; i < instruction.length(); i++) {

			if (instruction.charAt(i) == '=') {
				break;
			}

		}

		String workingString = instruction.substring(0, i);

		for (char c : workingString.toCharArray()) {

			if (c == 'A') {
				destination[0] = '1';
			}
			if (c == 'D') {
				destination[1] = '1';
			}
			if (c == 'M') {
				destination[2] = '1';
			}
		}

		return String.valueOf(destination);

	}

	public static String jump(String instruction) {

		if (!(instruction.contains(";"))) {
			return "000";

		} else {

			if (!instruction.contains(";")) {
				instruction = workingString(instruction);
			}

			else {

				int i;

				for (i = 0; i < instruction.length(); i++) {
					if (instruction.charAt(i) == ';') {
						break;
					}
				}

				instruction = instruction.substring(i + 1, instruction.length());

			}

			switch (instruction.charAt(1)) {
			case 'L':
				if (instruction.charAt(2) == 'T') {
					return "100";
				} else {
					return "110";
				}

			case 'G':
				if (instruction.charAt(2) == 'T') {
					return "001";
				} else {
					return "011";
				}

			case 'E':
				return "010";

			case 'N':
				return "101";

			case 'M':
				return "111";
			}

		}

		return "";
	}

	public static String instructionTypeC(String fullInstruction) {

		String machineCode = "111";

		machineCode += digit(fullInstruction);

		machineCode += alu(fullInstruction);

		machineCode += destination(fullInstruction);

		machineCode += jump(fullInstruction);

		return prettifier(machineCode);

	}

	public static boolean compareCodes(ArrayList<String> listMachCode, ArrayList<String> listTst) {

		try {
			for (int i = 0; i < listMachCode.size(); i++) {
				if (!listMachCode.get(i).replaceAll("\\s", "").equals(listTst.get(i).replaceAll("\\s", ""))) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;

	}

	public static void toTxt(String text, String printingPath) {
		try {
			BufferedWriter output = null;
			File file = new File(printingPath);
			file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			if (output != null) {
				output.close();
				JOptionPane.showMessageDialog(null, "Out file created at:\n" + printingPath);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error in printing");
		}
	}

	public static void processing(boolean isLoad) {
		listHackAsm.clear();
		lblFileLoaded.setVisible(false);
		lblFileLoaded.setText("File loaded succesfully");
		lblCompare.setVisible(false);
		lblCompare.setText("");

		tableHackAsm.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Linenumber", "Instruction" }));
		tableMachCode.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Machinecode" }));
		tableMemory.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Variable", "Value" }));

		int lineNumber = 0;
		int variableValue = 16;
		path = "";

		listMemory.clear();
		for (MemoryFragment fragment : mainListMemory) {
			listMemory.add(fragment);
		}

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		FileNameExtensionFilter filter;
		if (isLoad) {
			jfc.setDialogTitle("Select a .cmp file");
			jfc.setAcceptAllFileFilterUsed(false);
			filter = new FileNameExtensionFilter("CMP file", "cmp");
		} else {
			jfc.setDialogTitle("Select an .asm file");
			jfc.setAcceptAllFileFilterUsed(false);
			filter = new FileNameExtensionFilter("ASM file", "asm");
		}
		jfc.addChoosableFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			path = jfc.getSelectedFile().getPath();
		}

		try {
			scanner = new Scanner(new File(path));
		} catch (Exception exception) {
			lblFileLoaded.setText("File not loaded succesfully");
			lblFileLoaded.setVisible(true);
			return;
		}

		if (isLoad) {
			while (scanner.hasNext()) {
				listTst.add(scanner.nextLine());
			}

			try {
				scanner = new Scanner(new File(path.replace(".cmp", ".asm")));
			} catch (Exception exception) {
				lblFileLoaded.setText("Assembler file not found");
				lblFileLoaded.setVisible(true);
				return;
			}
		}

		while (scanner.hasNext()) {
			String instruction = scanner.nextLine();
			String tmpInstruction = instruction
					.substring(0, instruction.contains("/") ? instruction.indexOf('/') : instruction.length())
					.replaceAll("\\s", "");
			if (!tmpInstruction.equals("")) {
				listHackAsm.add(tmpInstruction);
			} else {
				continue;
			}

			String label = "";
			int data = 0;
			boolean isLabel = false, exists = false;
			if (instruction.charAt(0) == '(') {
				label = instruction.substring(1, instruction.indexOf(')'));
				data = lineNumber;
				isLabel = true;
			} else if (instruction.charAt(0) == '@' && Character.isAlphabetic((int) instruction.charAt(1))) {
				label = instruction.substring(1);
				data = variableValue;
				isLabel = true;
			}

			if (isLabel) {

				for (MemoryFragment fragment : listMemory) {
					if (fragment.name.equals(label)) {
						exists = true;
					}
				}

				if (!exists) {
					listMemory.add(new MemoryFragment(1, label, data));
					if (data == variableValue) {
						variableValue++;
					}
				}

			}

			lineNumber++;

		}

		tableHackAsm.setModel(new JTableHackAsm(listHackAsm));
		Collections.sort(listMemory,
				Comparator.comparing(MemoryFragment::getPriority).thenComparing(MemoryFragment::getData));
		tableMemory.setModel(new JTableMemory(listMemory));

		lblFileLoaded.setVisible(true);
		if (isLoad) {
			lblCompare.setVisible(true);
		}
		btnRun.setVisible(true);

	}
}