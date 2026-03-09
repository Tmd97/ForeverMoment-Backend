package com.forvmom.core.services;

import com.forvmom.common.dto.request.*;
import com.forvmom.common.dto.response.CategoryLocationResponseDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.ExperienceBeanMapper;
import com.forvmom.core.mapper.LocationBeanMapper;
import com.forvmom.data.dao.*;
import com.forvmom.data.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private PincodeDao pincodeDao;

    @Autowired
    private ExperienceLocationMapperDao locationMapperDao;

    @Autowired
    private ExperienceDao experienceDao;

    @Autowired
    private CatalogCacheService catalogCacheService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryLocationMapperDao categoryLocationMapperDao;

    @Override
    @Transactional
    public LocationResponseDto createLocation(LocationRequestDto requestDto) {
        if (locationDao.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Location with name '" + requestDto.getName() + "' already exists");
        }

        Location location = new Location();
        LocationBeanMapper.mapDtoToEntity(requestDto, location);
        Location saved = locationDao.save(location);
        return LocationBeanMapper.mapEntityToDto(saved);
    }

    @Override
    @Transactional
    public LocationResponseDto updateLocation(Long id, LocationRequestDto requestDto) {
        Location existing = locationDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Location not found with id " + id);
        }

        if (!existing.getName().equals(requestDto.getName()) && locationDao.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Location with name '" + requestDto.getName() + "' already exists");
        }

        LocationBeanMapper.mapDtoToEntity(requestDto, existing);
        Location updated = locationDao.update(existing);
        return LocationBeanMapper.mapEntityToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponseDto getById(Long id) {
        Location location = locationDao.findByIdWithPincodes(id);
        if (location == null) {
            throw new ResourceNotFoundException("Location not found with id " + id);
        }
        return LocationBeanMapper.mapEntityToDto(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDto> getAll() {
        List<Location> locations = locationDao.findAll();
        if (locations == null || locations.isEmpty())
            return new ArrayList<>();
        return locations.stream().map(LocationBeanMapper::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDto> getAllActive() {
        List<Location> locations = locationDao.findAllActive();
        if (locations == null || locations.isEmpty())
            return new ArrayList<>();
        return locations.stream().map(LocationBeanMapper::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDto> getByCity(String city) {
        List<Location> locations = locationDao.findByCity(city);
        if (locations == null || locations.isEmpty()) {
            throw new ResourceNotFoundException("No locations found for city: " + city);
        }
        return locations.stream().map(LocationBeanMapper::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteLocation(Long id) {
        Location existing = locationDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Location not found with id " + id);
        }
        locationDao.delete(existing);
        return true;
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Location existing = locationDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Location not found with id " + id);
        }
        existing.setActive(!existing.isActive());
        locationDao.update(existing);
    }

    // ─── Pincode operations ───────────────────────────────────────────────────

    @Override
    @Transactional
    public PincodeResponseDto addPincode(PincodeRequestDto requestDto) {
        Location location = locationDao.findById(requestDto.getLocationId());
        if (location == null) {
            throw new ResourceNotFoundException("Location not found with id " + requestDto.getLocationId());
        }

        if (pincodeDao.existsByPincodeCodeAndLocationId(requestDto.getPincodeCode(), requestDto.getLocationId())) {
            throw new IllegalArgumentException(
                    "Pincode '" + requestDto.getPincodeCode() + "' already exists for this location");
        }

        Pincode pincode = new Pincode();
        pincode.setName(requestDto.getName());
        pincode.setPincodeCode(requestDto.getPincodeCode());
        pincode.setAreaName(requestDto.getAreaName());
        pincode.setLatitude(requestDto.getLatitude());
        pincode.setLongitude(requestDto.getLongitude());
        if (requestDto.getIsActive() != null)
            pincode.setActive(requestDto.getIsActive());
        pincode.setLocation(location);

        Pincode saved = pincodeDao.save(pincode);
        return LocationBeanMapper.mapPincodeToDto(saved, true);
    }

    @Override
    @Transactional
    public PincodeResponseDto updatePincode(Long pincodeId, PincodeRequestDto requestDto) {
        Pincode existing = pincodeDao.findByIdWithLocation(pincodeId);
        if (existing == null) {
            throw new ResourceNotFoundException("Pincode not found with id " + pincodeId);
        }

        // If pincode code is changing, check uniqueness within the same location
        if (!existing.getPincodeCode().equals(requestDto.getPincodeCode()) &&
                pincodeDao.existsByPincodeCodeAndLocationId(requestDto.getPincodeCode(),
                        existing.getLocation().getId())) {
            throw new IllegalArgumentException(
                    "Pincode '" + requestDto.getPincodeCode() + "' already exists for this location");
        }

        existing.setName(requestDto.getName());
        existing.setPincodeCode(requestDto.getPincodeCode());
        existing.setAreaName(requestDto.getAreaName());
        existing.setLatitude(requestDto.getLatitude());
        existing.setLongitude(requestDto.getLongitude());
        if (requestDto.getIsActive() != null)
            existing.setActive(requestDto.getIsActive());

        Pincode updated = pincodeDao.update(existing);
        return LocationBeanMapper.mapPincodeToDto(updated, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PincodeResponseDto> getPincodesByLocation(Long locationId) {
        Location location = locationDao.findById(locationId);
        if (location == null) {
            throw new ResourceNotFoundException("Location not found with id " + locationId);
        }
        List<Pincode> pincodes = pincodeDao.findByLocationIdWithLocation(locationId);
        return pincodes.stream()
                .map(p -> LocationBeanMapper.mapPincodeToDto(p, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PincodeResponseDto checkPincode(String pincodeCode) {
        Pincode pincode = pincodeDao.findByPincodeCode(pincodeCode);
        if (pincode == null) {
            throw new ResourceNotFoundException("Pincode '" + pincodeCode + "' is not serviceable");
        }
        return LocationBeanMapper.mapPincodeToDto(pincode, true);
    }

    @Override
    @Transactional
    public boolean deletePincode(Long pincodeId) {
        Pincode existing = pincodeDao.findById(pincodeId);
        if (existing == null) {
            throw new ResourceNotFoundException("Pincode not found with id " + pincodeId);
        }
        pincodeDao.delete(existing);
        return true;
    }

    // ── Experience Association ────────────────────────────────────────────────

    @Override
    @Transactional
    public ExperienceLocationResponseDto attachToExperience(Long locationId, Long experienceId,
            ExperienceLocationAttachRequestDto requestDto) {

        if (locationMapperDao.existsByExperienceIdAndLocationId(experienceId, locationId)) {
            throw new IllegalStateException(
                    "Location " + locationId + " is already attached to experience " + experienceId);
        }

        Location location = locationDao.findById(locationId);
        if (location == null)
            throw new ResourceNotFoundException("Location not found: " + locationId);

        Experience experience = experienceDao.findById(experienceId);
        if (experience == null)
            throw new ResourceNotFoundException("Experience not found: " + experienceId);

        ExperienceLocationMapper mapper = new ExperienceLocationMapper();
        mapper.setLocation(location);
        mapper.setPriceOverride(requestDto.getPriceOverride());
        mapper.setValidFrom(requestDto.getValidFrom());
        mapper.setValidTo(requestDto.getValidTo());
        mapper.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true);

        // Bidirectional helper wires experience → mapper back-reference
        experience.addLocationMapper(mapper);

        ExperienceLocationMapper savedMapper = locationMapperDao.save(mapper);
        catalogCacheService.warmLocationCache(savedMapper);

        return ExperienceBeanMapper.mapLocationMapperToDto(savedMapper);
    }

    @Override
    @Transactional
    public void detachFromExperience(Long locationId, Long experienceId) {
        ExperienceLocationMapper mapper = locationMapperDao.findByExperienceIdAndLocationId(experienceId, locationId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "Location " + locationId + " is not attached to experience " + experienceId);
        }
        locationMapperDao.delete(mapper);
        catalogCacheService.evictLocation(experienceId, locationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceLocationResponseDto> getExperiencesForLocation(Long locationId) {
        // Re-uses DAO query keyed by experienceId — but we want by locationId.
        // The ExperienceLocationMapperDao.findByExperienceId covers experience-side
        // listing.
        // For location-side we query all mappers and filter; a dedicated DAO method can
        // be added later.
        List<ExperienceLocationMapper> mappers = locationMapperDao.findByLocationId(locationId);
        return ExperienceBeanMapper.mapLocationMappers(new ArrayList<>(mappers));
    }

    @Override
    @Transactional
    public ExperienceLocationResponseDto updateExperienceAttachment(Long locationId, Long experienceId,
            ExperienceLocationAttachRequestDto requestDto) {
        ExperienceLocationMapper mapper = locationMapperDao.findByExperienceIdAndLocationId(experienceId, locationId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "Location " + locationId + " is not attached to experience " + experienceId);
        }
        mapper.setPriceOverride(requestDto.getPriceOverride());
        mapper.setValidFrom(requestDto.getValidFrom());
        mapper.setValidTo(requestDto.getValidTo());
        if (requestDto.getIsActive() != null)
            mapper.setIsActive(requestDto.getIsActive());

        ExperienceLocationMapper updated = locationMapperDao.update(mapper);
        catalogCacheService.warmLocationCache(updated);

        return ExperienceBeanMapper.mapLocationMapperToDto(updated);
    }

    @Override
    @Transactional
    public void toggleExperienceAttachmentActive(Long mapperId) {
        ExperienceLocationMapper mapper = locationMapperDao.findById(mapperId);
        if (mapper == null)
            throw new ResourceNotFoundException("Location mapping not found: " + mapperId);
        mapper.setIsActive(!Boolean.TRUE.equals(mapper.getIsActive()));
        ExperienceLocationMapper updated = locationMapperDao.update(mapper);
        catalogCacheService.warmLocationCache(updated);
    }

    @Override
    @Transactional
    public CategoryLocationResponseDto attachCategoryToLocation(Long locationId, Long categoryId,
                                                                CategoryLocationAttachRequestDto requestDto) {
        if (categoryLocationMapperDao.existsByCategoryIdAndLocationId(categoryId, locationId)) {
            throw new IllegalStateException("Category " + categoryId + " is already attached to location " + locationId);
        }

        Location location = locationDao.findById(locationId);
        if (location == null) throw new ResourceNotFoundException("Location not found: " + locationId);

        Category category = categoryDao.findById(categoryId);
        if (category == null) throw new ResourceNotFoundException("Category not found: " + categoryId);

        CategoryLocationMapper mapper = new CategoryLocationMapper();
        mapper.setLocation(location);
        mapper.setCategory(category);
        mapper.setDisplayOrder(requestDto.getDisplayOrder() != null ? requestDto.getDisplayOrder() : 0);
        mapper.setActive(requestDto.getActive() != null ? requestDto.getActive() : true);

        CategoryLocationMapper saved = categoryLocationMapperDao.save(mapper);
        return mapCategoryLocationToDto(saved);
    }

    @Override
    @Transactional
    public void detachCategoryFromLocation(Long locationId, Long categoryId) {
        CategoryLocationMapper mapper = categoryLocationMapperDao.findByCategoryIdAndLocationId(categoryId, locationId);
        if (mapper == null) {
            throw new ResourceNotFoundException("Category " + categoryId + " is not attached to location " + locationId);
        }
        categoryLocationMapperDao.delete(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryLocationResponseDto> getCategoriesForLocation(Long locationId) {
        List<CategoryLocationMapper> mappers = categoryLocationMapperDao.findByLocationId(locationId);
        return mappers.stream().map(this::mapCategoryLocationToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryLocationResponseDto updateCategoryAttachment(Long locationId, Long categoryId,
                                                                CategoryLocationAttachRequestDto requestDto) {
        CategoryLocationMapper mapper = categoryLocationMapperDao.findByCategoryIdAndLocationId(categoryId, locationId);
        if (mapper == null) {
            throw new ResourceNotFoundException("Category " + categoryId + " is not attached to location " + locationId);
        }
        if (requestDto.getDisplayOrder() != null) {
            mapper.setDisplayOrder(requestDto.getDisplayOrder());
        }
        if (requestDto.getActive() != null) {
            mapper.setActive(requestDto.getActive());
        }
        CategoryLocationMapper updated = categoryLocationMapperDao.update(mapper);
        return mapCategoryLocationToDto(updated);
    }

    @Override
    @Transactional
    public void toggleCategoryAttachmentActive(Long mapperId) {
        CategoryLocationMapper mapper = categoryLocationMapperDao.findById(mapperId);
        if (mapper == null) throw new ResourceNotFoundException("Category-Location mapping not found: " + mapperId);
        mapper.setActive(!Boolean.TRUE.equals(mapper.getActive()));
        categoryLocationMapperDao.update(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryByLocationDto> getActiveCategoriesByLocation(Long locationId) {
        List<CategoryLocationMapper> mappers = categoryLocationMapperDao.findActiveByLocationId(locationId);
        return mappers.stream()
                .map(m -> new CategoryByLocationDto(
                        m.getCategory().getId(),
                        m.getCategory().getName(),
                        m.getCategory().getSlug(),
                        m.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    private CategoryLocationResponseDto mapCategoryLocationToDto(CategoryLocationMapper entity) {
        CategoryLocationResponseDto dto = new CategoryLocationResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategory().getId());
        dto.setCategoryName(entity.getCategory().getName());
        dto.setCategorySlug(entity.getCategory().getSlug());
        dto.setLocationId(entity.getLocation().getId());
        dto.setLocationName(entity.getLocation().getName());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setActive(entity.getActive());
        return dto;
    }
}
