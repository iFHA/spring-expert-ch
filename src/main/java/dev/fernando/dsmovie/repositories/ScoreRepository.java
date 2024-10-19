package dev.fernando.dsmovie.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.fernando.dsmovie.entities.ScoreEntity;
import dev.fernando.dsmovie.entities.ScoreEntityPK;

public interface ScoreRepository extends JpaRepository<ScoreEntity, ScoreEntityPK> {

}