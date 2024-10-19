package dev.fernando.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.fernando.dsmovie.dto.MovieDTO;
import dev.fernando.dsmovie.dto.ScoreDTO;
import dev.fernando.dsmovie.entities.MovieEntity;
import dev.fernando.dsmovie.entities.ScoreEntity;
import dev.fernando.dsmovie.entities.UserEntity;
import dev.fernando.dsmovie.repositories.MovieRepository;
import dev.fernando.dsmovie.repositories.ScoreRepository;
import dev.fernando.dsmovie.services.exceptions.ResourceNotFoundException;
import dev.fernando.dsmovie.tests.MovieFactory;
import dev.fernando.dsmovie.tests.ScoreFactory;
import dev.fernando.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;

	private UserEntity user;
	private MovieEntity movie;
	private Long existingMovieId, nonExistingMovieId;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;

	@BeforeEach
	void setUp() throws Exception {
		user = UserFactory.createUserEntity();
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		score = ScoreFactory.createScoreEntity();
		movie = score.getId().getMovie();
		movie.setId(existingMovieId);
		scoreDTO = new ScoreDTO(score);

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movie);

		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(score);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO movieDTO = service.saveScore(scoreDTO);
		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(movie.getId(), movieDTO.getId());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		movie.setId(nonExistingMovieId);
		scoreDTO = new ScoreDTO(score);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));
	}
}
