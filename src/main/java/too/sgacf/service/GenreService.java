package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.GenreDto;
import too.sgacf.model.GenreModel;
import too.sgacf.repository.IGenreRepository;

@Service
public class GenreService {

    @Autowired
    private IGenreRepository repository;

    public List<GenreDto> listAllGeneros(){
        List<GenreModel> genres = repository.findAll(Sort.by("id").ascending());
        return genres.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<GenreDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<GenreModel> genres = this.repository.findByStatus(status);
        return genres.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<GenreDto> listByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
    
        List<GenreModel> genres = this.repository.findByQuery(query);
        return genres.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public GenreDto findById(Long id) {
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }
    
    public GenreModel save(GenreDto dto) {
        Optional<GenreModel> genre = this.repository.findByName(dto.getName());
        if (genre.isPresent() && (dto.getId()==null || !genre.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El género ya existe.");
        }

        GenreModel genreModel;

        if (dto.getId() == null) {
            genreModel = new GenreModel();
            genreModel.setStatus(dto.isStatus());
        }
        else {
            Optional<GenreModel> genreOptional = this.repository.findById(dto.getId());
            if (genreOptional.isPresent()) {
                genreModel = genreOptional.get();
                if (genreModel.getName().equals(dto.getName()) && genreModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new EntityNotFoundException();
            }
        }

        genreModel.setName(dto.getName());

        return this.repository.save(genreModel);
    }

    
    
    public void delete(Long id) {
        Optional<GenreModel> genre = this.repository.findById(id);
        if (genre.isPresent()) {
            GenreModel genreModel = genre.get();
            genreModel.setStatus(false);
            this.repository.save(genreModel);
        }
    }

    public GenreDto convertToDto(GenreModel genre){
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setStatus(genre.isStatus());
        return dto;
    }
}
