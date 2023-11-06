package org.bsdevelopment.workload;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The {@code WorkloadRunnable} class implements the {@link Runnable} interface
 * and is responsible for managing and executing a queue of workloads.
 *
 * @author 7smile7 (<a href="https://www.spigotmc.org/threads/409003/">SOURCE</a>)
 * @author brainsynder
 */
public class WorkloadRunnable implements Runnable {
    // The maximum allowed time in milliseconds per tick for workload execution.
    private double MAX_MILLIS_PER_TICK = 2.5;

    // The maximum allowed time in nanoseconds per tick based on MAX_MILLIS_PER_TICK.
    private final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    // A deque to store the workloads to be executed.
    private final Deque<IWorkload> workloadDeque = new ArrayDeque<>();

    /**
     * Adds a workload to the queue for execution.
     *
     * @param workload The workload to be added to the queue.
     */
    public void addWorkload(IWorkload workload) {
        this.workloadDeque.add(workload);
    }

    /**
     * Updates the maximum allowed time per tick for workload execution.
     *
     * @param millisPerTick The new maximum time limit in milliseconds per tick.
     */
    public void updateMillisPerTick(double millisPerTick) {
        MAX_MILLIS_PER_TICK = millisPerTick;
    }

    /**
     * Executes workloads from the queue within the specified time limit per tick.
     */
    @Override
    public void run() {
        if (workloadDeque.isEmpty()) return;

        // Calculate the stop time based on the maximum time limit per tick.
        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        // Retrieve the last workload in the queue.
        IWorkload lastElement = this.workloadDeque.peekLast();
        IWorkload nextLoad = null;

        // Execute workloads until the time limit is reached or all workloads are processed.
        while ((System.nanoTime() <= stopTime)
                && (!this.workloadDeque.isEmpty())
                && (nextLoad != lastElement)) {
            nextLoad = this.workloadDeque.poll();
            nextLoad.compute();

            // Check if the workload is an instance of IScheduledWorkload and should be rescheduled.
            if (nextLoad instanceof IScheduledWorkload scheduledWorkload) {
                if (scheduledWorkload.shouldBeRescheduled()) addWorkload(scheduledWorkload);
            }
        }
    }
}
