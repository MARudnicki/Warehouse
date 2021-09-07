package com.example.demo.repositories;

import com.example.demo.domains.Record;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Record, Long>, MongoRepository<Record, Long> {

}
