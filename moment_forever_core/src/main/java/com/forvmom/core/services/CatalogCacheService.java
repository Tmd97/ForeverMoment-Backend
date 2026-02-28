package com.forvmom.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forvmom.data.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Writes catalog snapshots to Redis after every admin write operation.
 * Called inline by admin services (ExperienceService,
 * ExperienceTimeSlotMapperService, etc.)
 * so the async enrichment task always finds fresh data in Redis.
 *
 * <p>
 * Key pattern:
 * <ul>
 * <li>{@code exp:{expId}} — experience snapshot</li>
 * <li>{@code exp:{expId}:loc:{locationId}} — location mapper snapshot</li>
 * <li>{@code slot:{slotMapperId}} — time-slot mapper snapshot</li>
 * <li>{@code addon:{addonMapperId}} — addon mapper snapshot</li>
 * <li>{@code user:{userId}} — user snapshot (1h TTL)</li>
 * </ul>
 */
@Service
public class CatalogCacheService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogCacheService.class);

    private static final long EXP_TTL_HOURS = 24;
    private static final long USER_TTL_HOURS = 1;

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public CatalogCacheService(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    // ── Experience ─────────────────────────────────────────────────────────────

    public void warmExperienceCache(Experience exp) {
        String key = "exp:" + exp.getId();
        ExperienceSnapshot snapshot = new ExperienceSnapshot(
                exp.getId(), exp.getName(), exp.getSlug(), exp.getBasePrice());
        setJson(key, snapshot, EXP_TTL_HOURS);
        logger.debug("Warmed Redis key={}", key);
    }

    public ExperienceSnapshot getExperienceSnapshot(Long expId) {
        return getJson("exp:" + expId, ExperienceSnapshot.class);
    }

    public void evictExperience(Long expId) {
        redis.delete("exp:" + expId);
    }

    // ── Location mapper ────────────────────────────────────────────────────────

    public void warmLocationCache(ExperienceLocationMapper elm) {
        Long expId = elm.getExperience().getId();
        Long locId = elm.getLocation().getId();
        String key = "exp:" + expId + ":loc:" + locId;
        LocationSnapshot snapshot = new LocationSnapshot(
                locId, elm.getLocation().getName(), elm.getPriceOverride());
        setJson(key, snapshot, EXP_TTL_HOURS);
        logger.debug("Warmed Redis key={}", key);
    }

    public LocationSnapshot getLocationSnapshot(Long expId, Long locationId) {
        return getJson("exp:" + expId + ":loc:" + locationId, LocationSnapshot.class);
    }

    // ── Slot mapper ────────────────────────────────────────────────────────────

    public void warmSlotCache(ExperienceTimeSlotMapper esm) {
        String key = "slot:" + esm.getId();
        TimeSlot ts = esm.getTimeSlot();
        SlotSnapshot snapshot = new SlotSnapshot(
                esm.getId(),
                ts.getId(),
                ts.getLabel(),
                ts.getStartTime() != null ? ts.getStartTime().toString() : null,
                ts.getEndTime() != null ? ts.getEndTime().toString() : null,
                esm.getPriceOverride(),
                esm.getMaxCapacity());
        setJson(key, snapshot, EXP_TTL_HOURS);
        logger.debug("Warmed Redis key={}", key);
    }

    public SlotSnapshot getSlotSnapshot(Long slotMapperId) {
        return getJson("slot:" + slotMapperId, SlotSnapshot.class);
    }

    public void evictSlot(Long slotMapperId) {
        redis.delete("slot:" + slotMapperId);
    }

    // ── Addon mapper ───────────────────────────────────────────────────────────

    public void warmAddonCache(ExperienceAddonMapper eam) {
        String key = "addon:" + eam.getId();
        AddonSnapshot snapshot = new AddonSnapshot(
                eam.getId(),
                eam.getAddon().getName(),
                eam.effectivePrice(),
                Boolean.TRUE.equals(eam.getIsFree()));
        setJson(key, snapshot, EXP_TTL_HOURS);
        logger.debug("Warmed Redis key={}", key);
    }

    public AddonSnapshot getAddonSnapshot(Long addonMapperId) {
        return getJson("addon:" + addonMapperId, AddonSnapshot.class);
    }

    // ── User ───────────────────────────────────────────────────────────────────

    public void warmUserCache(ApplicationUser user) {
        String key = "user:" + user.getId();
        UserSnapshot snapshot = new UserSnapshot(
                user.getId(), user.getEmail(), user.getFullName());
        setJson(key, snapshot, USER_TTL_HOURS);
        logger.debug("Warmed Redis key={}", key);
    }

    public UserSnapshot getUserSnapshot(Long userId) {
        return getJson("user:" + userId, UserSnapshot.class);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void setJson(String key, Object value, long ttlHours) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redis.opsForValue().set(key, json, ttlHours, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize cache value for key={}", key, e);
        }
    }

    private <T> T getJson(String key, Class<T> type) {
        String json = redis.opsForValue().get(key);
        if (json == null)
            return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize cache value for key={}", key, e);
            return null;
        }
    }

    // ── Snapshot inner classes (plain POJOs — serialized as JSON) ──────────────

    public static class ExperienceSnapshot {
        private Long id;
        private String name;
        private String slug;
        private java.math.BigDecimal basePrice;

        public ExperienceSnapshot() {
        }

        public ExperienceSnapshot(Long id, String name, String slug, java.math.BigDecimal basePrice) {
            this.id = id;
            this.name = name;
            this.slug = slug;
            this.basePrice = basePrice;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public java.math.BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(java.math.BigDecimal basePrice) {
            this.basePrice = basePrice;
        }
    }

    public static class LocationSnapshot {
        private Long locationId;
        private String locationName;
        private java.math.BigDecimal priceOverride;

        public LocationSnapshot() {
        }

        public LocationSnapshot(Long locationId, String locationName, java.math.BigDecimal priceOverride) {
            this.locationId = locationId;
            this.locationName = locationName;
            this.priceOverride = priceOverride;
        }

        public Long getLocationId() {
            return locationId;
        }

        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public java.math.BigDecimal getPriceOverride() {
            return priceOverride;
        }

        public void setPriceOverride(java.math.BigDecimal priceOverride) {
            this.priceOverride = priceOverride;
        }
    }

    public static class SlotSnapshot {
        private Long slotMapperId;
        private Long timeSlotId;
        private String label;
        private String startTime;
        private String endTime;
        private java.math.BigDecimal priceOverride;
        private Integer maxCapacity;

        public SlotSnapshot() {
        }

        public SlotSnapshot(Long slotMapperId, Long timeSlotId, String label,
                String startTime, String endTime,
                java.math.BigDecimal priceOverride, Integer maxCapacity) {
            this.slotMapperId = slotMapperId;
            this.timeSlotId = timeSlotId;
            this.label = label;
            this.startTime = startTime;
            this.endTime = endTime;
            this.priceOverride = priceOverride;
            this.maxCapacity = maxCapacity;
        }

        public Long getSlotMapperId() {
            return slotMapperId;
        }

        public void setSlotMapperId(Long slotMapperId) {
            this.slotMapperId = slotMapperId;
        }

        public Long getTimeSlotId() {
            return timeSlotId;
        }

        public void setTimeSlotId(Long timeSlotId) {
            this.timeSlotId = timeSlotId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public java.math.BigDecimal getPriceOverride() {
            return priceOverride;
        }

        public void setPriceOverride(java.math.BigDecimal priceOverride) {
            this.priceOverride = priceOverride;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
        }
    }

    public static class AddonSnapshot {
        private Long addonMapperId;
        private String addonName;
        private java.math.BigDecimal effectivePrice;
        private boolean free;

        public AddonSnapshot() {
        }

        public AddonSnapshot(Long addonMapperId, String addonName,
                java.math.BigDecimal effectivePrice, boolean free) {
            this.addonMapperId = addonMapperId;
            this.addonName = addonName;
            this.effectivePrice = effectivePrice;
            this.free = free;
        }

        public Long getAddonMapperId() {
            return addonMapperId;
        }

        public void setAddonMapperId(Long addonMapperId) {
            this.addonMapperId = addonMapperId;
        }

        public String getAddonName() {
            return addonName;
        }

        public void setAddonName(String addonName) {
            this.addonName = addonName;
        }

        public java.math.BigDecimal getEffectivePrice() {
            return effectivePrice;
        }

        public void setEffectivePrice(java.math.BigDecimal effectivePrice) {
            this.effectivePrice = effectivePrice;
        }

        public boolean isFree() {
            return free;
        }

        public void setFree(boolean free) {
            this.free = free;
        }
    }

    public static class UserSnapshot {
        private Long userId;
        private String email;
        private String fullName;

        public UserSnapshot() {
        }

        public UserSnapshot(Long userId, String email, String fullName) {
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
