import java.util.ArrayList;
import java.util.concurrent.locks.*;

public final class StaticThreadPool {
	private WaitingRunnableQueue queue = null;
	private ArrayList<ThreadPoolThread> availableThreads = null;
	private static StaticThreadPool instance = new StaticThreadPool();

	private final int MAX_THREAD_NUM = 20;

	private StaticThreadPool() {
		queue = new ArrayListWaitingRunnableQueue(this);
		availableThreads = new ArrayList<>();

		for(int i = 0; i < MAX_THREAD_NUM; i++) {
			ThreadPoolThread th = new ThreadPoolThread(this, queue, i);
			availableThreads.add(th);
			th.start();
		}
	}

	public static StaticThreadPool getInstance() {
		return instance;
	}

	public void shutdown() {
		for (ThreadPoolThread tpt: availableThreads) {
			tpt.lock.lock();
			try {
				tpt.stopped = true;
				tpt.interrupt();
			} finally {
				tpt.lock.unlock();
			}
		}
	}

	public void execute(Runnable runnable) {
		queue.queueLock.lock();
		try {
			queue.put(runnable);
		} finally {
			queue.queueLock.unlock();
		}
	}

	public int getWaitingRunnableQueueSize() {
		queue.queueLock.lock();
		try {
			return queue.size();
		} finally {
			queue.queueLock.unlock();
		}
	}

	public int getThreadPoolSize() {
		return availableThreads.size();
	}

	private abstract class WaitingRunnableQueue {
		private StaticThreadPool pool;
		private ReentrantLock queueLock;
		private Condition runnablesAvailable;

		public WaitingRunnableQueue(StaticThreadPool pool) {
			this.pool = pool;
			queueLock = new ReentrantLock();
			runnablesAvailable = queueLock.newCondition();
		}

		public abstract int sizeLocked();
		protected abstract void add(Runnable obj);
		protected abstract boolean isEmpty();
		protected abstract Runnable extract();

		public int size() {
			queueLock.lock();
			try {
				return sizeLocked();
			} finally {
				queueLock.unlock();
			}
		}

		public void put(Runnable obj) {
			queueLock.lock();
			try {
				add(obj);
				runnablesAvailable.signalAll();
			} finally {
				queueLock.unlock();
			}
		}

		public Runnable get() {
			queueLock.lock();
			try {
				while(isEmpty())
					runnablesAvailable.await();
				return extract();
			} catch(InterruptedException ex) {
				return null;
			} finally {
				queueLock.unlock();
			}
		}
	}

	private class ArrayListWaitingRunnableQueue extends WaitingRunnableQueue {
		private ArrayList<Runnable> runnables = new ArrayList<>();

		public ArrayListWaitingRunnableQueue(StaticThreadPool pool) {
			super(pool);
		}

		@Override
		public int sizeLocked() {
			return runnables.size();
		}

		@Override
		protected void add(Runnable obj) {
			runnables.add(obj);
		}

		@Override
		protected boolean isEmpty() {
			return runnables.isEmpty();
		}

		@Override
		protected Runnable extract() {
			return runnables.remove(0);
		}
	}

	private class ThreadPoolThread extends Thread {
		private StaticThreadPool pool;
		private WaitingRunnableQueue queue;
		private int id;
		private boolean stopped = false;
		private ReentrantLock lock;

		public ThreadPoolThread(StaticThreadPool pool, WaitingRunnableQueue queue, int id) {
			this.pool = pool;
			this.queue = queue;
			this.id = id;
			this.lock = new ReentrantLock();
		}

		public void run() {
			while(true) {
				lock.lock();
				try {
					if (stopped)
						return;
				} finally {
					lock.unlock();
				}

				Runnable runnable = queue.get();
				if (runnable == null) {
					continue;
				} else {
					runnable.run();
				}
			}
		}
	}
}
