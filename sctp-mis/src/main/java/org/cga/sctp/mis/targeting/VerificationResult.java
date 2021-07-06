package org.cga.sctp.mis.targeting;

public class VerificationResult {
    private String householdCode;
    private String householdHead;
    private String district;
    private String ta;
    private String gvhName;
    private String villageCluster;
    private String zone;
    private String printedNumber;
    private String idForm;
    private String status;
    private int ranking;

    public VerificationResult(String hhcode, String hhHead, String district, String ta, String gvh, String cluster, String zone, String printNum, String idForm, String status, int ranking) {
        this.householdCode = hhcode;
        this.householdHead = hhHead;
        this.district = district;
        this.ta = ta;
        this.gvhName = gvh;
        this.villageCluster = cluster;
        this.zone = zone;
        this.printedNumber = printNum;
        this.idForm = idForm;
        this.status = status;
        this.ranking = ranking;
    }

    public String getHouseholdCode() {
        return householdCode;
    }

    public void setHouseholdCode(String householdCode) {
        this.householdCode = householdCode;
    }

    public String getHouseholdHead() {
        return householdHead;
    }

    public void setHouseholdHead(String householdHead) {
        this.householdHead = householdHead;
    }

    public String getTa() {
        return ta;
    }

    public void setTa(String ta) {
        this.ta = ta;
    }

    public String getGvhName() {
        return gvhName;
    }

    public void setGvhName(String gvhName) {
        this.gvhName = gvhName;
    }

    public String getVillageCluster() {
        return villageCluster;
    }

    public void setVillageCluster(String villageCluster) {
        this.villageCluster = villageCluster;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPrintedNumber() {
        return printedNumber;
    }

    public void setPrintedNumber(String printedNumber) {
        this.printedNumber = printedNumber;
    }

    public String getIdForm() {
        return idForm;
    }

    public void setIdForm(String idForm) {
        this.idForm = idForm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
