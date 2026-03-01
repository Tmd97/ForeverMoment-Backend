package com.forvmom.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forvmom.common.dto.snapshot.*;
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

    public void evictLocation(Long expId, Long locationId) {
        redis.delete("exp:" + expId + ":loc:" + locationId);
    }

    // ── Slot mapper ────────────────────────────────────────────────────────────

    public void warmSlotCache(ExperienceTimeSlotMapper esm) {
        String key = "slot:" + esm.getId();
        TimeSlot ts = esm.getTimeSlot();
        SlotSnapshot snapshot = new SlotSnapshot(
                esm.getId(),
                esm.getExperienceLocation().getExperience().getId(),
                esm.getExperienceLocation().getLocation().getId(),
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

    public void evictAddon(Long addonMapperId) {
        redis.delete("addon:" + addonMapperId);
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

    // ── Snapshot classes are now extracted to com.forvmom.core.dto.snapshot ────
}
