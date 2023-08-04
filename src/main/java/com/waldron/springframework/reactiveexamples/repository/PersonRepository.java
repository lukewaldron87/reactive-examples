package com.waldron.springframework.reactiveexamples.repository;

import com.waldron.springframework.reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> getById(Integer id);

    Mono<Person> findById(int id);

    Flux<Person> findAll();
}
