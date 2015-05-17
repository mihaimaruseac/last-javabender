import java.io.File;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccessCounter {
	public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private HashMap<File, Integer> accessList;

	public AccessCounter() {
		accessList = new HashMap<>();
	}

	public void increment(File f) {
		lock.writeLock().lock();
		try {
			accessList.put(f, accessList.getOrDefault(f, 0) + 1);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int getCount(File f) {
		lock.readLock().lock();
		try {
			return accessList.getOrDefault(f, 0);
		} finally {
			lock.readLock().unlock();
		}
	}
}
