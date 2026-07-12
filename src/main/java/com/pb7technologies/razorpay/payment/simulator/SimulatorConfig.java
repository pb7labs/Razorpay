package com.pb7technologies.razorpay.payment.simulator;

import com.pb7technologies.razorpay.common.enums.ChaosMode;
import com.pb7technologies.razorpay.common.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
@Getter
@Setter
public class SimulatorConfig {

    private Integer pollIntervalMs = 2000;
    private ChaosMode chaosMode = ChaosMode.NORMAL;
    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    @Getter
    @Setter
    public static class MethodSimulatorConfig {
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;
    }

    public SimulatorConfig.MethodSimulatorConfig configFor(PaymentMethod paymentMethod){
        return methods.getOrDefault(paymentMethod.name(), new MethodSimulatorConfig());
    }
}
