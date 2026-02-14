package ru.savvy.soldo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.savvy.soldo.config.SecurityConfig;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.mapper.BookingMapper;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.security.JwtAuthenticationFilter;
import ru.savvy.soldo.security.JwtTokenProvider;
import ru.savvy.soldo.service.BookingService;
import ru.savvy.soldo.service.UserService;
import ru.savvy.soldo.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BookingMapper mapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken userAuth(String userId) {
        return new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private UsernamePasswordAuthenticationToken adminAuth() {
        return new UsernamePasswordAuthenticationToken(
                "1", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private Booking testBooking() {
        return Booking.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .event(Event.builder().id(1L).title("Test").build())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private BookingResponse testResponse() {
        return BookingResponse.builder()
                .id(1L)
                .userId(1L)
                .eventId(1L)
                .eventTitle("Test")
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Получение своих бронирований")
    void getMyBookings_success() throws Exception {
        when(bookingService.findAllUserBookings(1L))
                .thenReturn(List.of(testBooking()));
        when(mapper.entitiesToResponses(any()))
                .thenReturn(List.of(testResponse()));

        mockMvc.perform(get("/bookings")
                                .with(authentication(userAuth("1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventTitle").value("Test"));
    }

    @Test
    @DisplayName("Создание бронирования")
    void createBooking_success() throws Exception {
        User user = User.builder().id(1L).build();
        when(userService.findById(1L)).thenReturn(user);
        when(mapper.dtoToEntity(any())).thenReturn(testBooking());
        when(bookingService.createBooking(any())).thenReturn(testBooking());
        when(mapper.entityToResponse(any())).thenReturn(testResponse());

        BookingDTO dto = BookingDTO.builder()
                .eventId(1L)
                .status(BookingStatus.PENDING)
                .build();

        mockMvc.perform(post("/bookings")
                                .with(authentication(userAuth("1")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Подтверждение чужого бронирования — 403")
    void confirmOtherUserBooking_forbidden() throws Exception {
        Booking booking = testBooking(); // user.id = 1
        when(bookingService.findBookingById(1L)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1/confirm")
                                .with(authentication(userAuth("2")))) // другой пользователь
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Админ может подтвердить чужое бронирование")
    void adminConfirmOtherBooking_success() throws Exception {
        Booking booking = testBooking();
        when(bookingService.findBookingById(1L)).thenReturn(booking);
        when(bookingService.confirmBooking(any())).thenReturn(booking);
        when(mapper.entityToResponse(any())).thenReturn(testResponse());

        mockMvc.perform(patch("/bookings/1/confirm")
                                .with(authentication(adminAuth())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Без аутентификации — 401")
    void noAuth_unauthorized() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isUnauthorized());
    }
}