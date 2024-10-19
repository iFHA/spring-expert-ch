package dev.fernando.dsmovie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.fernando.dsmovie.dto.MovieDTO;
import dev.fernando.dsmovie.dto.ScoreDTO;
import dev.fernando.dsmovie.entities.MovieEntity;
import dev.fernando.dsmovie.entities.ScoreEntity;
import dev.fernando.dsmovie.entities.UserEntity;
import dev.fernando.dsmovie.repositories.MovieRepository;
import dev.fernando.dsmovie.repositories.ScoreRepository;
import dev.fernando.dsmovie.services.exceptions.ResourceNotFoundException;

@Service
public class ScoreService {

	@Autowired
	private UserService userService;
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private ScoreRepository scoreRepository;
	
	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {
		
		UserEntity user = userService.authenticated();
		
		MovieEntity movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));		
		
		ScoreEntity score = new ScoreEntity();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());
		
		score = scoreRepository.saveAndFlush(score);
		
		double sum = 0.0;
		for (ScoreEntity s : movie.getScores()) {
			sum = sum + s.getValue();
		}
			
		double avg = sum / movie.getScores().size();
		
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		movie = movieRepository.save(movie);
		
		return new MovieDTO(movie);
	}
}
