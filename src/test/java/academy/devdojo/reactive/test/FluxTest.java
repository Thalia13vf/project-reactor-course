package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
public class FluxTest {
    //onSubscribe -> request(backpressure) -> onNext ... -> onComplete
    @Test
    public void fluxSubscriber(){
        Flux<String> fluxString = Flux.just("Thalia", "Vilela", "Ferreira").log();

        StepVerifier.create(fluxString)
                .expectNext("Thalia", "Vilela", "Ferreira")
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers(){
        Flux<Integer> flux = Flux.range(1, 5).log();

        flux.subscribe(integer -> log.info("Numero {}", integer)); //consumer - void accept(T elemento)

        log.info("-------------------------------------------------");
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList(){
        Flux<Integer> listaDeNumeros = Flux.fromIterable(List.of(1, 2, 3, 4, 5))
                .log();

        listaDeNumeros.subscribe(integer -> log.info("Numero {}", integer)); //consumer - void accept(T elemento)

        log.info("-------------------------------------------------");
        StepVerifier.create(listaDeNumeros)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersError(){

        //OnSubscribe -> request(backpressure) -> onNext(1), onNext(2), onNext(3), OnNext(4) -> cancel() -> stackTraceException

        Flux<Integer> listaDeNumeros = Flux.range(1, 5) //pubisher publicando, quando for 4 lança exception
                .log()
                .map(integer -> {
                    if(integer == 4){
                        throw new IndexOutOfBoundsException("Index Error");
                    }
                    return integer;
                });

        listaDeNumeros.subscribe(integer -> log.info("Numero {}", integer), Throwable::printStackTrace,
                () -> log.info("Done"), subscription -> subscription.request(3)); // backpressure 3, antes da exception ser lançada
        //Runnable - void run()         Consumer - void accept(T t)

        log.info("-------------------------------------------------");
        StepVerifier.create(listaDeNumeros)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }
}
