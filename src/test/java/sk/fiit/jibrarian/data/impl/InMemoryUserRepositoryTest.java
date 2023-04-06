package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.data.UserRepository.UserNotFound;
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

    @Test
    void updateUserNotFound() {
        assertThrows(UserNotFound.class, () -> inMemoryUserRepository.updateUser(user));
    }

    @Test
    void updateUserSuccessfully() throws AlreadyExistingUserException, UserNotFound {
        inMemoryUserRepository.saveUser(user);
        user.setRole(Role.ADMIN);
        inMemoryUserRepository.updateUser(user);
        var userFromRepo = inMemoryUserRepository.getUserByEmail(user.getEmail());
        assertTrue(userFromRepo.isPresent());
        assertEquals(user, userFromRepo.get());
    }

    @Test
    void getAllLibrarians() throws AlreadyExistingUserException {
        user.setRole(Role.LIBRARIAN);
        inMemoryUserRepository.saveUser(user);
        var librarians = inMemoryUserRepository.getAllLibrarians();
        assertEquals(1, librarians.size());
        assertEquals(user, librarians.get(0));
    }

    @Test
    void getAllAdmins() throws AlreadyExistingUserException {
        user.setRole(Role.ADMIN);
        inMemoryUserRepository.saveUser(user);
        var admins = inMemoryUserRepository.getAllAdmins();
        assertEquals(1, admins.size());
        assertEquals(user, admins.get(0));
    }

    @Test
    void saveCurrentlyLoggedInUser() {
        inMemoryUserRepository.saveCurrentlyLoggedInUser(user);
        var loggedInUser = inMemoryUserRepository.getCurrentlyLoggedInUser();
        assertTrue(loggedInUser.isPresent());
        assertEquals(user, loggedInUser.get());
    }


    @Test
    void getCurrentlyLoggedInUserNoneSaved() {
        var loggedInUser = inMemoryUserRepository.getCurrentlyLoggedInUser();
        assertTrue(loggedInUser.isEmpty());
    }

    @Test
    void deleteUserNotFound() {
        assertThrows(UserNotFound.class, () -> inMemoryUserRepository.deleteUser(user));
    }

    @Test
    void deleteUserSuccessfully() throws AlreadyExistingUserException, UserNotFound {
        inMemoryUserRepository.saveUser(user);
        inMemoryUserRepository.deleteUser(user);
        var userFromRepo = inMemoryUserRepository.getUserByEmail(user.getEmail());
        assertTrue(userFromRepo.isEmpty());
    }
}