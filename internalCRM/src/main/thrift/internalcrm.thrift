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
  9: string creationDate, // Format ISO-8601 : "yyyy-MM-dd'T'HH:mm:ss'Z'" (ex: "2024-09-15T10:00:00Z")
  10: string companyName,
  11: string state
}

// Interface du service InternalCRM (section 2.2 de l'enonce)
service InternalCRM {
  // Recherche par fourchette de revenus et region optionnelle
  list<InternalLeadDTO> findLeads(1: double lowAnnualRevenue, 2: double highAnnualRevenue, 3: string state),
  
  // Recherche par intervalle de dates (format ISO-8601)
  list<InternalLeadDTO> findLeadsByDate(1: string startDate, 2: string endDate),
  
  // Creation d'un prospect, retourne l'ID genere (i64 = entier 64 bits)
  i64 createLead(1: InternalLeadDTO lead),
  
  // Suppression d'un prospect par template (correspondance exacte)
  void deleteLead(1: InternalLeadDTO leadDto)
}

// Exceptions métier
exception ThriftNoSuchLeadException {
  1: string message
}

exception ThriftWrongDateFormatException {
  1: string message
}

exception ThriftWrongOrderForDateException {
  1: string message
}

exception ThriftWrongOrderForRevenueException {
  1: string message
}

exception ThriftWrongStateException {
  1: string message
}
