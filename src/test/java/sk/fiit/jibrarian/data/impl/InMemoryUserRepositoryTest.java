package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository inMemoryUserRepository;
    private User user;

    @BeforeEach
    void setUp() {
        inMemoryUserRepository = new InMemoryUserRepository();
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);
    }

    @Test
    void saveUserSuccessfully() throws AlreadyExistingUserException {
        inMemoryUserRepository.saveUser(user);
        var userFromRepo = inMemoryUserRepository.getUserByEmail(user.getEmail());
        assertTrue(userFromRepo.isPresent());
        assertEquals(user, userFromRepo.get());
    }

    @Test
    void saveUserWithExistingEmail() throws AlreadyExistingUserException {
        inMemoryUserRepository.saveUser(user);
        var userWithSameEmail = new User(UUID.randomUUID(), user.getEmail(), "passHash", Role.MEMBER);
        assertThrows(AlreadyExistingUserException.class, () -> inMemoryUserRepository.saveUser(userWithSameEmail));
    }

}