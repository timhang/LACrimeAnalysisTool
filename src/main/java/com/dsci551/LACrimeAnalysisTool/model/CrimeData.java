package com.dsci551.LACrimeAnalysisTool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CrimeData")
public class CrimeData {

    public String getDrNo() {
        return drNo;
    }

    public void setDrNo(String drNo) {
        this.drNo = drNo;
    }

    @Id
    @Column(length = 20)
    private String drNo;

    @Column(nullable = false)
    private Date dateRptd;

    @Column(nullable = false)
    private Date dateOcc;

    private Integer timeOcc;
    private Integer area;

    @Column(columnDefinition = "TEXT")
    private String areaName;

    @Column(columnDefinition = "TEXT")
    private String rptDistNo;

    @Column(columnDefinition = "TEXT")
    private String crmCd;

    @Column(columnDefinition = "TEXT")
    private String crmCdDesc;

    private Integer victAge;

    @Column(columnDefinition = "TEXT")
    private String victSex;

    @Column(columnDefinition = "TEXT")
    private String victDescent;

    private Integer premisCd;

    @Column(columnDefinition = "TEXT")
    private String premisDesc;

    @Column(columnDefinition = "TEXT")
    private String weaponUsedCd;

    @Column(columnDefinition = "TEXT")
    private String weaponDesc;

    @Column(columnDefinition = "TEXT")
    private String status;

    public Date getDateRptd() {
        return dateRptd;
    }

    public void setDateRptd(Date dateRptd) {
        this.dateRptd = dateRptd;
    }

    public Date getDateOcc() {
        return dateOcc;
    }

    public void setDateOcc(Date dateOcc) {
        this.dateOcc = dateOcc;
    }

    public Integer getTimeOcc() {
        return timeOcc;
    }

    public void setTimeOcc(Integer timeOcc) {
        this.timeOcc = timeOcc;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRptDistNo() {
        return rptDistNo;
    }

    public void setRptDistNo(String rptDistNo) {
        this.rptDistNo = rptDistNo;
    }

    public String getCrmCd() {
        return crmCd;
    }

    public void setCrmCd(String crmCd) {
        this.crmCd = crmCd;
    }

    public String getCrmCdDesc() {
        return crmCdDesc;
    }

    public void setCrmCdDesc(String crmCdDesc) {
        this.crmCdDesc = crmCdDesc;
    }

    public Integer getVictAge() {
        return victAge;
    }

    public void setVictAge(Integer victAge) {
        this.victAge = victAge;
    }

    public String getVictSex() {
        return victSex;
    }

    public void setVictSex(String victSex) {
        this.victSex = victSex;
    }

    public String getVictDescent() {
        return victDescent;
    }

    public void setVictDescent(String victDescent) {
        this.victDescent = victDescent;
    }

    public Integer getPremisCd() {
        return premisCd;
    }

    public void setPremisCd(Integer premisCd) {
        this.premisCd = premisCd;
    }

    public String getPremisDesc() {
        return premisDesc;
    }

    public void setPremisDesc(String premisDesc) {
        this.premisDesc = premisDesc;
    }

    public String getWeaponUsedCd() {
        return weaponUsedCd;
    }

    public void setWeaponUsedCd(String weaponUsedCd) {
        this.weaponUsedCd = weaponUsedCd;
    }

    public String getWeaponDesc() {
        return weaponDesc;
    }

    public void setWeaponDesc(String weaponDesc) {
        this.weaponDesc = weaponDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    @Column(columnDefinition = "TEXT")
    private String statusDesc;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(columnDefinition = "TEXT")
    private String crossStreet;

    private Float lat;
    private Float lon;

    public Integer getViolenceLevel() {
        return violenceLevel;
    }

    public void setViolenceLevel(int violenceLevel) {
        this.violenceLevel = violenceLevel;
    }

    private Integer violenceLevel;

    // Getters and setters
    // Note: For the Date fields, you might consider using @Temporal annotations to specify the DATE or TIMESTAMP type
    // However, starting with JPA 2.2, java.time.LocalDate or java.time.LocalDateTime is preferred over java.util.Date
}

