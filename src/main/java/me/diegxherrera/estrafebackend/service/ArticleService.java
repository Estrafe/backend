package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Article;
import me.diegxherrera.estrafebackend.model.UserEntity;
import me.diegxherrera.estrafebackend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // ✅ Get all articles
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // ✅ Get all articles by author ID.
    public List<Article> getArticlesByAuthorId(UserEntity author) {
        return articleRepository.findAllByAuthorId(author.getId());
    }

    // ✅ Get an article by Article ID.
    public Optional<Article> getArticleById(Article article) {
        return articleRepository.findById(article.getId());
    }
}