package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Article;
import me.diegxherrera.estrafebackend.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
    List<Article> findAllByAuthorId(UUID authorId);
}