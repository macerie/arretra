package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Sedi extends AbstractEntity {

    private String sede;
    private String indirizzo;
    private String cap;
    private String citta;
    private String prov;

    public String getSede() {
        return sede;
    }
    public void setSede(String sede) {
        this.sede = sede;
    }
    public String getIndirizzo() {
        return indirizzo;
    }
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
    public String getCap() {
        return cap;
    }
    public void setCap(String cap) {
        this.cap = cap;
    }
    public String getCitta() {
        return citta;
    }
    public void setCitta(String citta) {
        this.citta = citta;
    }
    public String getProv() {
        return prov;
    }
    public void setProv(String prov) {
        this.prov = prov;
    }

}
