package com.example.courtreservation.controller;

import com.example.courtreservation.entity.Court;
import com.example.courtreservation.entity.CourtSurface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@RestController
@RequestMapping("api/court")
public class CourtRestController {
    private EntityManagerFactory entityManagerFactory  = createEntityManagerFactory("courts");
    @GetMapping("/all-courts-list") // read
    public List<Court> getAllCourtsList() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query q = entityManager.createQuery("from Court", Court.class);
        return q.getResultList();
    }

    @PostMapping("/create-court-with-surface/{surfaceId}") // create
    public Court createCourt(@PathVariable int surfaceId, @RequestBody Court court) {
        return inTransaction(em -> {
            CourtSurface cs = em.find(CourtSurface.class, surfaceId);
            if (cs == null) {
                throw new RuntimeException("CourtSurface id not found  :" + surfaceId);
            }
            List<Court> list = em.createQuery("from Court", Court.class).getResultList();
            list.stream().forEach(e -> System.out.println(e));
            int maxCourtId = em.createQuery("select max(id) as max from Court c", Integer.class).getResultList().get(0);
            Court court2 = new Court();
            court2.setCs(cs);
            court2.setId(++maxCourtId);
            court2.setDescription(court.getDescription());
            em.persist(court2);
            return court2;
        });
    }

    @GetMapping("/single-court-view/{courtId}") // read
    public Court getSingleCourtView(@PathVariable int courtId) {
        return inTransaction(em -> {return em.find(Court.class, courtId);});
    }

    @DeleteMapping("/delete/{courtId}") // delete
    public void deleteCourt(@PathVariable int courtId) {
        inTransaction(em -> {
            em.remove(em.find(Court.class, courtId));
            return null;
        });
    }

    @PutMapping("/update/{surfaceId}")
    public Court updateCourt(@PathVariable int surfaceId, @RequestBody Court court) {
        return inTransaction(em -> {
            CourtSurface cs = em.find(CourtSurface.class, surfaceId);
            Court c = em.find(Court.class, court.getId());
            c.setCs(cs);
            c.setDescription(court.getDescription());
            em.merge(c);
            return c;
        });
    }


    Court inTransaction(Function<EntityManager, Court> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        Court c;
        try {
            transaction.begin();
            c = work.apply(entityManager);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        finally {
            entityManager.close();
        }
        return c;
    }
}
