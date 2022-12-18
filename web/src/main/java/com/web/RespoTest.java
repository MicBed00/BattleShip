package com.web;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespoTest extends CrudRepository<Video, Integer> {
}
