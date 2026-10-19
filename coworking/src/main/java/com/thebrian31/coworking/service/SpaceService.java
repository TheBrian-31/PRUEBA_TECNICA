package com.thebrian31.coworking.service;

import com.thebrian31.coworking.dto.SpaceRequest;
import com.thebrian31.coworking.dto.SpaceResponse;
import com.thebrian31.coworking.dto.SpaceUpdateRequest;
import com.thebrian31.coworking.entity.Space;
import com.thebrian31.coworking.exception.SpaceInUseException;
import com.thebrian31.coworking.exception.SpaceNotFoundException;
import com.thebrian31.coworking.mapper.SpaceMapper;
import com.thebrian31.coworking.repository.SpaceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;

    public SpaceService(SpaceRepository spaceRepository, SpaceMapper spaceMapper) {
        this.spaceRepository = spaceRepository;
        this.spaceMapper = spaceMapper;
    }

    @Transactional(readOnly = true)
    public Page<SpaceResponse> findAll(Pageable pageable) {
        return spaceRepository.findAll(pageable).map(spaceMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SpaceResponse findById(Long id) {
        return spaceMapper.toResponse(getSpace(id));
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public SpaceResponse create(SpaceRequest request) {
        Space space = spaceMapper.toEntity(request);
        return spaceMapper.toResponse(spaceRepository.save(space));
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public SpaceResponse update(Long id, SpaceUpdateRequest request) {
        Space space = getSpace(id);
        spaceMapper.applyUpdate(space, request);
        return spaceMapper.toResponse(space);
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public void delete(Long id) {
        Space space = getSpace(id);
        try {
            spaceRepository.delete(space);
            spaceRepository.flush();
        } catch (DataIntegrityViolationException exception) {

            throw new SpaceInUseException(id);
        }
    }

    private Space getSpace(Long id) {
        return lodSpace(id);
    }

    private Space lodSpace(Long id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> new SpaceNotFoundException(id));
    }
}
