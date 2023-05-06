package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    @Column(name = "start_time")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    @Column(name = "end_time")
    private LocalDateTime end;
//    @OneToOne(fetch = FetchType.LAZY)
    @OneToOne
    @JoinColumn(name = "item_id")
//    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private Item item;
//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "booker_id")
//    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}