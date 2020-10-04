package final_program;

public class MemoryFragment {
	public int priority;
	public String name;
	public int data;

	public MemoryFragment(int priority, String name, int data) {
		this.priority = priority;
		this.name = name;
		this.data = data;
	}

	public MemoryFragment(MemoryFragment memoryFragment) {
		this.name = memoryFragment.name;
		this.data = memoryFragment.data;
	}

	public int getPriority() {
		return priority;
	}
	
	public int getData() {
		return data;
	}
}
