package com.example.courtreservation.controller;

import com.example.courtreservation.entity.KindOfGame;
import com.example.courtreservation.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@RestController
@RequestMapping("api/res")
public class ReservationController {
    private EntityManagerFactory entityManagerFactory  = createEntityManagerFactory("reservations");

    @GetMapping("/all-res-list") // read
    public List<Reservation> getAllReservationsList() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query q = entityManager.createQuery("from Reservation", Reservation.class);
        return q.getResultList();
    }

    @PostMapping("/create-res/{courtId}/{phoneNum}") // create
    public Reservation createReservation(@PathVariable int courtId, @PathVariable String phoneNum, @RequestBody Reservation res) {
        return inTransaction(em -> {
            Court c = em.find(Court.class, courtId);

            //check existence of user
            MyUser myUser = new MyUser();
            List<MyUser> listOfUsers = em.createQuery("select m from MyUser m where m.phoneNumber like :pn", MyUser.class).setParameter("pn", phoneNum).getResultList();
            if (listOfUsers.isEmpty()){
                myUser.setPhoneNumber(phoneNum);
                em.persist(myUser);
            } else {
                myUser = listOfUsers.get(0);
            }
            //check if court is available
            List<Reservation> reservationsWithStartBeforeDesiredEnd =
                    em.createQuery("select r from Reservation r where r.startDate < :desiredEnd and r.endDate > :desiredStart")
                            .setParameter("desiredEnd", res.getEndDate())
                            .setParameter("desiredStart", res.getStartDate())
                            .getResultList();
            reservationsWithStartBeforeDesiredEnd.stream().forEach(
                    (Reservation r) -> {
                        if (r.getCourt().getId() == courtId) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Colliding interval, already exists");
                        }
                    }
            );

            /*LocalDateTime startDate = res.getStartDate();
            LocalDateTime endDate = res.getEndDate();
            Duration duration = Duration.between(startDate, endDate);
            long durationInMinutes = duration.toMinutes();
            if (durationInMinutes <= 0 || (Duration.between(LocalDateTime.now(), startDate).toMinutes() < 0)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrectly set start and end interval");
            }
            // total cost
            //surface of desired court
            long totalCost = c.getCs().getCostInMinutes()*durationInMinutes;
            if (res.getKindOfGame() == KindOfGame.FOUR_PLAYERS) {
                totalCost *= 1.5;
            }*/

            res.setCourt(c);
            res.setMyUser(myUser);
            res.setTotalCost(getTotalCost(res, c));
            em.persist(res);
            return res;
        });
    }

    @GetMapping("/all-res-for-court/{courtId}") // read
    public List<Reservation> getCourtReservations(@PathVariable int courtId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createQuery("select res from Reservation res INNER JOIN res.court court where court.id= :courtId order by res.createdOn", Reservation.class)
                .setParameter("courtId", courtId);
        return q.getResultList();
    }

    @GetMapping("/all-res-for-num/{pnum}") // read
    public List<Reservation> getReservationsForPhoneNumber(@PathVariable String pnum) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createQuery("select res from Reservation res inner join res.myUser where res.myUser.phoneNumber= :pnum")
                .setParameter("pnum", pnum);
        return q.getResultList();
    }

    //reservations for number in future/all
    @GetMapping("/all-res-for-num-future/{pnum}") // read
    public List<Reservation> getReservationsForPhoneNumberInFuture(@PathVariable String pnum) {
        EntityManager em = entityManagerFactory.createEntityManager();
        LocalDateTime now = LocalDateTime.now();
        Query q = em.createQuery("select res from Reservation res inner join res.myUser where res.myUser.phoneNumber= :pnum and res.startDate > :now", Reservation.class)
                .setParameter("pnum", pnum)
                .setParameter("now", now);
        return q.getResultList();
    }

    //update
    @PutMapping("/update/{courtId}/{phoneNum}")
    public Reservation updateReservation(@PathVariable int courtId, @PathVariable String phoneNum, @RequestBody Reservation res) {
        return inTransaction(em -> {
            Reservation r = em.find(Reservation.class, res.getId());
            if (r == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation not found");
            }
            Court c = em.find(Court.class, courtId);
            if (c == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court not found");
            }
            r.setCourt(c);
            Query q = em.createQuery("select myuser from MyUser myuser where myuser.phoneNumber like :pnum", MyUser.class)
                .setParameter("pnum", phoneNum);
            if (q.getResultList().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given phoneNumber not found");
            }
            List<MyUser> l = q.getResultList();
            r.setMyUser(l.get(0));
            r.setKindOfGame(res.getKindOfGame());
            r.setStartDate(res.getStartDate());
            r.setEndDate(res.getEndDate());
            r.setTotalCost(getTotalCost(r, c));
            em.merge(r);
            return r;
        });
    }

    //delete
    @DeleteMapping("/delete/{resId}") // delete
    public void deleteReservation(@PathVariable int resId) {
        inTransaction(em -> {
            em.remove(em.find(Reservation.class, resId));
            return null;
        });
    }

    private long getTotalCost(Reservation res, Court c){
        //verify interval - start < end, start > now
        LocalDateTime startDate = res.getStartDate();
        LocalDateTime endDate = res.getEndDate();
        Duration duration = Duration.between(startDate, endDate);
        long durationInMinutes = duration.toMinutes();
        if (durationInMinutes <= 0 || (Duration.between(LocalDateTime.now(), startDate).toMinutes() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrectly set start and end interval");
        }
        //total cost
        long totalCost = c.getCs().getCostInMinutes()* durationInMinutes;
        if (res.getKindOfGame() == KindOfGame.FOUR_PLAYERS) {
            totalCost *= 1.5;
        }
        return totalCost;
    }

    Reservation inTransaction(Function<EntityManager, Reservation> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        Reservation reservation;
        try {
            transaction.begin();
            reservation = work.apply(entityManager);
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
        return reservation;
    }
}
