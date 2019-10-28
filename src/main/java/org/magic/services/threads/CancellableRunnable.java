package org.magic.services.threads;

public interface CancellableRunnable extends Runnable {

	void cancel();
}
