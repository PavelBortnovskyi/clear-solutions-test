package com.neo.controller;

import com.neo.dto.rq.UserDTOrq;
import com.neo.dto.rs.ApiResponse;
import com.neo.dto.rs.UserDTOrs;
import com.neo.marker.Marker;
import com.neo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Validated({Marker.NewOrUpdate.class})
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTOrs>> createUser(@RequestBody @Valid UserDTOrq userDTOrq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(userService.createUser(userDTOrq)));
    }

    @Validated({Marker.NewOrUpdate.class})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTOrs>> updateUser(@RequestBody @Valid UserDTOrq userDTOrq, @PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.updateUser(userId, userDTOrq)));
    }

    @Validated({Marker.PartialUpdate.class})
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTOrs>> patchUser(@RequestBody @Valid UserDTOrq userDTOrq, @PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(userService.patchUser(userId, userDTOrq)));
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@PathVariable(name = "id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTOrs>>> searchUsersByBirthDateRange(@RequestParam("from") LocalDate fromDate,
                                                                                    @RequestParam("to") LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<UserDTOrs> users = userService.findUsersByBirthDateInRange(fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(users));
    }
}
