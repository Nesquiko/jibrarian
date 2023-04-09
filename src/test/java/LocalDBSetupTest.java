import jdk.jshell.execution.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.UtilAuth;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.CatalogRepository.ItemAlreadyExistsException;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.data.impl.PostgresCatalogRepository;
import sk.fiit.jibrarian.data.impl.PostgresReservationRepository;
import sk.fiit.jibrarian.data.impl.PostgresUserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Disabled("This test is not meant to be run as part of the test suite, it is meant to be run manually to setup the local database")
class LocalDBSetupTest {
    private static ConnectionPool connectionPool;
    private static UserRepository userRepository;
    private static CatalogRepository catalogRepository;
    private static ReservationRepository reservationRepository;

    @BeforeAll
    static void setUpClass() throws SQLException {
        connectionPool = new ConnectionPoolBuilder()
                .setHost("localhost")
                .setPort(42069)
                .setDatabase("jibrarian")
                .setUser("jibrarian")
                .setPassword("password")
                .build();
        clearDatabase();
        catalogRepository = new PostgresCatalogRepository(connectionPool);
        userRepository = new PostgresUserRepository(connectionPool);
        reservationRepository = new PostgresReservationRepository(connectionPool);
    }

    @Test
    @SuppressWarnings("squid:S2699")
    void setupLocalDB() throws URISyntaxException, IOException, AlreadyExistingUserException,
            ItemAlreadyExistsException {
        for (User user : users)
            userRepository.saveUser(user);

        for (Item item : items())
            catalogRepository.saveItem(item);
    }

    private final List<User> users = List.of(
            new User(UUID.randomUUID(), "member1@jibrarian.sk", UtilAuth.hashPassword("123"), Role.MEMBER),
            new User(UUID.randomUUID(), "member2@jibrarian.sk", UtilAuth.hashPassword("123"), Role.MEMBER),
            new User(UUID.randomUUID(), "member3@jibrarian.sk", UtilAuth.hashPassword("123"), Role.MEMBER),
            new User(UUID.randomUUID(), "librarian1@jibrarian.sk", UtilAuth.hashPassword("123"), Role.LIBRARIAN),
            new User(UUID.randomUUID(), "librarian2@jibrarian.sk", UtilAuth.hashPassword("123"), Role.LIBRARIAN),
            new User(UUID.randomUUID(), "admin@jibrarian.sk", UtilAuth.hashPassword("123"), Role.ADMIN)
    );

    private List<Item> items() throws URISyntaxException, IOException {
        return List.of(
                new Item(UUID.randomUUID(), "The Hitchhiker's Guide to the Galaxy", "Douglas Adams",
                        """
                                It's an ordinary Thursday lunchtime for Arthur Dent until his house gets demolished. The
                                Earth follows shortly afterwards to make way for a new hyperspace express route, and his
                                best friend has just announced that he's an alien. At this moment, they're hurtling through
                                space with nothing but their towels and an innocuous-looking book inscribed, in large
                                friendly letters, with the words: DON'T PANIC. The weekend has only just begun...
                                """,
                        "EN", "Fantasy", "0345391802", ItemType.BOOK, 224, 10, 10, 0,
                        itemImage("/hitchhikers-guide-cover.png")
                ),
                new Item(UUID.randomUUID(), "Drak sa vracia", "Dobroslav Chrobak",
                        """
                                Román Drak sa vracia je jedným z troch diel, ktorými sa začína nový edičný rad
                                prinášajúci širokým čitateľským vrstvám najhodnotnejšie diela slovenskej medzivojnovej
                                prózy (popri ňom vyjdú diela Tri gaštanové kone a Švantnerova Dáma). Román má všetky
                                predpoklady zaujať čitateľa aj sedemdesiat rokov po svojom vzniku. Pútavý a nie vždy
                                celkom jasný dej, ľúbostná zápletka, nečakané peripetie, tajomstvom opradená titulná
                                postava, pozorne štylizovaný jazykový prejav a nezakrytá romanesknosť príbehu, v epilógu
                                posunutá až do polohy legendy či mýtu, to všetko je nielen charakteristickým znakom
                                slovenského naturizmu, ale aj dobrého románového čítania vo všeobecnosti.
                                """,
                        "SK", "Roman", "9788055130842", ItemType.BOOK, 128, 5, 5, 0,
                        itemImage("/drak-sa-vracia-cover.jpg")
                ),
                new Item(UUID.randomUUID(), "War and Peace", "Lev Nikolayevich Tolstoy",
                        """
                                At its center are Pierre Bezukhov, searching for meaning in his life; cynical Prince
                                Andrei, ennobled by wartime suffering; and Natasha Rostov, whose impulsiveness threatens
                                to destroy her happiness. As Tolstoy follows the changing fortunes of his characters, he
                                crafts a view of humanity that is both epic and intimate and that continues to define
                                fiction at its most resplendent. This edition includes an introduction, note on the
                                translation, cast of characters, maps, notes on the major battles depicted, and chapter
                                summaries. Praise for Antony Brigg's translation of "War and Peace" "The best translation
                                so far of Tolstoy's masterpiece into English." -Robert A. Maguire, professor emeritus of
                                Russian studies, Columbia University "In Tolstoy's work part of the translator's
                                difficulty lies in conveying not only the simplicity but the subtlety of the book's
                                scale and effect.
                                """,
                        "EN", "Romance", "9780143039990", ItemType.BOOK, 1408, 5, 5, 0,
                        itemImage("/war-and-peace-cover.jpg")
                ),
                new Item(UUID.randomUUID(), "All Quiet on the Western Front", "Erich Maria Remarque",
                        """
                                The novel is set during World War I and is narrated by Paul Bäumer, a young German soldier
                                who enlists in the German army to fight in the war. The novel is a first-person account of
                                the war from the perspective of a young soldier. The novel is a Bildungsroman, a coming-of-age
                                story, and is considered a classic of 20th-century literature. The novel is also notable for
                                its anti-war sentiment, which is a common theme in Remarque's work.
                                """,
                        "EN", "War", "9781907360671", ItemType.BOOK, 364, 5, 5, 0,
                        itemImage("/all-quiet-on-western-cover.jpg")
                )
        );
    }

    private byte[] itemImage(String name) throws URISyntaxException, IOException {
        URI uri = Objects.requireNonNull(getClass().getResource(name)).toURI();
        Path path = Path.of(uri);
        return Files.readAllBytes(path);
    }

    private static void clearDatabase() {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement("""
                            truncate table users cascade;
                            truncate table items cascade;
                            truncate table reservations;
                            truncate table borrowed_items;
                        """)
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
