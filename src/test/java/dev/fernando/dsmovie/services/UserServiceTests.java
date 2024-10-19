package dev.fernando.dsmovie.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import dev.fernando.dsmovie.entities.UserEntity;
import dev.fernando.dsmovie.projections.UserDetailsProjection;
import dev.fernando.dsmovie.repositories.UserRepository;
import dev.fernando.dsmovie.tests.UserDetailsFactory;
import dev.fernando.dsmovie.tests.UserFactory;
import dev.fernando.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomUserUtil util;
	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		userEntity = UserFactory.createUserEntity();

		Mockito.when(util.getLoggedUsername()).thenReturn(userEntity.getUsername());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));

		var user = service.authenticated();
		Assertions.assertNotNull(user);
		Assertions.assertEquals(userEntity.getId(), user.getId());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		List<UserDetailsProjection> list = UserDetailsFactory.createCustomClientUser(userEntity.getUsername());
		Mockito.when(userRepository.searchUserAndRolesByUsername(any())).thenReturn(list);

		Assertions.assertDoesNotThrow(() -> {
			var user = service.loadUserByUsername(userEntity.getUsername());
			Assertions.assertNotNull(user);
			Assertions.assertEquals(list.get(0).getUsername(), user.getUsername());
		});
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		List<UserDetailsProjection> list = UserDetailsFactory.createCustomClientUser(userEntity.getUsername());
		Mockito.when(userRepository.searchUserAndRolesByUsername(any())).thenReturn(Collections.emptyList());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			var user = service.loadUserByUsername(userEntity.getUsername());
			Assertions.assertNotNull(user);
			Assertions.assertEquals(list.get(0).getUsername(), user.getUsername());
		});
	}
}
