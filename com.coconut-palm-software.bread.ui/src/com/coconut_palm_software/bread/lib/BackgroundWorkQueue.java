package com.coconut_palm_software.bread.lib;

import java.util.LinkedList;

import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;

public class BackgroundWorkQueue {

	private Thread workerThread;
	private Object monitor = new Object();
	private LinkedList<ISafeRunnable> workQueue = new LinkedList<ISafeRunnable>();
	
	public void submit(Runnable runnable) {
		synchronized(monitor) {
			ensureWorkerThreadIsRunning();
			workQueue.add(makeSafeRunnable(runnable));
			monitor.notifyAll();
		}
	}

	private ISafeRunnable makeSafeRunnable(final Runnable runnable) {
		ISafeRunnable safeRunnable;
		if (runnable instanceof ISafeRunnable) {
			safeRunnable = (ISafeRunnable) runnable;
		} else {
			safeRunnable = new ISafeRunnable() {
				public void handleException(Throwable exception) {
					Policy
							.getLog()
							.log(
									new Status(
											IStatus.ERROR,
											Policy.JFACE_DATABINDING,
											IStatus.OK,
											"Unhandled exception: " + exception.getMessage(), exception)); //$NON-NLS-1$
				}
				public void run() throws Exception {
					runnable.run();
				}
			};
		}
		return safeRunnable;
	}

	private void ensureWorkerThreadIsRunning() {
		if (workerThread == null) {
			workerThread = new Thread() {
				public void run() {
					try {
						while (true) {
							ISafeRunnable nextJob = null;
							synchronized (monitor) {
								while (workQueue.isEmpty()) {
									monitor.wait();
								}
								nextJob = workQueue.removeFirst();
							}
							SafeRunner.run(nextJob);
						}
					} catch (Exception e) {
						workerThread = null;
					}
				}
			};
			workerThread.start();
		}
	}
	

	public boolean isInWorkerThread() {
		return Thread.currentThread().equals(workerThread);
	}
}
