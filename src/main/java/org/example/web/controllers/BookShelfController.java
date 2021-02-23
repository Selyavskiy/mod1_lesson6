package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping(value = "/books")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info("got book shelf");
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @GetMapping("/filter")
    public String filterBooks(Model model,    @RequestParam(value="bookAuthorToFilter") String bookAuthorToFilter,
                                              @RequestParam(value="bookTitleToFilter") String bookTitleToFilter,
                                              @RequestParam(value="bookSizeToFilter") String bookSizeToFilter) {


        logger.info("try got book shelf with filter: " + "; " + bookAuthorToFilter + "; "
                + bookTitleToFilter + "; " +bookSizeToFilter);

        List<Book> modelBookList = bookService.getFilterBooks(bookAuthorToFilter, bookTitleToFilter, bookSizeToFilter);
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", modelBookList);
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(Book book) {
        bookService.saveBook(book);
        return "redirect:/books/shelf";
    }

    @PostMapping("/remove")
    public String removeBook(@RequestParam(value = "bookIdToRemove") Integer bookIdToRemove,
                             @RequestParam(value = "bookAuthorToRemove") String bookAuthorToRemove,
                             @RequestParam(value = "bookTitleToRemove") String bookTitleToRemove,
                             @RequestParam(value = "bookSizeToRemove") String bookSizeToRemove) {


        logger.info("try remove book: " + bookIdToRemove + ";"
                + bookAuthorToRemove + ";" + bookTitleToRemove + ";" + bookSizeToRemove + ";");

        if(bookService.removeBookByFields(bookIdToRemove, bookAuthorToRemove, bookTitleToRemove,bookSizeToRemove)){
            logger.info("Remove book was successfully");
        }
        else{
            logger.info("Remove book was not successfully");
        }
            return "redirect:/books/shelf";
    }

}
