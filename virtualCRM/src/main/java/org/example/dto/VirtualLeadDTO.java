package org.example.dto;

public class VirtualLeadDTO {

    private String firstName;
    private String lastName;
    private String companyName;
    private Double annualRevenue;
    private String phone;
    private String street;
    private String postalCode;
    private String city;
    private String state;
    private String country;
    private String creationDate;
    private GeographicPointDTO geographicPoint; // latitude + longitude

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Double getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(Double annualRevenue) { this.annualRevenue = annualRevenue; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public GeographicPointDTO getGeographicPoint() { return geographicPoint; }
    public void setGeographicPoint(GeographicPointDTO geographicPoint) { this.geographicPoint = geographicPoint; }
}
