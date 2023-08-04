package com.waldron.springframework.reactiveexamples;

import com.waldron.springframework.reactiveexamples.domain.Person;
import com.waldron.springframework.reactiveexamples.repository.PersonRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository;

    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);

        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);

        StepVerifier.create(personMono).expectNextCount(1l).verifyComplete();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.map(person -> {
            System.out.println(person.toString());
            return person.getFirstName();
        }).subscribe(firstName -> {
            System.out.println("from map: "+firstName);
        });
    }

    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxToListMono(){
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> {
            list.forEach(person -> {
                System.out.println(person.toString());
            });
        });
    }

    @Test
    void testFindPersonById() {
        Flux<Person> personFlux = personRepository.findAll();

        final int id = 3;
        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == id)
                .next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });

        assertEquals(id, personMono.block().getId());
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final int id = 33;
        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == id)
                .next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = personRepository.findAll();

        final int id = 33;
        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == id)
                .single();

        personMono.doOnError(throwable -> System.out.println("id "+id+" not found"))
                // if there's an error return an empty person object
                // this object will be passed to the subscribe method
                .onErrorReturn(Person.builder().id(id).build())
                // otherwise print the existing object
                .subscribe(person -> {
                    System.out.println(person.toString());
                });
    }

    @Test
    void findById_shouldReturnCorrectPerson() {

        final int id = 3;
        StepVerifier.create(personRepository.findById(id))
                .assertNext(person -> assertEquals(id, person.getId()))
                .verifyComplete();
    }

    @Test
    public void findById_shouldReturnEmptyPerson_whenIdNotFound() {


        final int id = 33;
        StepVerifier.create(personRepository.findById(id))
                .assertNext(person -> {
                    assertEquals(id, person.getId());
                    assertNull(person.getFirstName());
                    assertNull(person.getLastName());
                })
                .verifyComplete();

    }
}