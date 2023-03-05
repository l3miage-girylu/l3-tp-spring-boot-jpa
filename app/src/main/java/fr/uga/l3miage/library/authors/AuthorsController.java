package fr.uga.l3miage.library.authors;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }

    @GetMapping("/authors/{id}")
    public AuthorDTO author(@PathVariable("id") Long id) {
        try{
            var auteur = this.authorService.get(id);
            return authorMapper.entityToDTO(auteur);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody @Valid AuthorDTO author) {
        try {
            Author auteur = authorMapper.dtoToEntity(author);
            Author autSave = authorService.save(auteur);
            return authorMapper.entityToDTO(autSave);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping ("/authors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO updateAuthor(@RequestBody AuthorDTO author, @PathVariable("id") Long id) {
        // attention AuthorDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        if(id == author.id()){
            try{
                var auteur = this.authorService.get(id);
                auteur.setFullName(author.fullName());
                return this.authorMapper.entityToDTO(auteur);

            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.RESET_CONTENT);
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable("id") Long id){
        // unimplemented... yet!
        try{
            var auteur = this.authorService.get(id);
            this.authorService.delete(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public Collection<BookDTO> books(Long authorId) {
        return Collections.emptyList();
    }

}
