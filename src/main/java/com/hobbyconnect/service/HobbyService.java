package com.hobbyconnect.service;

import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.repository.HobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class HobbyService {

    private final HobbyRepository hobbyRepository;

    @Autowired
    public HobbyService(HobbyRepository hobbyRepository) {
        this.hobbyRepository = hobbyRepository;
    }

    public List<Hobby> getAllHobbies() {
        return hobbyRepository.findAllByOrderByNameAsc();
    }

    public Optional<Hobby> getHobbyById(Long id) {
        return hobbyRepository.findById(id);
    }

    public List<Hobby> getHobbiesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return hobbyRepository.findByIdIn(ids);
    }

    public List<String> getAllCategories() {
        return hobbyRepository.findDistinctCategories();
    }

    public List<Hobby> getHobbiesByCategory(String category) {
        return hobbyRepository.findByCategory(category);
    }

    @Transactional
    public Hobby save(Hobby hobby) {
        return hobbyRepository.save(hobby);
    }

    public Optional<Hobby> findByName(String name) {
        return hobbyRepository.findByName(name);
    }
}
