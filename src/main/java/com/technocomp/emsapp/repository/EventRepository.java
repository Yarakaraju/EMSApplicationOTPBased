package com.technocomp.emsapp.repository;

import com.technocomp.emsapp.domain.Event;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    
    public Event findEventByDateOfEvent(Date date_of_event);

    public Iterable<Event> findEventsByEmail(String email);

    public Event findEventByEmailAndDateOfEvent(String email, Date date_of_event);

    public Event findEventByEventNameAndEmailAndDateOfEvent(String eventName, String email, Date dateOfEvent);
}
