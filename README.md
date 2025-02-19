Ο κωδικος για ολους τους χρηστες που εχω προσθεσει ενδεικτικα ειναι "123". πχ. username:thanos1 password:123, username:owner1 password:123, username:owner2 password:123, username:admin1 password:123

Οι κωδικοι για database ειναι ηδη στο application.properties: 
spring.datasource.username=fakebnbuser 
spring.datasource.password=veaFgbuIfLxnQLjxPvXHU1yWklaEx1hZ 
spring.datasource.url=jdbc:postgresql://dpg-cuoe6m52ng1s73e821a0-a.oregon-postgres.render.com:5432/fakebnb_n6j2

Δεν ξερω αν πρεπει να σχολιασω καθε σημειο, κλαση, μεθοδο του project.

Οποτε θα γραψω λιγα πραγματα για αυτα που θεωρω  οτι αξιζουν τα υποθουν:
- Owner και Tenant επεκτεινουν την κλαση User και διακρινονται με @DiscriminatorValue
- Χρησιμοποιησα το username ως primary key αντι για ID επειδη το θεωρησα καλυτερο
- Το Property εχει σχεση @ManytoOne με Owner ωστε καθε ιδιοκτητης να μπορει να καταχωρησει πανω απο 1 ιδιοκτησια και ManyToMany με Tenants ωστε καθε ιδιοκτησια να μπορει να εχει περισσοτερους ενοικιαστες
- Σε πολλα σημεια εχουμε @Secured για να προστατευθουν απο την προσβαση χρηστων χωρις καταλληλο ρολο (εκτος του SecurityConfig).
- Γενικα θεωρω ειναι σχετικα ευδιακριτος ο κωδικας με καλες ονομασιες και οχι πολυ περιεργος.
