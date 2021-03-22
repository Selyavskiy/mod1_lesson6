package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookToFilter;
import org.example.web.dto.BookToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping(value = "/books")
@Scope("singleton")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookToRemove", new BookToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        model.addAttribute("bookToFilter", new BookToFilter());
        model.addAttribute("fileList", bookService.getFileList());
        return "book_shelf";
    }

    @GetMapping("/filter")
    public String filterBooks(@Valid BookToFilter bookToFilter, BindingResult bindingResult, Model model) {


        logger.info("try got book shelf with filter: " + "; " + bookToFilter);

        if(bindingResult.getFieldErrorCount()==4){
            model.addAttribute("book", new Book());
            model.addAttribute("bookToFilter", bookToFilter);
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("bookToRemove", new BookToRemove());
            model.addAttribute("fileList", bookService.getFileList());
            return "book_shelf";
        }
        else {
            List<Book> modelBookList = bookService.getFilterBooks(bookToFilter.getAuthor(), bookToFilter.getTitle(), String.valueOf(bookToFilter.getSize()));
            model.addAttribute("book", new Book());
            model.addAttribute("bookToRemove", new BookToRemove());
            model.addAttribute("bookToFilter", new BookToFilter());
            model.addAttribute("bookList", modelBookList);
            model.addAttribute("fileList", bookService.getFileList());
            return "book_shelf";
        }
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("book", book);
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("bookToRemove", new BookToRemove());
            model.addAttribute("bookToFilter", new BookToFilter());
            model.addAttribute("fileList", bookService.getFileList());
            return "book_shelf";
        }
        else {
            bookService.saveBook(book);
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookToRemove bookToRemove, BindingResult bindingResult, Model model) {

        logger.info(bookToRemove);

        if(bindingResult.getFieldErrorCount()==4){
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("bookToRemove", bookToRemove);
            model.addAttribute("bookToFilter", new BookToFilter());
            model.addAttribute("fileList", bookService.getFileList());
            return "book_shelf";
        }
        else{
            bookService.removeBookByFields(bookToRemove);
            return "redirect:/books/shelf";
        }

    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file")MultipartFile file) throws Exception{
        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        //create dir
        String rootPath = System.getProperty("catalina.home"); // get server path
        logger.info("Server Path is: " + rootPath);
        File dir = new File(rootPath + File.separator + "external_uploads");
        if(!dir.exists()){
            dir.mkdirs();
        }

        //createFile

        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            bookService.addFileListItem(serverFile.getName());

        }
        catch (FileNotFoundException e){
            return "redirect:/books/shelf";
        }

        logger.info("new file saved at: " + serverFile.getAbsolutePath());
        return "redirect:/books/shelf";

    }


    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam (name = "fileName") String fileName) {

        logger.info("try download files: " + request + "; FileName: " + fileName);
        String rootPath = System.getProperty("catalina.home"); // get server path

        String dataDirectory = rootPath + File.separator + "external_uploads" + File.separator;
        Path file = Paths.get(dataDirectory, fileName);
        logger.info(dataDirectory);
        logger.info(file.getFileName());

        if (Files.exists(file))
        {
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }



}
