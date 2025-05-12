package org.sunday.projectpop.service.ongoingproject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.model.repository.OnGoingProjectRepository;

import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OnGoingProjectServiceImpl implements OnGoingProjectService {

    private final OnGoingProjectRepository onGoingProjectRepository;

    @Override
    public Optional<OnGoingProject> findById(String id) {
        return onGoingProjectRepository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return onGoingProjectRepository.existsById(id);
    }

    @Override
    public List<OnGoingProject> findAll() {
        return onGoingProjectRepository.findAll();
    }
}
