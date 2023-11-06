package org.bsdevelopment.workload;

/**
 * The {@code IScheduledWorkload} interface extends the {@link IWorkload} interface
 * to represent a workload that can be scheduled and executed periodically.
 *
 * @author 7smile7 (<a href="https://www.spigotmc.org/threads/409003/">SOURCE</a>)
 * @author brainsynder
 */
public interface IScheduledWorkload extends IWorkload {
    /**
     * Checks whether this workload should be rescheduled for future execution.
     * Implementations can return true if the workload needs to be rescheduled or false if not.
     *
     * @return {@code true} if the workload should be rescheduled; {@code false} otherwise.
     */
    default boolean shouldBeRescheduled() {
        return false;
    }
}

