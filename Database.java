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
    public synchronized boolean readTryAcquire() {
        if (!isWriting && activeReaders < maxNumOfReaders) {
            activeReaders++;
            return true;
        }
        return false;
    }

    /**
     * Acquires a read lock, blocking until it can be obtained.
     *
     * @throws RuntimeException if the current thread is interrupted while waiting.
     */
    public synchronized void readAcquire(){
        while (isWriting || activeReaders >= maxNumOfReaders) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        activeReaders++;
    }

    /**
     * Releases a read lock and notifies waiting threads.
     *
     * @throws IllegalMonitorStateException if there are no active readers.
     */
    public synchronized void readRelease() {
        if (activeReaders <= 0) {
            throw new IllegalMonitorStateException("Illegal read release attempt");
        }
        activeReaders--;
        if (activeReaders == 0) {
            notifyAll();
        }
    }

    /**
     * Acquires a write lock, blocking until it can be obtained.
     *
     * @throws RuntimeException if the current thread is interrupted while waiting.
     */
    public synchronized void writeAcquire(){
        while (isWriting || activeReaders > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isWriting = true;
    }

    /**
     * Attempts to acquire a write lock without blocking.
     * If successful, sets the isWriting flag to true and returns true; otherwise, returns false.
     *
     * @return true if the write lock is acquired, false otherwise.
     */
    public synchronized boolean writeTryAcquire() {
        if (!isWriting && activeReaders == 0) {
            isWriting = true;
            return true;
        }
        return false;
    }

    /**
     * Releases a write lock, notifies waiting threads, and sets the isWriting flag to false.
     *
     * @throws IllegalMonitorStateException if the current thread is not the one currently writing.
     */
    public synchronized void writeRelease() {
        if (!isWriting) {
            throw new IllegalMonitorStateException("Illegal write release attempt");
        }
        isWriting = false;
        notifyAll();
    }

}
