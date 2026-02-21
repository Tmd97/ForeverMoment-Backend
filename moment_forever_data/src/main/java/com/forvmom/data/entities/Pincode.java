package com.forvmom.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "pincode")
@SQLDelete(sql = "UPDATE pincode SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Pincode extends NamedEntity {

    @Column(name = "pincode_code", nullable = false)
    private String pincodeCode;

    @Column(name = "area_name")
    private String areaName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public String getPincodeCode() {
        return pincodeCode;
    }

    public void setPincodeCode(String pincodeCode) {
        this.pincodeCode = pincodeCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
