package dev.fernando.dsmovie.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.fernando.dsmovie.dto.MovieDTO;
import dev.fernando.dsmovie.entities.MovieEntity;
import dev.fernando.dsmovie.repositories.MovieRepository;
import dev.fernando.dsmovie.services.exceptions.DatabaseException;
import dev.fernando.dsmovie.services.exceptions.ResourceNotFoundException;
import dev.fernando.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private MovieEntity movie;
	private MovieDTO movieDTO;
	private PageImpl<MovieEntity> page;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;


	@BeforeEach
	void setUp() throws Exception {
		movie = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movie);
		page = new PageImpl<>(List.of(movie));
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		

		Mockito.when(repository.searchByTitle(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(movie);
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(movie);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Page<MovieDTO> pg = service.findAll("titulo teste", PageRequest.of(0, 10));
		Assertions.assertNotNull(pg);
		Assertions.assertTrue(pg.getContent().size() == 1);
		Assertions.assertEquals(movie.getId(), pg.getContent().get(0).getId());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO dto = service.findById(existingId);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(movie.getId(), dto.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO dto = service.insert(movieDTO);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(movie.getId(), dto.getId());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO dto = service.update(existingId, movieDTO);
		Assertions.assertNotNull(dto);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, movieDTO));
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId));
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
	}
}
