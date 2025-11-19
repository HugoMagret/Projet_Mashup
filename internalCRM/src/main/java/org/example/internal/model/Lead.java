package org.example.internal.model;

import java.util.Calendar;
import java.util.Objects;

/**
 * Entité métier représentant un prospect (Lead) dans le CRM interne.
 * 
 * Cette classe stocke toutes les informations d'un client potentiel :
 * - Identité : prénom, nom, entreprise
 * - Contact : téléphone, adresse complète
 * - Commercial : revenu annuel estimé, région
 * - Date de création : stockée en Calendar (conversion ISO faite par ConverterUtils)
 * 
 * IMPORTANT : Les égalités (equals/hashCode) se basent UNIQUEMENT sur l'ID.
 * Deux leads avec le même ID sont considérés identiques, même si leurs données diffèrent.
 */
public class Lead {
    private Long id;
    private String firstName;
    private String lastName;
    private double annualRevenue;
    private String phone;
    private String street;
    private String postalCode;
    private String city;
    private String country;
    private Calendar creationDate; // utilisation de Calendar pour la date
    private String companyName;
    private String state;

    public Lead() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public double getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(double annualRevenue) { this.annualRevenue = annualRevenue; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Calendar getCreationDate() { return creationDate; }
    public void setCreationDate(Calendar creationDate) { this.creationDate = creationDate; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
