package com.alphabook.repository;

import com.alphabook.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Hoang Van Khoi
 * @date 4/5/2026
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
}
