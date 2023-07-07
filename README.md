###Zadanie rekrutacyjne backend

Stwórz RESTowy serwis, który zwróci informacje
* Identyfikator
* Login
* Nazwa
* Typ
* Url do avatara
* Data stworzenia
* Obliczenia

API serwisu powinno wyglądać jak poniżej:
```
GET /users/{login}
{
"id": "...",
"login": "...",
"name": "…",
"type": "...",
"avatarUrl": „”,
"createdAt": "..."
"calculations": "..."
}
```

Serwis powinien pobrać dane z https://api.github.com/users/{login} (np.
https://api.github.com/users/octocat) i przekazać je w API. W polu calculations powinien być
zwrócony wynik działania 6 / liczba_followers * (2 + liczba_public_repos).

Serwis powinien zapisywać w bazie danych liczbę wywołań API dla każdego loginu.
Baza danych powinna zawierać dwie kolumny: LOGIN oraz REQUEST_COUNT. Dla każdego wywołania
usługi wartość REQUEST_COUNT powinna być zwiększana o jeden.

Serwis powinien być zaimplementowany w Java. Projekt powinien być możliwy do zbudowania za
pomocą Maven lub Gradle. Możesz wspierać się dowolnymi, łatwo dostępnymi technologiami (silniki
BD, biblioteki, frameworki).

Pamiętaj o zastosowaniu dobrych praktyk programowania.
Projekt umieść na dowolnym repozytorium i udostępnij nam link.