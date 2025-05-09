package org.beta;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class FlowExample {
    public static void main(String[] args) throws Exception {
    	
    	//en remplacement des Observable
    	SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

    	//pour les observer
        var subscriber = new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1); // Demande d'un élément
            }

            @Override
            public void onNext(String item) {
                System.out.println("Reçu : " + item);
                subscription.request(1); // Demande du suivant
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Terminé");
            }
        };

        publisher.subscribe(subscriber);
        publisher.submit("Hello");
        publisher.submit("Prout");
        publisher.close();

        Thread.sleep(1000); // Attente pour éviter arrêt prématuré
    }
}
