namespace java org.example.internal

struct InternalLeadDTO {
  1: string firstName,
  2: string lastName,
  3: double annualRevenue,
  4: string phone,
  5: string street,
  6: string postalCode,
  7: string city,
  8: string country,
  9: string creationDate, // ISO-8601
  10: string companyName,
  11: string state
}

service InternalCRM {
  list<InternalLeadDTO> findLeads(1: double lowAnnualRevenue, 2: double highAnnualRevenue, 3: string state),
  list<InternalLeadDTO> findLeadsByDate(1: string startDate, 2: string endDate),
  i64 createLead(1: InternalLeadDTO lead),
  void deleteLead(1: InternalLeadDTO leadDto)
}
