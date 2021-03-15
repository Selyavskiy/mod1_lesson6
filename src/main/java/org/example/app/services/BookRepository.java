package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTemplate.query("Select * FROM books", (ResultSet rs,int rowNum)->{
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author, :title, :size)", parameterSource);
        logger.info("store new book: " + book);

    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", parameterSource);
        logger.info("remove book completed: " + bookIdToRemove);
        return true;
    }



    @Override
    public boolean addFileListItem(String fileLink){
        logger.info("add file link: " + fileLink);

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("filelink", fileLink);
        jdbcTemplate.update("INSERT INTO file_links(file_link) VALUES(:filelink)", parameterSource);
        return true;
    }

    @Override
    public List<String> getFileList(){
        List<String> fileList = jdbcTemplate.query("Select * FROM file_links", (ResultSet rs,int rowNum)->{
            String filePath = rs.getString("file_link");
            return filePath;
        });
        return new ArrayList<>(fileList);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @PostConstruct
    public void postConstructIdProvider(){
        logger.info("postConstruct annotated method BookRepository");
    }

    @PreDestroy
    public void preDestroyIdProvider(){
        logger.info("preDestroyConstruct annotated method BookRepository");
    }

}
