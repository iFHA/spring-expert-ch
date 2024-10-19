package dev.fernando.dsmovie.tests;

import dev.fernando.dsmovie.entities.UserEntity;

public class UserFactory {
	
	public static UserEntity createUserEntity() {
		UserEntity user = new UserEntity(2L, "Maria", "maria@gmail.com", "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
		return user;
	}
}
