import java.io.File;

public class FileCacheLFU extends FileCache {
	private static FileCacheLFU instance = new FileCacheLFU();
	/* keep a different ac for thread sync */
	private AccessCounter ac = new AccessCounter();

	private FileCacheLFU() {}

	public static FileCacheLFU getInstance() {
		return instance;
	}

	@Override
	protected void removeEntry() {
		File toRemove = null;
		int accessedMax = Integer.MAX_VALUE;

		for (File f : contents.keySet()) {
			if (accessedMax > ac.getCount(f)) {
				accessedMax = ac.getCount(f);
				toRemove = f;
			}
		}

		contents.remove(toRemove);
	}

	@Override
	protected void addEntryExtra(File f) {
		ac.increment(f);
	}
}
