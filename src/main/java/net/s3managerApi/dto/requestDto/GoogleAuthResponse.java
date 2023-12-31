package net.s3managerApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAuthResponse {

    private String success;

    private String userAuthenticatedSuccessfully;
}
