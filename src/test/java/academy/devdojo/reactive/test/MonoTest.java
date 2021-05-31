package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Locale;

@Slf4j
public class MonoTest {

    @Test
    public void monoSubscriber() {
        String nome = "Thalia";
        Mono<String> mono = Mono.just(nome)
                .log();

        mono.subscribe();

        StepVerifier.create(mono)
                .expectNext(nome)
                .verifyComplete();

        log.info("Mono {} ", mono);
    }

    @Test
    public void monoSubscriberConsumer() {
        String nome = "Thalia";
        Mono<String> mono = Mono.just(nome)
                .log();

        mono.subscribe(s -> log.info("Valor da String nome {}", s));

        log.info("------------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(nome)
                .verifyComplete();

        log.info("Mono {} ", mono);
    }

    @Test
    public void monoSubscriberConsumerError() {
        String nome = "Thalia";
        Mono<String> mono = Mono.just(nome)
                .map(s -> {throw new RuntimeException("Testando mono com erro");});

        //mono.subscribe(s -> log.info("Valor da String nome {}", s), s -> log.error("ERRO"));
        mono.subscribe(s -> log.info("Valor da String nome {}", s), Throwable::printStackTrace);

        log.info("------------------------------------------------------");
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();

        log.info("Mono {} ", mono);
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String nome = "Thalia";
        Mono<String> mono = Mono.just(nome)
                .log()
                .map(s -> s.toUpperCase());

        mono.subscribe(s -> log.info("Valor da String nome {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINALIZADO!!!"));

        log.info("------------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(nome.toUpperCase())
                .verifyComplete();

        log.info("Mono {} ", mono);
    }
    @Test
    public void monoSubscriberConsumerSubscription() {
        String nome = "Thalia";
        Mono<String> mono = Mono.just(nome)
                .log()
                .map(s -> s.toUpperCase());

        mono.subscribe(s -> log.info("Valor da String nome {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINALIZADO!!!"),
                subscription -> subscription.request(10L));

        log.info("Mono {} ", mono);
    }

    @Test
    public void monoDoOnMethods() {
        String nome = "Thalia";

        Mono<Object> mono = Mono.just(nome)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed {}", subscription))
                .doOnRequest(longNumber -> log.info("Request recebida ..... {}", longNumber))
                .doOnNext(s -> log.info("Valor do doOnNext 1 ... {}", s))
                .flatMap(s -> Mono.empty()) //remove os valores que tem, o mono fica vazio
                .doOnNext(s -> log.info("Valor do doOnNext 2... {}", s))
                .doOnSuccess(s -> log.info("doOnSuccess executando {}", s));

        log.info("---------------------------------------------------------");
        mono.subscribe(s -> log.info("Valor do método subscribe {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINALIZADO !!!!!!!"));
    }

    @Test
    public void doOnErrorResume() {
        String mensagem = "Mensagem depois do erro";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception error."))
                .doOnError(erro -> log.error("Mensagem de erro {}", erro.getMessage()))
                .onErrorResume(s -> {
                    log.info("Dentro do onErrorResume");
                    return Mono.just(mensagem);
                })
                .log();

        StepVerifier.create(error)
                .expectNext(mensagem)
                .verifyComplete();
    }

    @Test
    public void doOnErrorReturn() {
        String mensagem = "Empty";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception error."))
                .doOnError(erro -> log.error("Mensagem de erro {}", erro.getMessage()))
                .onErrorReturn(mensagem)
                .log();

        StepVerifier.create(error)
                .expectNext(mensagem)
                .verifyComplete();
    }

}
