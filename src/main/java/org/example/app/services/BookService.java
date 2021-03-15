package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.example.web.dto.BookToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

@Service
public class BookService {

    private Logger logger = Logger.getLogger(BookService.class);
    private final ProjectRepository<Book> bookRepo;

    @Autowired
    public BookService(ProjectRepository<Book> bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks() {
        return bookRepo.retreiveAll();
    }

    public List<Book> getFilterBooks(String bookAuthorToFilter, String bookTitleToFilter, String bookSizeToFilter){
        List<Book> resultBookList = new ArrayList<>();

        boolean checkAuthor;
        boolean checkTitle;
        boolean checkSize;

        logger.info("try got book shelf with filter: " + "; " + bookAuthorToFilter + "; "
                + bookTitleToFilter + "; " +bookSizeToFilter);


        for (Book tmpBook :
                bookRepo.retreiveAll()) {

            checkAuthor = checkParamString(tmpBook.getAuthor(), bookAuthorToFilter, true);
            checkTitle = checkParamString(tmpBook.getTitle(), bookTitleToFilter, true);
            checkSize = checkParamString(tmpBook.getSize().toString(), bookSizeToFilter, true);

            if (checkAuthor && checkTitle && checkSize){
                resultBookList.add(tmpBook);
            }
        }

        return resultBookList;
    }

    public void saveBook(Book book) {
        if(!book.getAuthor().isEmpty() || !(book.getSize() == null) || !book.getTitle().isEmpty()) {
            bookRepo.store(book);
            logger.info("current book save: " + book.toString());
        }
        else {
            logger.info("trying to save an empty book");
        }
    }


    public boolean removeBookByFields(BookToRemove bookToRemove) {

        String bookIdToRemove = String.valueOf(bookToRemove.getId());
        String bookAuthorToRemove = bookToRemove.getAuthor();
        String bookTitleToRemove = bookToRemove.getTitle();
        String bookSizeToRemove = String.valueOf(bookToRemove.getSize());

        logger.info(String.valueOf(bookToRemove.getId())=="");
        logger.info("try delete book" + bookIdToRemove + bookAuthorToRemove + bookTitleToRemove + bookSizeToRemove);

        boolean result = false;

        if (bookIdToRemove.isEmpty() &&
                bookAuthorToRemove.isEmpty() &&
                bookTitleToRemove.isEmpty() &&
                bookSizeToRemove.equals("null"))
        {
            logger.info("all field is empty");
            return false;
        }

        boolean checkId;
        boolean checkAuthor;
        boolean checkTitle;
        boolean checkSize;

        for (Book tmpBook :
                bookRepo.retreiveAll()) {

            checkId = checkParamString(String.valueOf(tmpBook.getId()), bookIdToRemove, false);
            checkAuthor = checkParamString(tmpBook.getAuthor(), bookAuthorToRemove, false);
            checkTitle = checkParamString(tmpBook.getTitle(), bookTitleToRemove, false);
            checkSize = checkParamString(tmpBook.getSize().toString(), bookSizeToRemove, false);

            logger.info("check status: " + checkId + checkAuthor + checkTitle + checkSize);
            if (checkId && checkAuthor && checkTitle && checkSize){
                result = bookRepo.removeItemById(tmpBook.getId());
            }
        }

        return result;
    }


    public List<String> getFileList(){
        return bookRepo.getFileList();
    }

    public boolean addFileListItem(String fileLink){
        bookRepo.addFileListItem(fileLink);
        return true;
    }




    public boolean checkParamString (String bookParam, String frontParam, boolean isFilter){
        boolean result = false;
        if (bookParam == null || frontParam == null || frontParam.equals("") || frontParam.equals("null") ) {
            result = true;
        }
        else {
            if (isFilter) {
                if (bookParam.contains(frontParam)) {
                    result = true;
                } else {
                    try{
                        if (bookParam.matches(frontParam)){
                            result = true;
                        }
                    }
                    catch(PatternSyntaxException e){
                        result = false;
                    }
                }
            }
            else{
                if (bookParam.equals(frontParam)) {
                    result = true;
                } else{
                    try{
                        if (bookParam.matches(frontParam)){
                            result = true;
                        }
                    }
                    catch(PatternSyntaxException e){
                        result = false;
                    }
                }
            }
        }
        return result;
    }






    @PostConstruct
    public void postConstructIdProvider(){
        logger.info("postConstruct annotated method BookService");
    }

    @PreDestroy
    public void preDestroyIdProvider(){
        logger.info("preDestroyConstruct annotated method BookService");
    }


}
