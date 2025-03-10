# PassGo

## Endpoints for user

| HTTP_METHOD | ENDPOINT                     | OPIS                                                  |
|------------|------------------------------|------------------------------------------------------|
| POST       | /accounts                    | Tworzenie konta użytkownika                         |
| GET        | /accounts                    | Pobieranie listy kont (paginacja, filtrowanie)      |
| GET        | /accounts/{id}/details       | Pobieranie szczegółów konta                         |
| GET        | /tickets/{id}                | Pobieranie biletu                                   |
| POST       | /tickets                     | Kupowanie biletu                                   |
| GET        | /events                      | Przeglądanie wydarzeń (paginacja, filtrowanie)     |
| GET        | /users/{id}/wallet           | Pobranie informacji o portfelu                     |
| POST       | /users/{id}/wallet           | Dodanie środków do portfela                        |
| GET        | /tickets/{id}/details        | Pobranie biletu w formacie PDF z QR                |
| GET        | /faqs                        | Pobranie listy FAQ                                 |
| GET        | /faqs/{id}                   | Pobranie szczegółów FAQ                            |
| GET        | /users/{id}/transactions     | Pobranie historii transakcji użytkownika          |
| GET        | /users/{id}/details          | Pobranie szczegółów użytkownika                   |

## Endpoints for the organizer

| HTTP_METHOD | ENDPOINT                    | OPIS                                             |
|------------|-----------------------------|-------------------------------------------------|
| POST       | /events                      | Tworzenie wydarzenia                            |
| GET        | /events/{id}/details        | Podgląd szczegółów wydarzenia                  |
| GET        | /buildings                   | Pobranie listy budynków                         |
| GET        | /buildings/{id}/details      | Szczegóły budynku                               |
| POST       | /buildings                   | Prośba o utworzenie nowego obiektu             |

## Endpoints for administrator

| HTTP_METHOD | ENDPOINT                     | OPIS                                               |
|------------|------------------------------|---------------------------------------------------|
| GET        | /buildings?approved=false    | Pobranie planów budynków do zatwierdzenia        |
| PATCH      | /buildings/{id}              | Zatwierdzenie planu budynku (pole approved)      |
| GET        | /users                       | Zarządzanie użytkownikami                        |
| GET        | /organisers                   | Zarządzanie organizatorami                       |
| POST       | /users                        | Dodanie użytkownika                              |
| DELETE     | /users/{id}                   | Usunięcie użytkownika                            |
| PUT/PATCH  | /users/{id}                   | Aktualizacja danych użytkownika                  |
| GET        | /stats                        | Wgląd w statystyki                               |
| DELETE     | /events/{id}                   | Usunięcie wydarzenia                            |
| POST       | /events/{id}                   | Dodanie wydarzenia                              |
| PUT        | /events/{id}                   | Aktualizacja wydarzenia                         |
| POST       | /faqs                         | Dodanie FAQ                                     |
| DELETE     | /faqs/{id}                     | Usunięcie FAQ                                   |
| PUT        | /faqs/{id}                     | Aktualizacja FAQ                               |
