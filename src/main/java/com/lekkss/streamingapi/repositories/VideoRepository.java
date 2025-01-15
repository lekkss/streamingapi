package com.lekkss.streamingapi.repositories;

import com.lekkss.streamingapi.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Optional<Video> findByTitle(String title);
}
