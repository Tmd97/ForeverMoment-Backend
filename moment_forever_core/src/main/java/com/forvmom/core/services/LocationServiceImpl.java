package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceLocationAttachRequestDto;
import com.forvmom.common.dto.request.LocationRequestDto;
import com.forvmom.common.dto.request.PincodeRequestDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.ExperienceBeanMapper;
import com.forvmom.core.mapper.LocationBeanMapper;
import com.forvmom.data.dao.ExperienceDao;
import com.forvmom.data.dao.ExperienceLocationMapperDao;
import com.forvmom.data.dao.LocationDao;
import com.forvmom.data.dao.PincodeDao;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceLocationMapper;
import com.forvmom.data.entities.Location;
import com.forvmom.data.entities.Pincode;
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
}
