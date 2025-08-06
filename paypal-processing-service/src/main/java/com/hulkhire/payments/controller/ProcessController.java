package com.hulkhire.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhire.payments.service.ReconService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/paypal")
public class ProcessController {
	
	private final ReconService reconService;

    @GetMapping("/process")
    public String processPayment() {
        return "Payment processed successfully";
    }
    @PostMapping("/recon")
    public String triggerRecon() {
		// Logic to trigger reconciliation
    	log.info("ProcessController.triggerRecon() called");
    			reconService.reconTransactions();
		return "Reconciliation triggered";
	}
}
