package com.amazon.seller.devopsportal.dto.environment;

import java.util.List;

public record EnvironmentResponse(Long id, String name, boolean development, boolean testing, boolean staging,
                                  boolean production, List<Long> serviceIds) {
}
