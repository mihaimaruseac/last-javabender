import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class FileCacheLRU extends FileCache {
	private static FileCacheLRU instance = new FileCacheLRU();
	private HashMap<File, Date> timestamps = new HashMap<>();

	private FileCacheLRU() {}

	public static FileCacheLRU getInstance() {
		return instance;
	}

	@Override
	protected void removeEntry() {
		Date last = new Date();
		File toRemove = null;

		for (File f : contents.keySet()) {
			if (last.after(timestamps.get(f))) {
				last = timestamps.get(f);
				toRemove = f;
			}
		}

		contents.remove(toRemove);
	}

	@Override
	protected void addEntryExtra(File f) {
		timestamps.put(f, new Date());
	}
}
