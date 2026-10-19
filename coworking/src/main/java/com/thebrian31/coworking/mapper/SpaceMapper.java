package com.thebrian31.coworking.mapper;

import com.thebrian31.coworking.dto.SpaceRequest;
import com.thebrian31.coworking.dto.SpaceResponse;
import com.thebrian31.coworking.dto.SpaceUpdateRequest;
import com.thebrian31.coworking.entity.Space;
import com.thebrian31.coworking.exception.InvalidSpaceUpdateException;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public Space toEntity(SpaceRequest request) {
        Space space = new Space();
        apply(space, request);
        return space;
    }

    public void apply(Space space, SpaceRequest request) {
        space.setName(request.name().trim());
        space.setType(request.type());
        space.setCapacity(request.capacity());
        space.setLocation(request.location().trim());
        space.setHourlyRate(request.hourlyRate());
        space.setOutOfService(request.outOfService());
    }

    public void applyUpdate(Space space, SpaceUpdateRequest request) {
        if (request.name() == null
                && request.type() == null
                && request.capacity() == null
                && request.location() == null
                && request.hourlyRate() == null
                && request.outOfService() == null) {
            throw new InvalidSpaceUpdateException("Indica al menos un campo para actualizar");
        }
        if (request.name() != null) {
            space.setName(requiredText(request.name(), "name"));
        }
        if (request.type() != null) {
            space.setType(request.type());
        }

        if (request.capacity() != null) {
            space.setCapacity(request.capacity());
        }
        if (request.location() != null) {
            space.setLocation(requiredText(request.location(), "location"));
        }
        if (request.hourlyRate() != null) {
            space.setHourlyRate(request.hourlyRate());
        }
        if (request.outOfService() != null) {
            space.setOutOfService(request.outOfService());
        }
    }

    private String requiredText(String value, String field) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidSpaceUpdateException(field
                    + " no puede estar vacío");
        }
        return trimmed;
    }

    public SpaceResponse toResponse(Space space) {
        return new SpaceResponse(
                space.getId(),
                space.getName(),
                space.getType(),
                space.getCapacity(),
                space.getLocation(),
                space.getHourlyRate(),
                space.isOutOfService(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }
}
