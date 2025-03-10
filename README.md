# java_project

| HTTP_METHOD | ENDPOINT                                   | OPIS                                                      | IS_DONE |
|-------------|--------------------------------------------|-----------------------------------------------------------|---------|
| POST        | /accounts                                  | Tworzenie konta użytkownika                               |         |
| GET         | /accounts                                  | Pobieranie listy kont (paginacja, filtrowanie, queryPath)  |         |
| GET         | /accounts/{id}/details                     | Pobieranie szczegółów konta                                |         |
| GET         | /tickets/{id}                              | Pobieranie biletu                                          |         |
| POST        | /tickets                                   | Kupowanie biletu                                           |         |
| GET         | /events                                    | Przeglądanie wydarzeń (paginacja, filtrowanie, queryPath)  |         |
| GET         | /users/{id}/wallet                         | Pobranie informacji o portfelu                             |         |
| POST        | /users/{id}/wallet                         | Dodanie środków do portfela                                |         |
| GET         | /tickets/{id}/details                      | Pobierz bilet w formacie PDF wraz z QR                      |         |
| GET         | /faqs                                      | Pobranie listy FAQ                                          |         |
| GET         | /faqs/{id}                                 | Pobranie szczegółów FAQ                                     |         |
| GET         | /users                                     | Pobranie listy użytkowników                                |         |
| GET         | /users/{id}/transactions                   | Pobranie historii transakcji użytkownika                    |         |
| GET         | /users/{id}/details                        | Pobranie szczegółów użytkownika                             |         |
| POST        | /events                                    | Tworzenie wydarzenia przez organizatora                    |         |
| GET         | /events/{id}/details                       | Podgląd szczegółów wydarzenia                              |         |
| GET         | /buildings                                 | Pobranie listy budynków                                    |         |
| GET         | /buildings/{id}/details                    | Szczegóły budynku                                          |         |
| POST        | /buildings                                 | Prośba o utworzenie nowego obiektu                         |         |
| GET         | /buildings?approved=false                  | Pobranie planów budynków do zatwierdzenia                  |         |
| PATCH       | /buildings/{id}                            | Zatwierdzenie planu budynku (pole approved)                |         |
| GET         | /users                                     | Zarządzanie użytkownikami (admin)                          |         |
| GET         | /organisers                                | Zarządzanie organizatorami (admin)                         |         |
| POST        | /users                                     | Dodanie użytkownika                                        |         |
| DELETE      | /users/{id}                                | Usunięcie użytkownika                                      |         |
| PUT/PATCH   | /users/{id}                                | Aktualizacja danych użytkownika                            |         |
| GET         | /stats                                     | Wgląd w statystyki                                         |         |
| DELETE      | /events/{id}                               | Usunięcie wydarzenia (admin)                               |         |
| POST        | /events/{id}                               | Dodanie wydarzenia (admin)                                 |         |
| PUT         | /events/{id}                               | Aktualizacja wydarzenia (admin)                            |         |
| POST        | /faqs                                      | Dodanie FAQ (admin)                                        |         |
| DELETE      | /faqs/{id}                                 | Usunięcie FAQ (admin)                                      |         |
| PUT         | /faqs/{id}                                 | Aktualizacja FAQ (admin)                                   |         |

