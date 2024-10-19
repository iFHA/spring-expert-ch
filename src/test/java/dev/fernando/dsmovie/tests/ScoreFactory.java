package dev.fernando.dsmovie.tests;

import dev.fernando.dsmovie.dto.ScoreDTO;
import dev.fernando.dsmovie.entities.MovieEntity;
import dev.fernando.dsmovie.entities.ScoreEntity;
import dev.fernando.dsmovie.entities.UserEntity;

public class ScoreFactory {
	
	public static Double scoreValue = 4.5;
	
	public static ScoreEntity createScoreEntity() {
		MovieEntity movie = MovieFactory.createMovieEntity();
		UserEntity user = UserFactory.createUserEntity();
		ScoreEntity score = new ScoreEntity();
		
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(scoreValue);
		movie.getScores().add(score);
		return score;
	}
	
	public static ScoreDTO createScoreDTO() {
		ScoreEntity score = createScoreEntity();
		return new ScoreDTO(score.getId().getMovie().getId(), score.getValue());
	}
}
