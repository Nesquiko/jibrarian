package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * Persists user
     */
    void saveUser(User user) throws AlreadyExistingUserException;

    /**
     * Return user with matching email, if it doesn't exist, returns empty optional
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Updates user, if it doesn't exist, throws UserNotFound
     */
    void updateUser(User user) throws UserNotFound;

    /**
     * Returns all users which are librarians
     */
    List<User> getAllLibrarians();

    /**
     * Returns all users which are admins
     */
    List<User> getAllAdmins();


    class AlreadyExistingUserException extends Exception {
        public AlreadyExistingUserException(String message) {
            super(message);
        }
    }

    class UserNotFound extends Exception {
        public UserNotFound(String message) {
            super(message);
        }
    }
}
