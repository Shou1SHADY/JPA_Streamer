package com.demo.jpastreamer.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDomain, Long> {
}
