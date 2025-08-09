package com.news.SpringBootNews_wb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.SpringBootNews_wb.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


  Optional<User> findByEmail(String email);
  

}
