package com.coffee.app.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateProfileRequest(@Size(
   max = 255
) String familyName, @Size(
   max = 255
) String givenName, @Size(
   max = 32
) String phoneNumber, String gender, LocalDate dob, @Size(
   max = 256
) String profileImage, @Size(
   max = 256
) String coverImage) {
}
