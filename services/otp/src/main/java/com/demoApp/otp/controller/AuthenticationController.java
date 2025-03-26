package com.demoApp.otp.controller;

import com.demoApp.otp.model.AuthRequest;
import com.demoApp.otp.service.OtpService;
import com.demoApp.otp.service.UserDetailsService;
import com.demoApp.otp.util.JwtUtil;
import com.demoApp.otp.entity.OtpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/client/auth/")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtTokenUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private OtpService otpService;

    @RequestMapping({"hello"})
    public String firstPage() {
        return "Hello World";
    }

    @RequestMapping(value = "requestOtp/{phoneNo}", method = RequestMethod.GET)
    public Map<String, Object> getOtp(@PathVariable String phoneNo) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            // Generate OTP (returns OtpEntity), then extract the OTP string.
            OtpEntity otpEntity = otpService.generateOtp(phoneNo);
            String otp = otpEntity.getOtp();
            
            returnMap.put("otp", otp);
            returnMap.put("status", "success");
            returnMap.put("message", "Otp sent successfully");
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }
        return returnMap;
    }

    @RequestMapping(value = "verifyOtp/", method = RequestMethod.POST)
    public Map<String, Object> verifyOtp(@RequestBody AuthRequest authenticationRequest) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            // Verify OTP by comparing with cached value
            String cachedOtp = otpService.getCacheOtp(authenticationRequest.getPhoneNo());
            if (authenticationRequest.getOtp().equals(cachedOtp)) {
                String jwtToken = createAuthenticationToken(authenticationRequest);
                returnMap.put("status", "success");
                returnMap.put("message", "Otp verified successfully");
                returnMap.put("jwt", jwtToken);
                otpService.clearOtp(authenticationRequest.getPhoneNo());
            } else {
                returnMap.put("status", "failed");
                returnMap.put("message", "Otp is either expired or incorrect");
            }
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }
        return returnMap;
    }

    // Create authentication token method
    public String createAuthenticationToken(AuthRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getPhoneNo(), "")
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getPhoneNo());
        return jwtTokenUtil.generateToken(userDetails);
    }
}
