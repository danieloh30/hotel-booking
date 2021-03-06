package com.redhat.hotelbooking.repository;


import com.redhat.hotelbooking.bean.Authentication;
import org.springframework.data.repository.CrudRepository;

public interface AuthenticationRepository extends CrudRepository<Authentication, Integer> {
    public Authentication findByEmail(String email);
}