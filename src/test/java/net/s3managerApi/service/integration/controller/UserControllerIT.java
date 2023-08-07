package net.s3managerApi.service.integration.controller;

import lombok.RequiredArgsConstructor;
import net.s3managerApi.service.integration.IntegrationTestBase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class UserControllerIT extends IntegrationTestBase {

    private final MockMvc mockMvc;


}
