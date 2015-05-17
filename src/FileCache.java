import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class FileCache {
	private final int SIZE = Integer.parseInt(TinyHttpd.properties.getProperty("FileCacheSize", "10"));

	protected HashMap<File, byte[]> contents = new HashMap<>();
	private ReentrantLock lock = new ReentrantLock();

	public byte[] fetch(File f) {
		lock.lock();
		try {
			if (contents.containsKey(f))
				return contents.get(f);
		} finally {
			lock.unlock();
		}

		/* cacheFile will retake the lock but we don't want to call it
		 * with a lock taken because reading file contents might take
		 * too long and will stall other threads
		 */
		return cacheFile(f);
	}

	private byte[] cacheFile(File f) {
		lock.lock();
		try {
			if (contents.size() >= SIZE)
				removeEntry();
		} finally {
			lock.unlock();
		}

		/* have no lock taken here because reading might take a long
		 * time and we don't want to stall other threads
		 */
		try {
			int len = (int)f.length();
			DataInputStream fin = new DataInputStream(new FileInputStream(f));
			try {
				byte buf[] = new byte[len];
				fin.readFully(buf);

				lock.lock();
				try {
					contents.put(f, buf);
					addEntryExtra(f);
					return buf;
				} finally {
					lock.unlock();
				}
			} finally {
				fin.close();
			}
		} catch (IOException ex) {
			/* impossible anyway */
			ex.printStackTrace();
			return null;
		}
	}

	/* The following methods are always called with FileCache.lock taken */
	protected abstract void removeEntry();
	protected abstract void addEntryExtra(File f);
}
