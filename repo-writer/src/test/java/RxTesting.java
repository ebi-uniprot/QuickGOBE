import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

/**
 * Checking to see how some reactive programming paradigm works. Could it be useful?
 *
 * Created 01/12/15
 * @author Edd
 */
public class RxTesting {
    // =======================================================================================
    @Test
    public void basicObservable() {
        Integer[] numbers = { 0, 1, 2, 3, 4, 5 };

        Observable numberObservable = Observable.from(numbers);

        numberObservable.subscribe(
                (incomingNumber) -> System.out.println("incomingNumber " + incomingNumber),
                (error) -> System.out.println("Something went wrong" + ((Throwable)error).getMessage()),
                () -> System.out.println("This observable is finished")
        );
    }

    // =======================================================================================
    @Test
    public void creatingObservable() {
        Observable.OnSubscribe<String> subscribeFunction = (s) -> {
            Subscriber subscriber = (Subscriber)s;

            for (int ii = 0; ii < 10; ii++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Pushed value " + ii);
                }
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        };

        Observable createdObservable = Observable.create(subscribeFunction);

        createdObservable.subscribe(
                (incomingValue) -> System.out.println("incomingValue " + incomingValue),
                (error) -> System.out.println("Something went wrong" + ((Throwable)error).getMessage()),
                () -> System.out.println("This observable is finished")
        );
    }

    // =======================================================================================
    @Test
    public void filtering() {
        Observable.OnSubscribe<String> subscribeFunction = (s) -> asyncProcessingOnSubscribe(s);

        Observable asyncObservable = Observable.create(subscribeFunction);

        asyncObservable.skip(5).subscribe((incomingValue) -> System.out.println(incomingValue));

    }
    private void asyncProcessingOnSubscribe(Subscriber s) {
        final Subscriber subscriber = (Subscriber)s;
        Thread thread = new Thread(() -> produceSomeValues(subscriber));
        thread.start();
    }

    private void produceSomeValues(Subscriber subscriber) {
        for (int ii = 0; ii < 10; ii++) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext("Pushing value from async thread " + ii);
            }
        }
    }

    // =======================================================================================
    @Test
    public void handleError() {
        Observable.OnSubscribe<String> subscribeFunction = (s) -> produceValuesAndAnError(s);

        Observable createdObservable = Observable.create(subscribeFunction);

        createdObservable.subscribe(
                (incomingValue) -> System.out.println("incoming " + incomingValue),
                (error) -> System.out.println("Something went wrong " + ((Throwable)error).getMessage()),
                () -> System.out.println("This observable is finished")
        );

    }

    private void produceValuesAndAnError(Subscriber s) {
        Subscriber subscriber = (Subscriber)s;

        try {
            for (int ii = 0; ii < 50; ii++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Pushed value " + ii);
                }

                if (ii == 5) {
                    throw new Throwable("Something has gone wrong here");
                }
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        } catch (Throwable throwable) {
            subscriber.onError(throwable);
        }
    }

    // =======================================================================================
    @Test
    public void tweetSimple() {
        Observable<String> tweets = Observable.just("learning RxJava", "Writing blog about RxJava", "RxJava rocks!!");
        tweets.subscribe(System.out::println);
    }

    @Test
    public void equivalentTweetSimple() {
        List<String> tweets = Arrays.asList("learning RxJava", "Writing blog about RxJava", "RxJava rocks!!");
        Observable<String> observable = Observable.from(tweets);
        observable.subscribe(System.out::println);
    }

    @Test
    public void createObservableDoc() {
        Observable.create(subscriber -> {

        });
    }

    @Test
    public void hotObservable() throws InterruptedException {
        ConnectableObservable<Long> hotObservable = Observable.interval(1, TimeUnit.SECONDS).publish();
        hotObservable.subscribe(val -> System.out.println("Subscriber 1 >> " + val));
        hotObservable.connect();

        Thread.sleep(5000);

        hotObservable.subscribe(val -> System.out.println("Subscriber 2 >> " + val));

        Thread.sleep(5000);
    }
}
