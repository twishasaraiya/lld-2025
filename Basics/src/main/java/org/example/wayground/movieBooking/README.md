Functional Requirements :
- Able to search a movie, give a list of Movie Halls back.
- Able to search movies running in a Movie Hall X.
- Able to run same movie at different times, think of a new entity : Show
- Able to book a ticket for a Show  
What had to be done ?
- Create LLD diagrams
- Think row level locking when booking seats in a Show
- Create Services and Access Patterns

/* Entities
*
      * Movie
*  - id
*  - name
*  - List<Artist> (optional)
    *
    *
    *  Movie Hall
*    - id
*    - name
*    - location
*    - Map<String, List<Show>> movieShows
*
      *
      *  Screen
*      - id
*      - List<Show>
*
      *  Ticket
*      - showId
*      - List<Seat>
*      - screenId
*      - double cost
*
Seat
- id
- isAvailable

*  Show
*      - id
*      - movieId
*      - screenId
*      - Map<Integer, List<Seat>> rowBooking
*      - start
*      - end
*
      *
      * IMovieManager{
*     List<MovieHalls> searchMovie(name)
            *     List<Show> searchMovieInHall(name, movieHallId)
            *     String bookTicket(showId, List<Integer> seatIds)
            * }
*/