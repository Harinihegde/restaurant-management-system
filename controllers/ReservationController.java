package com.restaurant.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationModule reservationModule;

    public ReservationController(ReservationModule reservationModule) {
        this.reservationModule = reservationModule;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("reservations", reservationModule.searchReservations(keyword));
        model.addAttribute("reservation", new ReservationEntry());
        model.addAttribute("keyword", keyword);
        return "reservations";
    }

    @GetMapping("/save")
    public String save(@RequestParam String guestName,
                       @RequestParam Integer guestCount,
                       @RequestParam String reservationTime,
                       RedirectAttributes redirectAttributes) {
        ReservationEntry reservation = new ReservationEntry();
        reservation.setGuestName(guestName);
        reservation.setGuestCount(guestCount);
        reservation.setReservationTime(LocalDateTime.parse(reservationTime));
        reservationModule.createReservation(reservation);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation created and notification commands executed.");
        return "redirect:/reservations";
    }
}

@Component
class ReservationModule {

    private final List<ReservationEntry> reservations = new ArrayList<>();
    private final AtomicLong reservationIds = new AtomicLong(1);
    private final NotificationCommandInvoker notificationInvoker;

    ReservationModule(NotificationCommandInvoker notificationInvoker) {
        this.notificationInvoker = notificationInvoker;
        ReservationEntry sample = new ReservationEntry();
        sample.setId(reservationIds.getAndIncrement());
        sample.setGuestName("Diya Shah");
        sample.setGuestCount(4);
        sample.setReservationTime(LocalDateTime.now().plusDays(1));
        sample.setStatus("CONFIRMED");
        reservations.add(sample);
    }

    List<ReservationEntry> searchReservations(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(reservations);
        }
        String normalized = keyword.toLowerCase();
        return reservations.stream()
                .filter(reservation -> reservation.getStatus().toLowerCase().contains(normalized))
                .toList();
    }

    ReservationEntry createReservation(ReservationEntry reservation) {
        reservation.setId(reservationIds.getAndIncrement());
        reservation.setStatus("CONFIRMED");
        if (reservation.getReservationTime() == null) {
            reservation.setReservationTime(LocalDateTime.now().plusHours(2));
        }
        reservations.add(reservation);
        notificationInvoker.runAll(reservation);
        return reservation;
    }
}

interface ReservationCommand {
    void execute(ReservationEntry reservation);
}

@Component
class NotificationCommandInvoker {

    private final List<ReservationCommand> commands;

    NotificationCommandInvoker(List<ReservationCommand> commands) {
        this.commands = commands;
    }

    void runAll(ReservationEntry reservation) {
        commands.forEach(command -> command.execute(reservation));
    }
}

@Component
@Order(1)
class EmailNotificationCommand implements ReservationCommand {

    @Override
    public void execute(ReservationEntry reservation) {
        System.out.println("Email sent for reservation #" + reservation.getId() + " for " + reservation.getGuestName());
    }
}

@Component
@Order(2)
class SMSNotificationCommand implements ReservationCommand {

    @Override
    public void execute(ReservationEntry reservation) {
        System.out.println("SMS sent for reservation #" + reservation.getId() + " for " + reservation.getGuestName());
    }
}

class ReservationEntry {

    private Long id;
    private String guestName = "";
    private Integer guestCount = 1;
    private LocalDateTime reservationTime;
    private String status = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
