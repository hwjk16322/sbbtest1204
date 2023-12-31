package com.example.sbbtest.article;

import com.example.sbbtest.Article;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Integer> {

    List<Article> findAll(Specification<Article> spec);
}
