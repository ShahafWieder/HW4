import java.util.HashMap;
import java.util.Map;

/**
 * The Database class represents a simple database implementation that supports concurrent read and write operations.
 */
public class Database {
    private Map<String, String> data;
    private int maxNumOfReaders;
    private int activeReaders;
    private boolean isWriting;
    private final Object lock = new Object();

    /**
     * Constructs a Database object with the specified maximum number of readers.
     *
     * @param maxNumOfReaders The maximum number of concurrent readers allowed.
     */
    public Database(int maxNumOfReaders) {
        data = new HashMap<>();
        this.maxNumOfReaders = maxNumOfReaders;
        this.activeReaders = 0;
        this.isWriting = false;
    }

    /**
     * Puts a key-value pair into the database.
     *
     * @param key   The key associated with the value.
     * @param value The value to be stored in the database.
     */
    public void put(String key, String value) {
            data.put(key, value);
    }

    /**
     * Retrieves the value associated with the given key from the database.
     *
     * @param key The key whose value to retrieve.
     * @return The value associated with the key, or null if the key is not found.
     */
    public String get(String key) {
            return data.get(key);
    }

    /**
     * Attempts to acquire a read lock without blocking.
     * If successful, increments the count of active readers.
     *
     * @return true if the read lock is acquired, false otherwise.
     */
    public boolean readTryAcquire() {
        synchronized (lock) {
            if (!isWriting && activeReaders < maxNumOfReaders) {
                activeReaders++;
                return true;
            }
            return false;
        }
    }

    /**
     * Acquires a read lock, blocking until it can be obtained.
     *
     * @throws RuntimeException if the current thread is interrupted while waiting.
     */
    public void readAcquire() {
        synchronized (lock) {
            while (isWriting || activeReaders >= maxNumOfReaders) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            activeReaders++;
        }
    }

    /**
     * Releases a read lock and notifies waiting threads.
     *
     * @throws IllegalMonitorStateException if there are no active readers.
     */
    public void readRelease() {
        synchronized (lock) {
            if (activeReaders <= 0) {
                throw new IllegalMonitorStateException("Illegal read release attempt");
            }
            activeReaders--;
            if (activeReaders == 0) {
                lock.notifyAll();
            }
        }
    }

    /**
     * Acquires a write lock, blocking until it can be obtained.
     *
     * @throws RuntimeException if the current thread is interrupted while waiting.
     */
    public void writeAcquire() {
        synchronized (lock) {
            while (isWriting || activeReaders > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            isWriting = true;
        }
    }

    /**
     * Attempts to acquire a write lock without blocking.
     * If successful, sets the isWriting flag to true and returns true; otherwise, returns false.
     *
     * @return true if the write lock is acquired, false otherwise.
     */
    public boolean writeTryAcquire() {
        synchronized (lock) {
            if (!isWriting && activeReaders == 0) {
                isWriting = true;
                return true;
            }
            return false;
        }
    }

    /**
     * Releases a write lock, notifies waiting threads, and sets the isWriting flag to false.
     *
     * @throws IllegalMonitorStateException if the current thread is not the one currently writing.
     */
    public void writeRelease() {
        synchronized (lock) {
            if (!isWriting) {
                throw new IllegalMonitorStateException("Illegal write release attempt");
            }
            isWriting = false;
            lock.notifyAll();
        }
    }
}
