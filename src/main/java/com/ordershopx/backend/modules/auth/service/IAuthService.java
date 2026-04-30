package com.ordershopx.backend.modules.auth.service;

import com.ordershopx.backend.modules.auth.dto.request.LoginRequestDTO;
import com.ordershopx.backend.modules.auth.dto.request.RegisterRequestDTO;
import com.ordershopx.backend.modules.auth.dto.response.LoginResponseDTO;
import com.ordershopx.backend.modules.auth.dto.response.RegisterResponseDTO;

public interface IAuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    RegisterResponseDTO register(RegisterRequestDTO request);;


}
