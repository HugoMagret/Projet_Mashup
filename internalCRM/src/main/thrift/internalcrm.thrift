namespace java org.example.internal

struct InternalLeadTO {
  1: i64 id,
  2: string fullname,      // "Last, First" dans ce service
  3: string company,
  4: double annualRevenue,
  5: string phone,
  6: string address,
  7: string postalCode,
  8: string city,
  9: string province,
  10: string country,
  11: string registrationDate // ISO-8601
}

service InternalCRM {
  list<InternalLeadTO> findLeads(1: double lowAnnualRevenue, 2: double highAnnualRevenue, 3: string province),
  list<InternalLeadTO> findLeadsByDate(1: string fromIso, 2: string toIso),
  i64 createLead(1: InternalLeadTO lead),
  void deleteLead(1: i64 id)
}
