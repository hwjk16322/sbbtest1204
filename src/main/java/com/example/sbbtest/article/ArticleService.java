package com.example.sbbtest.article;

import com.example.sbbtest.Article;
import com.example.sbbtest.DataNotException;
import com.example.sbbtest.answer.Answer;
import com.example.sbbtest.siteUser.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getList(String kw) {

        Specification<Article> spec = search(kw);
        List<Article> articleList = articleRepository.findAll(spec);
        return articleList;
    }

    public void create(String subject, String content, SiteUser user) {
        Article q = new Article();
        q.setSubject(subject);
        q.setContent(content);
        q.setLocalDateTime(LocalDateTime.now());
        q.setAuthor(user);
        this.articleRepository.save(q);
    }

    public Article findById(Integer id) {
        Optional<Article> oq = this.articleRepository.findById(id);
        return oq.get();
    }

    public Article getArticle(Integer id) {
        Optional<Article> oq = this.articleRepository.findById(id);

        if (oq.isPresent() == false) throw new DataNotException("article not found");

        return oq.get();
    }


    public void modify(Article article, String subject, String content) {
        article.setSubject(subject);
        article.setContent(content);
        article.setModifyDate(LocalDateTime.now());
        this.articleRepository.save(article);
    }

    public void delete(Article article) {
        this.articleRepository.delete(article);
    }


    private Specification<Article> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Article> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거 
                Join<Article, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Article, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목 
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용 
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자 
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용 
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자 
            }
        };
    }
}
