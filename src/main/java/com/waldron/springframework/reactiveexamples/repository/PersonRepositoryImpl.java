package com.waldron.springframework.reactiveexamples.repository;

import com.waldron.springframework.reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {

    private Person micael = new Person(1, "Michael", "Weston");
    private Person fiona = new Person(2, "Fiona", "Glenanne");
    private Person sam = new Person(3, "Sam", "Axe");
    private Person jesse = new Person(4, "Jesse", "Porter");

    @Override
    public Mono<Person> getById(Integer id) {
        return Mono.just(micael);
    }

    @Override
    public Mono<Person> findById(int id) {
        return findAll()
                .filter(person -> person.getId() == id)
                .single()
                .doOnError( throwable -> System.out.println("id not found"))
                .onErrorReturn(Person.builder().id(id).build());
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(micael, fiona, sam, jesse);
    }
}
