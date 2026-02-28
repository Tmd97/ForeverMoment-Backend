package com.forvmom.common.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for bulk-attaching multiple addons to an experience in one call.
 *
 * <p>
 * Each item in the list identifies an existing master {@code Addon} by its ID
 * and optionally overrides the price or marks it as free for this experience.
 */
public class BulkAttachAddonRequestDto {

    @NotEmpty(message = "Addon list must not be empty")
    @Valid
    private List<AddonAttachItem> items;

    public List<AddonAttachItem> getItems() {
        return items;
    }

    public void setItems(List<AddonAttachItem> items) {
        this.items = items;
    }

    // ── Inner class ───────────────────────────────────────────────────────────

    /**
     * A single addon attachment specification within a bulk request.
     */
    public static class AddonAttachItem {

        @NotNull(message = "addonId is required for each item")
        private Long addonId;

        /**
         * Optional per-experience price override.
         * {@code null} = use {@code Addon.basePrice}.
         */
        private BigDecimal priceOverride;

        /**
         * {@code true} = addon is complimentary for this experience,
         * overriding both {@code basePrice} and {@code priceOverride}.
         */
        private Boolean isFree = false;

        public Long getAddonId() {
            return addonId;
        }

        public void setAddonId(Long addonId) {
            this.addonId = addonId;
        }

        public BigDecimal getPriceOverride() {
            return priceOverride;
        }

        public void setPriceOverride(BigDecimal priceOverride) {
            this.priceOverride = priceOverride;
        }

        public Boolean getIsFree() {
            return isFree;
        }

        public void setIsFree(Boolean isFree) {
            this.isFree = isFree;
        }
    }
}
