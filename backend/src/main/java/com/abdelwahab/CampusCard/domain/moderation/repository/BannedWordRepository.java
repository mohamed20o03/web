package com.abdelwahab.CampusCard.domain.moderation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abdelwahab.CampusCard.domain.moderation.model.BannedWord;

import java.util.List;

public interface BannedWordRepository extends JpaRepository<BannedWord, Integer> {
    BannedWord findByWord(String word);
    List<BannedWord> findAllByOrderByWordAsc();
}
