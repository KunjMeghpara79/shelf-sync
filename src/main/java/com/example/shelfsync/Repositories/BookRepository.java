package com.example.shelfsync.Repositories;

import com.example.shelfsync.Models.Entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Integer> {

}
