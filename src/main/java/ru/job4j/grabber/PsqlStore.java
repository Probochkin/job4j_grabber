package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private static final String PAGE_LINK = "https://career.habr.com/vacancies/java_developer?page=";
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("insert into grabber(title, link, description, created) values (?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from grabber")) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
         while (resultSet.next()) {
             posts.add(getPost(resultSet));
         }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from grabber where id=?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
             if (resultSet.next()) {
            post = getPost(resultSet);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public Post getPost(ResultSet resultSet) throws SQLException {
        return new Post(resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("link"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            config.load(in);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(config);
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
       List<Post> posts = habrCareerParse.list(PAGE_LINK);
        for (Post post : posts) {
            psqlStore.save(post);
        }
       psqlStore.getAll().forEach(System.out::println);
        System.out.println(psqlStore.findById(1));
    }
}
