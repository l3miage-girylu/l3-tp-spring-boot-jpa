package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class BooksController {

    private final BookService bookService;
    private final BooksMapper booksMapper;
    private final AuthorService authorService;
    @Autowired
    public BooksController(BookService bookService, BooksMapper booksMapper,  AuthorService authorService) {
       this.bookService = bookService;
        this.booksMapper = booksMapper;
        this.authorService = authorService;
    }

    @GetMapping("/books/v1")
    public Collection<BookDTO> books(@RequestParam("q") String query) {
        return null;
    }

    @GetMapping("/books/{id}")
    public BookDTO book(@PathVariable("id") Long id){
        try{
            var li = this.bookService.get(id);
            return booksMapper.entityToDTO(li);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/authors/{authorId}/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO newBook(@PathVariable("authorId") Long authorId, @RequestBody BookDTO book) {
        try {
            authorService.get(authorId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur non trouvé", e);
        }
        try {
            if(book.title().replaceAll("\\s", "").equals("") || Long.toString(book.isbn()).length() < 10 || Long.toString(book.year()).length() > 4){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            var livre = this.booksMapper.dtoToEntity(book);
            this.bookService.save(authorId, livre);
            return this.booksMapper.entityToDTO(livre);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public BookDTO updateBook(Long authorId, BookDTO book) {
        // attention BookDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise

        if(authorId == book.id()){
            try{
                var livre = this.bookService.get(book.id());
                livre.setId(authorId);
                return this.booksMapper.entityToDTO(livre);

            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.RESET_CONTENT);
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        try{
            Book livre = this.bookService.get(id);
            this.bookService.delete(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void addAuthor(Long authorId, AuthorDTO author) {

    }
}
